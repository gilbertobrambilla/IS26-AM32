package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.game.models.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyErrorEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyStateUpdatedEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.model.GameResult;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.enums.LobbyErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;
import it.polimi.ingsw.am32.model.exceptions.InvalidLobbyActionException;
import it.polimi.ingsw.am32.model.receipts.*;
import it.polimi.ingsw.am32.model.tiles.OfferTile;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

/**
 * Controller class responsible for managing the flow of a single game instance.
 * It handles client commands, updates the game state, and pushes updates to the views.
 */
public class GameController implements Runnable {
    private final int gameId;
    private final GameState gameState;
    private final GameLifecycleCallback lifecycleCallback;
    private final Map<String, PlayerSession> activeClients;
    private final BlockingQueue<PrioritizedTask> commandQueue;
    private boolean isSuspended;
    private boolean started;
    private final Object suspendLock = new Object();
    private static final long FORFEIT_TIMEOUT_MS = 60000;

    /**
     * Constructs a new GameController for a specific game session.
     *
     * @param gameId the unique identifier for this game
     * @param gameState the model representing the state of the game
     * @param lifecycleCallback the callback interface to notify the lobby of game lifecycle events
     */
    public GameController(int gameId, GameState gameState, GameLifecycleCallback lifecycleCallback) {
        this.gameId = gameId;
        this.gameState = gameState;
        this.lifecycleCallback = lifecycleCallback;
        this.activeClients = new ConcurrentHashMap<>();
        this.commandQueue = new PriorityBlockingQueue<>();
        this.isSuspended = false;
        this.started = false;
    }

    /**
     * Broadcasts the current waiting room state to all players inside this game.
     */
    public void broadcastWaitingRoomState() {
        // lobby info dto is built
        LobbyInfoDTO infoDTO = new LobbyInfoDTO(
                this.gameId,
                this.getCurrentPlayersCount(),
                getPlayersNicknames(),
                getTargetPlayersCount()
        );

        // waiting room event is built with the info dto
        LobbyStateUpdatedEvent eventDTO = new LobbyStateUpdatedEvent(infoDTO);

        // event is broadcasted to all active clients
        for (PlayerSession session : this.activeClients.values()) {
            session.getView().notifyLobbyRoomUpdated(eventDTO);
        }
    }

    /**
     * Adds a newly connected player to the game state and active client list.
     *
     * @param nickname The unique nickname of the player.
     * @param view     The virtual view handling the connection to the player's client.
     * @param player   The player model instance.
     * @param onSuccess The callback to execute after the player is added.
     */
    void addPlayerToGame(String nickname, GameView view, Player player, Runnable onSuccess) {
        // player is added to active clients and model
        this.activeClients.put(nickname, new PlayerSession(player, view));
        this.gameState.addPlayer(player);

        // success callback is executed
        if (onSuccess != null) {
            onSuccess.run();
        }
    }

    /**
     * Processes a player disconnection before the game has started.
     *
     * @param nickname     The nickname of the disconnected player.
     * @param connectionID The unique identifier of the connection that dropped.
     * @param onComplete   The callback to execute after the disconnection is processed.
     */
    public void processPregameDisconnect(String nickname, String connectionID, Runnable onComplete) {
        // active session is retrieved safely
        PlayerSession activeSession = this.activeClients.get(nickname);

        // connection identity is verified to prevent ghost disconnects
        if (activeSession != null && activeSession.getView().getConnectionID().equals(connectionID)) {
            // player session is removed
            this.activeClients.remove(nickname);
            // player is removed from game state via model method
            this.gameState.removePlayer(nickname);

            // updated waiting room state is broadcasted
            broadcastWaitingRoomState();

            // menu update callback is triggered
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    /**
     * Processes a join request from the lobby asynchronously.
     *
     * @param nickname The nickname of the joining player.
     * @param view The virtual view of the player.
     * @param onSuccess The callback to execute if the join is successful.
     */
    public void processJoinRequest(String nickname, VirtualView view, Runnable onSuccess) {
        if (this.getCurrentPlayersCount() < this.getTargetPlayersCount() && !this.isStarted()) {
            // player is instantiated
            Player player = new Player(nickname);

            // player is added to active clients and model
            this.activeClients.put(nickname, new PlayerSession(player, view));

            try {
                // player is added to the game model
                this.gameState.addPlayer(player);
            } catch (InvalidLobbyActionException e) {
                // session is removed due to domain logic failure
                this.activeClients.remove(nickname);
                // lobby error is notified
                view.notifyLobbyError(new LobbyErrorEvent(e.getReason(), e.getMessage()));
                return;
            } catch (RuntimeException e) {
                // exception is logged with stack trace
                LogManager.getInstance().log(
                        LogLevel.ERROR,
                        "[GameController - processJoinRequest] critical error during join request for player " + nickname,
                        e
                );
                // session is removed due to unexpected failure
                this.activeClients.remove(nickname);
                // internal error is notified
                view.notifyLobbyError(new LobbyErrorEvent(LobbyErrorReason.JOIN_FAILED, "internal error during join request"));
                return;
            }

            // success callback is executed
            if (onSuccess != null) {
                onSuccess.run();
            }

            // waiting room state is broadcasted
            this.broadcastWaitingRoomState();

            // game initialization is triggered if capacity is reached
            if (this.getCurrentPlayersCount() == this.getTargetPlayersCount()) {
                this.startGame();
            }
        } else {
            // error is notified if game is full or started
            view.notifyLobbyError(new LobbyErrorEvent(LobbyErrorReason.GAME_FULL_OR_STARTED, "game full or already started"));
        }
    }

    /**
     * Notifies the specific client that their requested action was denied due to the game being suspended.
     *
     * @param nickname the nickname of the player issuing the command.
     */
    private void notifySuspendedActionDenied(String nickname) {
        PlayerSession session = this.activeClients.get(nickname);
        if (session != null) {
            // error dto is built
            GameErrorEvent errorDTO = new GameErrorEvent(
                    nickname,
                    GameErrorReason.ACTION_DENIED_GAME_SUSPENDED,
                    "action denied because the game is currently suspended"
            );
            // view is notified
            session.getView().notifyError(errorDTO);
        }
    }

    /**
     * Initializes the game and broadcasts the starting snapshot to all players.
     */
    public void startGame() {
        try {
            // game state is initialized
            this.gameState.initGame();
        } catch (RuntimeException e) {
            // exception is logged with stack trace
            LogManager.getInstance().log(
                    LogLevel.ERROR,
                    "[GameController - startGame] critical error during game initialization",
                    e
            );
            // error is notified to all connected clients
            for (PlayerSession session : this.activeClients.values()) {
                GameErrorEvent errorDTO = new GameErrorEvent(
                        session.getPlayer().getNickname(),
                        GameErrorReason.GAME_INITIALIZATION_FAILED,
                        "internal error during game initialization"
                );
                session.getView().notifyError(errorDTO);
            }
            // pregame is destroyed
            this.destroyPregame();
            return;
        }

        // started flag is set
        this.started = true;
        // active client nicknames are extracted
        List<String> activeNicknames = new ArrayList<>(this.activeClients.keySet());
        // initial game state is built via mapper
        GameStateDTO initialState = GameDTOMapper.buildGameStateDTO(this.gameId, this.gameState, activeNicknames);
        // next player is retrieved from model
        String nextPlayer = this.gameState.getCurrentPlayer() != null ?
                this.gameState.getCurrentPlayer().getNickname() : null;
        // game started event is built
        GameStartedEvent eventDTO = new GameStartedEvent(initialState, nextPlayer);
        // event is broadcasted to all connected clients
        for (PlayerSession session : this.activeClients.values()) {
            session.getView().notifyGameStarted(eventDTO);
        }
    }

    /**
     * Adds a game command received from a client to the execution queue.
     *
     * @param command the command object encapsulating the player's request.
     * @param playerNickname the nickname of the player issuing the command.
     */
    public void enqueueCommand(GameCommand command, String playerNickname) {
        // game command is enqueued with normal priority
        this.commandQueue.add(new PrioritizedTask(2, () -> command.execute(this, playerNickname)));
    }

    /**
     * Adds an internal system task to the execution queue.
     *
     * @param task the runnable task to be executed.
     */
    public void enqueueSystemTask(Runnable task) {
        // internal system task is enqueued with high priority
        this.commandQueue.add(new PrioritizedTask(1, task));
    }

    /**
     * Main execution loop for the game controller thread.
     * Processes tasks from the command queue sequentially based on priority.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // prioritized task is retrieved from the queue
                PrioritizedTask prioritizedTask = this.commandQueue.take();
                try {
                    // task is executed
                    prioritizedTask.run();
                } catch (Exception e) {
                    System.err.println("task execution failed in game " + this.gameId + ": " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            // thread interruption is handled
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Automatically advances the game state through phases that require no user input.
     * Emits relevant delta events and phase change notifications.
     */
    private void autoAdvanceGameIfNecessary() {
        if (isGameSuspended()) return;
        GamePhase initialPhase = this.gameState.getCurrentPhase();
        boolean advanced = true;
        // phases without user input are resolved automatically
        while (advanced && !isGameSuspended()) {
            advanced = false;
            if (this.gameState.getCurrentPhase() == GamePhase.RESOLVE_EVENTS) {
                // events resolution is handled
                handleAutomaticEventsResolution();
                advanced = true;
            } else if (this.gameState.getCurrentPhase() == GamePhase.END_ROUND) {
                // endgame conditions are checked
                if (this.gameState.getCurrentRound() == 10 || this.gameState.getTribeDeckSize() == 0) {
                    handleGameEnd();
                    return;
                } else {
                    // round end is handled
                    handleAutomaticRoundEnd();
                    advanced = true;
                }
            }
        }
        // phase change notification is broadcasted if phase advanced
        if (initialPhase != this.gameState.getCurrentPhase()) {
            String nextPlayer = this.gameState.getCurrentPlayer() != null ?
                    this.gameState.getCurrentPlayer().getNickname() : null;
            GamePhaseChangedEvent phaseDTO = new GamePhaseChangedEvent(this.gameState.getCurrentPhase(), nextPlayer);
            for (PlayerSession session : this.activeClients.values()) {
                session.getView().notifyGamePhaseChanged(phaseDTO);
            }
        }
        // disconnected player turn is evaluated
        checkAndSkipDisconnectedPlayerTurn();
    }

    /**
     * Handles the automatic resolution of game events.
     */
    private void handleAutomaticEventsResolution() {
        // events are resolved on the model and receipts are collected
        List<EventResolutionReceipt> eventReceipts = this.gameState.resolveEvents();
        // next player is retrieved from model
        String nextPlayer = this.gameState.getCurrentPlayer() != null ?
                this.gameState.getCurrentPlayer().getNickname() : null;
        // each event receipt is mapped and broadcasted separately
        for (EventResolutionReceipt receipt : eventReceipts) {
            // stats changes are mapped to dtos via mapper
            List<PlayerStatsChangedDTO> statChangesDTO = GameDTOMapper.mapStatsChangesList(receipt.getStatChanges());
            // event resolved event is built
            EventResolvedEvent eventDTO = new EventResolvedEvent(receipt.getEventCardId(), statChangesDTO, nextPlayer);
            // event is broadcasted to all connected clients
            for (PlayerSession session : this.activeClients.values()) {
                session.getView().notifyEventResolved(eventDTO);
            }
        }
    }

    /**
     * Handles the automatic round end procedures.
     */
    private void handleAutomaticRoundEnd() {
        // round is ended on the model
        this.gameState.endRound();
        // round end details are retrieved from model
        int newRoundNumber = this.gameState.getCurrentRound();
        int newEraNumber = this.gameState.getCurrentEra();
        // absolute board state is fetched from model and mapped to dtos via mapper
        List<CardDTO> upperTribeRow = GameDTOMapper.mapCharacterCardsList(this.gameState.getUpperTribeRow());
        List<CardDTO> lowerTribeRow = GameDTOMapper.mapCharacterCardsList(this.gameState.getLowerTribeRow());
        List<CardDTO> upperBuildingRow = GameDTOMapper.mapBuildingCardsList(this.gameState.getUpperBuildingRow());
        List<CardDTO> lowerBuildingRow = GameDTOMapper.mapBuildingCardsList(this.gameState.getLowerBuildingRow());
        String nextPlayer = this.gameState.getCurrentPlayer() != null ?
                this.gameState.getCurrentPlayer().getNickname() : null;
        // round ended event is built with the absolute state
        RoundEndedEvent eventDTO = new RoundEndedEvent(
                newRoundNumber,
                newEraNumber,
                upperTribeRow,
                lowerTribeRow,
                upperBuildingRow,
                lowerBuildingRow,
                nextPlayer
        );
        // event is broadcasted to all connected clients
        for (PlayerSession session : this.activeClients.values()) {
            session.getView().notifyRoundEnded(eventDTO);
        }
    }

    /**
     * Forces the current player's turn to be skipped due to disconnection or timeout.
     * Alters the model and emits a delta event representing the automated action.
     */
    public void forceSkipCurrentPlayerTurn() {
        Player currentPlayer = this.gameState.getCurrentPlayer();
        if (currentPlayer == null) return;
        String currentNickname = currentPlayer.getNickname();
        GamePhase currentPhase = this.gameState.getCurrentPhase();
        if (currentPhase == GamePhase.PLACE_TOTEMS) {
            List<OfferTile> track = this.gameState.getOfferTrack();
            for (int i = track.size() - 1; i >= 0; i--) {
                if (track.get(i).isFree()) {
                    String tileId = track.get(i).getId();
                    // totem is placed automatically on available tile
                    this.gameState.placeTotem(currentNickname, tileId);
                    // next player is retrieved from model
                    String nextPlayer = this.gameState.getCurrentPlayer() != null ?
                            this.gameState.getCurrentPlayer().getNickname() : null;
                    // totem placed event is built with autoplayed flag set to true
                    TotemPlacedEvent eventDTO = new TotemPlacedEvent(currentNickname, tileId, nextPlayer, true);
                    // event is broadcasted to all connected clients
                    for (PlayerSession session : this.activeClients.values()) {
                        session.getView().notifyTotemPlaced(eventDTO);
                    }
                    return;
                }
            }
        } else if (currentPhase == GamePhase.RESOLVE_ACTIONS) {
            // action is forfeited automatically and receipt is captured
            ActionReceipt receipt = this.gameState.forfeitAction(currentNickname);
            // receipts are mapped to dtos via mapper
            List<PlayerStatsChangedDTO> statsDTOs = GameDTOMapper.mapStatsChangesList(receipt.getStatsChanges());
            TotemReturnedDTO totemDTO = GameDTOMapper.mapTotemReturn(receipt.getTotemDetails());
            // next player is extracted directly from the receipt
            String nextPlayer = receipt.getTotemDetails().getNextPlayerNickname();
            // action resolved event is built with autoplayed flag set to true
            ActionResolvedEvent eventDTO = new ActionResolvedEvent(
                    currentNickname,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    statsDTOs,
                    totemDTO,
                    nextPlayer,
                    true
            );
            // event is broadcasted to all connected clients
            for (PlayerSession session : this.activeClients.values()) {
                session.getView().notifyActionResolved(eventDTO);
            }
        } else if (currentPhase == GamePhase.EXTRA_ACQUISITION) {
            // phase is advanced manually on the model
            this.gameState.setCurrentPhase(GamePhase.RESOLVE_EVENTS);
            this.gameState.setCurrentPlayer(null);
            // extra acquisition event is built with empty lists and autoplayed flag
            ExtraAcquisitionResolvedEvent eventDTO = new ExtraAcquisitionResolvedEvent(
                    currentNickname,
                    null,
                    new ArrayList<>(),
                    null,
                    true
            );
            // event is broadcasted to all connected clients
            for (PlayerSession session : this.activeClients.values()) {
                session.getView().notifyExtraAcquisitionResolved(eventDTO);
            }
        }
    }

    /**
     * Checks if the currently active player is disconnected and, if so, skips their turn.
     */
    private void checkAndSkipDisconnectedPlayerTurn() {
        if (isGameSuspended() || this.gameState.getCurrentPlayer() == null) return;

        String currentNickname = this.gameState.getCurrentPlayer().getNickname();

        // turn is skipped if current player is not connected
        if (!this.activeClients.containsKey(currentNickname)) {
            this.forceSkipCurrentPlayerTurn();
            // next steps are enqueued
            enqueueSystemTask(this::autoAdvanceGameIfNecessary);
        }
    }

    /**
     * Processes a totem placement action initiated by a player.
     *
     * @param nickname the nickname of the player placing the totem
     * @param tileId the identifier of the offer tile chosen
     */
    public void processPlaceTotem(String nickname, String tileId) {
        if (isGameSuspended()) {
            // suspended error is notified
            notifySuspendedActionDenied(nickname);
            return;
        }

        PlayerSession session = this.activeClients.get(nickname);
        if (session != null) {
            try {
                // --- AGGIUNTA: Salviamo la fase PRIMA della mossa ---
                GamePhase phaseBeforeMove = this.gameState.getCurrentPhase();

                // totem placement is executed on the model
                this.gameState.placeTotem(session.getPlayer().getNickname(), tileId);

                // next player is retrieved from model to build the dto
                String nextPlayer = this.gameState.getCurrentPlayer() != null ?
                        this.gameState.getCurrentPlayer().getNickname() : null;

                // dto is instantiated with the extracted data
                TotemPlacedEvent eventDTO = new TotemPlacedEvent(nickname, tileId, nextPlayer, false);

                // event is broadcasted to all connected clients
                for (PlayerSession s : this.activeClients.values()) {
                    s.getView().notifyTotemPlaced(eventDTO);
                }

                GamePhase phaseAfterMove = this.gameState.getCurrentPhase();
                if (phaseBeforeMove != phaseAfterMove) {
                    GamePhaseChangedEvent phaseDTO = new GamePhaseChangedEvent(phaseAfterMove, nextPlayer);
                    for (PlayerSession s : this.activeClients.values()) {
                        s.getView().notifyGamePhaseChanged(phaseDTO);
                    }
                }

                // game phase is advanced if necessary
                autoAdvanceGameIfNecessary();

            } catch (InvalidGameActionException e) {
                // domain-specific game error is notified
                GameErrorEvent errorDTO = new GameErrorEvent(nickname, e.getReason(), e.getMessage());
                session.getView().notifyError(errorDTO);
            } catch (RuntimeException e) {
                // exception is logged with stack trace
                LogManager.getInstance().log(
                        LogLevel.ERROR,
                        "[GameController - processPlaceTotem] critical error while placing totem for player " + nickname,
                        e
                );
                // internal error is notified for unexpected failures
                GameErrorEvent errorDTO = new GameErrorEvent(nickname, GameErrorReason.INTERNAL_ERROR, "internal error while placing totem");
                session.getView().notifyError(errorDTO);
            }
        }
    }

    /**
     * Processes an action resolution initiated by a player, acquiring characters and buildings.
     *
     * @param nickname the nickname of the player performing the action
     * @param characters the list of character card IDs acquired
     * @param buildings the list of building card IDs acquired
     */
    public void processResolveAction(String nickname, List<String> characters, List<String> buildings) {
        if (isGameSuspended()) {
            // suspended error is notified
            notifySuspendedActionDenied(nickname);
            return;
        }
        PlayerSession session = this.activeClients.get(nickname);
        if (session != null) {
            try {
                // action resolution is executed on the model and receipt is captured
                ActionReceipt receipt = this.gameState.resolveAction(session.getPlayer().getNickname(), characters, buildings);
                // receipts are mapped to dtos via mapper
                List<PlayerStatsChangedDTO> statsDTOs = GameDTOMapper.mapStatsChangesList(receipt.getStatsChanges());
                TotemReturnedDTO totemDTO = GameDTOMapper.mapTotemReturn(receipt.getTotemDetails());
                // next player is extracted directly from the receipt
                String nextPlayer = receipt.getTotemDetails().getNextPlayerNickname();
                // dto is instantiated with the mapped data
                ActionResolvedEvent eventDTO = new ActionResolvedEvent(
                        nickname,
                        characters,
                        buildings,
                        statsDTOs,
                        totemDTO,
                        nextPlayer,
                        false
                );
                // event is broadcasted to all connected clients
                for (PlayerSession s : this.activeClients.values()) {
                    s.getView().notifyActionResolved(eventDTO);
                }
                // game phase is advanced if necessary
                autoAdvanceGameIfNecessary();
            } catch (InvalidGameActionException e) {
                // domain-specific game error is notified
                GameErrorEvent errorDTO = new GameErrorEvent(nickname, e.getReason(), e.getMessage());
                session.getView().notifyError(errorDTO);
            } catch (RuntimeException e) {
                // exception is logged with stack trace
                LogManager.getInstance().log(
                        LogLevel.ERROR,
                        "[GameController - processResolveAction] critical error while resolving action for player " + nickname,
                        e
                );
                // internal error is notified for unexpected failures
                GameErrorEvent errorDTO = new GameErrorEvent(nickname, GameErrorReason.INTERNAL_ERROR, "internal error while resolving action");
                session.getView().notifyError(errorDTO);
            }
        }
    }

    /**
     * Processes a player disconnection, notifying other players and checking for suspension.
     *
     * @param nickname the nickname of the disconnected player.
     * @param connectionID the unique identifier of the connection that dropped.
     */
    public void processDisconnect(String nickname, String connectionID) {
        // active session is retrieved safely
        PlayerSession activeSession = this.activeClients.get(nickname);

        // connection identity is verified to prevent ghost disconnects
        if (activeSession != null && activeSession.getView().getConnectionID().equals(connectionID)) {
            // player session is removed and observers are updated
            this.activeClients.remove(nickname);

            // disconnected event is built
            PlayerDisconnectedEvent disconnectDTO = new PlayerDisconnectedEvent(nickname);

            // event is broadcasted to remaining active clients
            for (PlayerSession s : this.activeClients.values()) {
                s.getView().notifyPlayerDisconnected(disconnectDTO);
            }

            checkSuspendCondition();

            if (!isGameSuspended()) {
                autoAdvanceGameIfNecessary();
            }
        }
    }

    /**
     * Checks if a player is currently active in the game.
     *
     * @param nickname the player's nickname.
     * @return true if the player is active, false otherwise.
     */
    public boolean isPlayerActive(String nickname) {
        // player presence is checked in the concurrent map
        return this.activeClients.containsKey(nickname);
    }

    /**
     * Processes a player reconnection, sending them the connection result, the game state and notifying others.
     *
     * @param nickname the nickname of the reconnected player.
     * @param newView the new view associated with the reconnected player.
     */
    public void processReconnect(String nickname, VirtualView newView) {
        // player is retrieved from model to restore session
        Player reconnectedPlayer = this.gameState.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);

        if (reconnectedPlayer != null) {
            // player reconnection is logged
            LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] Player reconnected: " + nickname);

            // session is restored
            PlayerSession session = new PlayerSession(reconnectedPlayer, newView);
            this.activeClients.put(nickname, session);

            // execution is stopped if game is not started
            if (!this.started) {
                return;
            }

            synchronized (suspendLock) {
                if (this.isSuspended && this.activeClients.size() >= 2) {
                    this.isSuspended = false;
                    suspendLock.notifyAll();

                    // game resumed notification is broadcasted only to previously connected players
                    for (Map.Entry<String, PlayerSession> entry : this.activeClients.entrySet()) {
                        if (!entry.getKey().equals(nickname)) {
                            entry.getValue().getView().notifyGameResumed();
                        }
                    }
                }
            }

            // connection success is sent to unlock the client waiting thread with reconnecting flag set to true
            newView.notifyConnectionResult(new ConnectionResultEvent(true, "reconnection successful", true, newView.getConnectionID()));

            // game state event is built and sent in unicast
            GameStateEvent currentStateEvent = buildGameStateEvent();
            newView.notifyGameState(currentStateEvent);

            // reconnected event is built
            PlayerReconnectedEvent reconnectedDTO = new PlayerReconnectedEvent(nickname);

            // event is broadcasted to other active clients
            for (Map.Entry<String, PlayerSession> entry : this.activeClients.entrySet()) {
                if (!entry.getKey().equals(nickname)) {
                    entry.getValue().getView().notifyPlayerReconnected(reconnectedDTO);
                }
            }

            // game phase is advanced if necessary
            if (!isGameSuspended()) {
                autoAdvanceGameIfNecessary();
            }
        }
    }

    /**
     * Processes an extra acquisition action initiated by a player.
     *
     * @param nickname the nickname of the player performing the action
     * @param cardId the id of the acquired character or building card
     */
    public void processExtraAcquisition(String nickname, String cardId) {
        if (isGameSuspended()) {
            // suspended error is notified
            notifySuspendedActionDenied(nickname);
            return;
        }
        PlayerSession session = this.activeClients.get(nickname);
        if (session != null) {
            try {
                // extra acquisition is resolved on the model and receipt is captured
                ExtraAcquisitionReceipt receipt = this.gameState.resolveExtraAcquisition(nickname, cardId);
                // receipts are mapped to dtos via mapper
                List<PlayerStatsChangedDTO> statsDTOs = GameDTOMapper.mapStatsChangesList(receipt.getStatsChanges());
                // next player is retrieved from model (not present in extra acquisition receipt)
                String nextPlayer = this.gameState.getCurrentPlayer() != null ?
                        this.gameState.getCurrentPlayer().getNickname() : null;
                // extra acquisition event is built using the mapped list
                ExtraAcquisitionResolvedEvent eventDTO = new ExtraAcquisitionResolvedEvent(
                        nickname,
                        cardId,
                        statsDTOs,
                        nextPlayer,
                        false
                );
                // event is broadcasted to all connected clients
                for (PlayerSession s : this.activeClients.values()) {
                    s.getView().notifyExtraAcquisitionResolved(eventDTO);
                }
                // game phase is advanced if necessary
                autoAdvanceGameIfNecessary();
            } catch (InvalidGameActionException e) {
                // domain-specific game error is notified
                GameErrorEvent errorDTO = new GameErrorEvent(nickname, e.getReason(), e.getMessage());
                session.getView().notifyError(errorDTO);
            } catch (RuntimeException e) {
                // exception is logged with stack trace
                LogManager.getInstance().log(
                        LogLevel.ERROR,
                        "[GameController - processExtraAcquisition] critical error during extra acquisition for player " + nickname,
                        e
                );
                // internal error is notified for unexpected failures
                GameErrorEvent errorDTO = new GameErrorEvent(nickname, GameErrorReason.INTERNAL_ERROR, "internal error during extra acquisition");
                session.getView().notifyError(errorDTO);
            }
        }
    }

    /**
     * Handles the scenario where the game ends prematurely due to player abandonment and timeout.
     */
    private void handleForfeitVictory() {
        if (!isGameSuspended()) return;

        if (this.activeClients.isEmpty()) {
            // game is aborted due to total abandonment
            this.lifecycleCallback.abortGame(this.gameId);
            // command queue is cleared
            this.commandQueue.clear();
        } else {
            // game end is triggered due to timeout
            handleGameEnd();
        }
    }

    /**
     * Ends the game, calculates final scores, records them in the database, and notifies all clients.
     */
    private void handleGameEnd() {
        GameResult modelResult = this.gameState.calculateResults();
        int numOfPlayers = this.gameState.getPlayers().size();

        // winners list is extracted
        List<String> winners = modelResult.getWinners().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());

        Map<String, int[]> fullMatchScores = new HashMap<>();
        Map<String, Integer> activePlayersDbScores = new HashMap<>();

        for (Map.Entry<Player, int[]> entry : modelResult.getLeaderboard().entrySet()) {
            String nickname = entry.getKey().getNickname();
            int prestige = entry.getValue()[0];
            int food = entry.getValue()[1];

            // full scores are stored for dto broadcast
            fullMatchScores.put(nickname, entry.getValue());

            // custom score is calculated and stored only for active players
            if (this.activeClients.containsKey(nickname)) {
                int calculatedScore = (prestige * 1000) + food;
                activePlayersDbScores.put(nickname, calculatedScore);
            }
        }

        List<LeaderboardEntryDTO> globalRanking = new ArrayList<>();
        Connection conn = null;
//        try {
//            // database connection is established
//            conn = DatabaseConnection.getInstance().getConnection();
//            LeaderboardDAO leaderboardDAO = new LeaderboardDAO(conn);
//
//            // only active players are saved to database with original player count
//            leaderboardDAO.saveMatchResults(activePlayersDbScores, numOfPlayers);
//
//            // global ranking is fetched
//            globalRanking = leaderboardDAO.fetchLeaderboard(numOfPlayers);
//        } catch (Exception e) {
//            System.err.println("error during database operations: " + e.getMessage());
//        } finally {
//            if (conn != null) {
//                try {
//                    // database connection is closed
//                    conn.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        for (PlayerSession session : this.activeClients.values()) {
            String currentNickname = session.getPlayer().getNickname();
            int globalPos = calculatePlayerPosition(currentNickname, globalRanking);

            // result dto is built with full scores
            GameResultDTO resultDTO = new GameResultDTO(winners, fullMatchScores, globalRanking, globalPos);
            GameEndedEvent eventDTO = new GameEndedEvent(resultDTO);

            // client is notified
            session.getView().notifyGameEnded(eventDTO);
        }

        // lobby is notified to clear resources
        this.lifecycleCallback.endGame(this.gameId, modelResult);
    }

    /**
     * Determines the global leaderboard position of a specific player.
     *
     * @param nickname the player's nickname
     * @param ranking the fetched global ranking list
     * @return the player's integer position, or -1 if not found
     */
    private int calculatePlayerPosition(String nickname, List<LeaderboardEntryDTO> ranking) {
        for (LeaderboardEntryDTO entry : ranking) {
            if (entry.getPlayer().equals(nickname)) {
                return entry.getPosition();
            }
        }
        return -1;
    }

    /**
     * Builds a comprehensive snapshot of the current game state wrapped in a network event.
     *
     * @return the generated GameStateEvent containing the full state of the game.
     */
    private GameStateEvent buildGameStateEvent() {
        // active client nicknames are extracted
        List<String> activeNicknames = new ArrayList<>(this.activeClients.keySet());
        // game state dto is built via mapper and wrapped in the dedicated event
        return new GameStateEvent(GameDTOMapper.buildGameStateDTO(this.gameId, this.gameState, activeNicknames));
    }

    /**
     * Returns the unique identifier associated with this game instance.
     *
     * @return the integer game id
     */
    public int getGameId() {
        return this.gameId;
    }

    /**
     * Returns the current number of players registered in this game.
     *
     * @return the number of players
     */
    public int getCurrentPlayersCount() {
        // total players registered in the game state are returned
        return this.gameState.getPlayers().size();
    }

    /**
     * Returns the target number of players required for the game to start.
     *
     * @return the target number of players
     */
    public int getTargetPlayersCount() {
        // required players for game start are returned
        return this.gameState.getTargetPlayers();
    }

    /**
     * Indicates whether the game session has officially begun.
     *
     * @return true if started, false otherwise
     */
    public boolean isStarted() {
        return this.started;
    }

    /**
     * Destroys an unstarted game and notifies all connected players.
     * Active connections are cleared and the game state is discarded.
     */
    public void destroyPregame() {
        // active clients are notified of the game cancellation
        for (PlayerSession session : this.activeClients.values()) {

            // error dto is built using the specific enum for pregame destruction
            GameErrorEvent errorDTO = new GameErrorEvent(
                    session.getPlayer().getNickname(),
                    GameErrorReason.PREGAME_DESTROYED_HOST_DISCONNECTED,
                    "the unstarted game has been destroyed due to a player disconnection"
            );

            // view is notified and observer is removed from the game state
            session.getView().notifyError(errorDTO);
        }

        // client sessions map is cleared
        this.activeClients.clear();

        // pending commands in the queue are cleared
        this.commandQueue.clear();
    }

    /**
     * Retrieves the nicknames of all players currently registered in the game.
     *
     * @return A list containing the nicknames of the players.
     */
    public List<String> getPlayersNicknames() {
        // player nicknames are extracted from the game state
        return this.gameState.getPlayers().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    /**
     * Safely checks the suspension flag using the synchronization lock.
     *
     * @return true if the game is currently suspended, false otherwise
     */
    private boolean isGameSuspended() {
        synchronized (suspendLock) {
            return this.isSuspended;
        }
    }

    /**
     * Checks if the game requires suspension due to lack of connected players and manages the forfeit timer.
     */
    private void checkSuspendCondition() {
        synchronized (suspendLock) {
            if (this.activeClients.size() < 2 && !this.isSuspended) {
                this.isSuspended = true;

                for (PlayerSession session : this.activeClients.values()) {
                    session.getView().notifyGameSuspended();
                }

                // timer thread is started for forfeit victory
                new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    long timeToWait = FORFEIT_TIMEOUT_MS;

                    synchronized (suspendLock) {
                        while (this.isSuspended && timeToWait > 0) {
                            try {
                                suspendLock.wait(timeToWait);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                            long elapsedTime = System.currentTimeMillis() - startTime;
                            timeToWait = FORFEIT_TIMEOUT_MS - elapsedTime;
                        }

                        if (this.isSuspended && timeToWait <= 0) {
                            enqueueSystemTask(this::handleForfeitVictory);
                        }
                    }
                }).start();
            }
        }
    }
}
