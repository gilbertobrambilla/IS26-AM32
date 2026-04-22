package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.game.models.*;
import it.polimi.ingsw.am32.model.GameResult;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidActionException;
import it.polimi.ingsw.am32.model.receipts.*;
import it.polimi.ingsw.am32.model.tiles.OfferTile;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
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
    private final BlockingQueue<Runnable> commandQueue;
    private boolean isSuspended;
    private boolean started;
    private final Object suspendLock = new Object();

    // timeout is defined in milliseconds
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
        this.commandQueue = new LinkedBlockingQueue<>();
        this.isSuspended = false;
        this.started = false;
    }

    /**
     * Adds a newly connected player to the game state and active client list.
     *
     * @param nickname the unique nickname of the player
     * @param view the virtual view handling the connection to the player's client
     * @param player the player model instance
     */
    public void addPlayerToGame(String nickname, GameView view, Player player) {
        // player is added to active clients and model
        this.activeClients.put(nickname, new PlayerSession(player, view));
        this.gameState.addPlayer(player);
    }

    /**
     * Initializes the game and broadcasts the starting snapshot to all players.
     */
    public void startGame() {
        // game is initialized and started flag is set
        this.gameState.initGame();
        this.started = true;

        // initial game state is built
        GameStateDTO initialState = buildGameStateDTO();

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
     * @param command the command object encapsulating the player's request
     * @param playerNickname the nickname of the player issuing the command
     */
    public void enqueueCommand(GameCommand command, String playerNickname) {
        // game command is enqueued for sequential execution
        this.commandQueue.add(() -> command.execute(this, playerNickname));
    }

    /**
     * Adds an internal system task to the execution queue.
     *
     * @param task the runnable task to be executed
     */
    public void enqueueSystemTask(Runnable task) {
        // internal system task is enqueued
        this.commandQueue.add(task);
    }

    /**
     * Main execution loop for the game controller thread.
     * Processes tasks from the command queue sequentially.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // tasks are retrieved and executed from the queue
                Runnable task = this.commandQueue.take();
                try {
                    task.run();
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
                // events are resolved on the model and receipts are collected
                List<EventResolutionReceipt> eventReceipts = this.gameState.resolveEvents();

                // next player is retrieved from model
                String nextPlayer = this.gameState.getCurrentPlayer() != null ?
                        this.gameState.getCurrentPlayer().getNickname() : null;

                // each event receipt is mapped and broadcasted separately
                for (EventResolutionReceipt receipt : eventReceipts) {
                    // stats changes are mapped to dtos
                    List<PlayerStatsChangedDTO> statChangesDTO = mapStatsChangesList(receipt.getStatChanges());

                    // event resolved event is built
                    EventResolvedEvent eventDTO = new EventResolvedEvent(receipt.getEventCardId(), statChangesDTO, nextPlayer);

                    // event is broadcasted to all connected clients
                    for (PlayerSession session : this.activeClients.values()) {
                        session.getView().notifyEventResolved(eventDTO);
                    }
                }

                advanced = true;
            }

            if (this.gameState.getCurrentPhase() == GamePhase.END_ROUND) {
                // endgame conditions are checked
                if (this.gameState.getCurrentRound() == 10 || this.gameState.getTribeDeckSize() == 0) {
                    handleGameEnd();
                    return;
                } else {
                    // round is ended on the model
                    this.gameState.endRound();

                    // round end details are retrieved from model
                    int newRoundNumber = this.gameState.getCurrentRound();
                    int newEraNumber = this.gameState.getCurrentEra();

                    // absolute board state is fetched from model and mapped to dtos
                    List<CardDTO> upperTribeRow = mapCharacterCardsList(this.gameState.getUpperTribeRow());
                    List<CardDTO> lowerTribeRow = mapCharacterCardsList(this.gameState.getLowerTribeRow());
                    List<CardDTO> upperBuildingRow = mapBuildingCardsList(this.gameState.getUpperBuildingRow());
                    List<CardDTO> lowerBuildingRow = mapBuildingCardsList(this.gameState.getLowerBuildingRow());

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

            // receipts are mapped to dtos
            List<PlayerStatsChangedDTO> statsDTOs = mapStatsChangesList(receipt.getStatsChanges());
            TotemReturnedDTO totemDTO = mapTotemReturn(receipt.getTotemDetails());

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
        if (isGameSuspended()) return;

        PlayerSession session = this.activeClients.get(nickname);
        if (session != null) {
            try {
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

                // game phase is advanced if necessary
                autoAdvanceGameIfNecessary();

            } catch (InvalidActionException e) {
                // error dto is built and sent only to the specific client
                InvalidActionEvent errorDTO = new InvalidActionEvent(nickname, e.getReason(), e.getMessage());
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
        if (isGameSuspended()) return;

        PlayerSession session = this.activeClients.get(nickname);
        if (session != null) {
            try {
                // action resolution is executed on the model and receipt is captured
                ActionReceipt receipt = this.gameState.resolveAction(session.getPlayer().getNickname(), characters, buildings);

                // receipts are mapped to dtos
                List<PlayerStatsChangedDTO> statsDTOs = mapStatsChangesList(receipt.getStatsChanges());
                TotemReturnedDTO totemDTO = mapTotemReturn(receipt.getTotemDetails());

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

            } catch (InvalidActionException e) {
                // error dto is built and sent only to the specific client
                InvalidActionEvent errorDTO = new InvalidActionEvent(nickname, e.getReason(), e.getMessage());
                session.getView().notifyError(errorDTO);
            }
        }
    }

    /**
     * Processes a player disconnection, notifying other players and checking for suspension.
     *
     * @param nickname the nickname of the disconnected player
     */
    public void processDisconnect(String nickname) {
        // player session is removed and observers are updated
        PlayerSession session = this.activeClients.remove(nickname);
        if (session != null) {

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
     * Processes a player reconnection, sending them the game state and notifying others.
     *
     * @param nickname the nickname of the reconnected player
     * @param newView the new view associated with the reconnected player
     */
    public void processReconnect(String nickname, GameView newView) {
        // player is retrieved from model to restore session
        Player reconnectedPlayer = this.gameState.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);

        if (reconnectedPlayer != null) {
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

                    // game resumed notification is broadcasted
                    for (PlayerSession s : this.activeClients.values()) {
                        s.getView().notifyGameResumed();
                    }
                }
            }

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
        if (isGameSuspended()) return;

        PlayerSession session = this.activeClients.get(nickname);
        if (session != null) {
            try {
                // extra acquisition is resolved on the model and receipt is captured
                ExtraAcquisitionReceipt receipt = this.gameState.resolveExtraAcquisition(nickname, cardId);

                // receipts are mapped to dtos
                List<PlayerStatsChangedDTO> statsDTOs = mapStatsChangesList(receipt.getStatsChanges());

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

            } catch (InvalidActionException e) {
                // error dto is built and sent only to the specific client
                InvalidActionEvent errorDTO = new InvalidActionEvent(nickname, e.getReason(), e.getMessage());
                session.getView().notifyError(errorDTO);
            }
        }
    }

    /**
     * Handles the scenario where the game ends prematurely due to player abandonment and timeout.
     */
    private void handleForfeitVictory() {
        if (!isGameSuspended() || this.activeClients.isEmpty()) return;
        // game end is triggered due to timeout
        handleGameEnd();
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
     * Builds a comprehensive snapshot of the current game state.
     *
     * @return the generated GameStateDTO containing the full state of the game
     */
    private GameStateDTO buildGameStateDTO() {
        // players are mapped to dtos
        List<PlayerDTO> playersDTO = this.gameState.getPlayers().stream()
                .map(this::mapPlayer)
                .collect(Collectors.toList());

        // rows are retrieved from model and mapped to dtos
        List<CardDTO> upperTribeRowDTO = mapCharacterCardsList(this.gameState.getUpperTribeRow());
        List<CardDTO> lowerTribeRowDTO = mapCharacterCardsList(this.gameState.getLowerTribeRow());
        List<CardDTO> upperBuildingRowDTO = mapBuildingCardsList(this.gameState.getUpperBuildingRow());
        List<CardDTO> lowerBuildingRowDTO = mapBuildingCardsList(this.gameState.getLowerBuildingRow());

        // offer track is mapped to dtos
        List<OfferTileDTO> offerTrackDTO = this.gameState.getOfferTrack().stream()
                .map(this::mapOfferTile)
                .collect(Collectors.toList());

        // turn order is extracted as a list of nicknames
        List<String> turnOrderDTO = this.gameState.getTurnOrder().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());

        return new GameStateDTO(
                this.gameId,
                playersDTO,
                upperTribeRowDTO,
                lowerTribeRowDTO,
                upperBuildingRowDTO,
                lowerBuildingRowDTO,
                offerTrackDTO,
                turnOrderDTO,
                this.gameState.getTribeDeckSize(),
                this.gameState.getTribeDeckFirstCardEra(),
                this.gameState.getCurrentRound(),
                this.gameState.getCurrentEra(),
                this.gameState.getCurrentPhase(),
                this.gameState.getCurrentPlayer() != null ? this.gameState.getCurrentPlayer().getNickname() : null
        );
    }

    /**
     * Builds a comprehensive snapshot of the current game state wrapped in a network event.
     *
     * @return the generated GameStateEvent containing the full state of the game.
     */
    private GameStateEvent buildGameStateEvent() {
        // game state dto is built and wrapped in the dedicated event
        return new GameStateEvent(buildGameStateDTO());
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
            InvalidActionEvent errorDTO = new InvalidActionEvent(
                    session.getPlayer().getNickname(),
                    InvalidActionReason.PREGAME_DESTROYED_HOST_DISCONNECTED,
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

    /**
     * Maps a single player stats change receipt to its corresponding dto.
     *
     * @param receipt the receipt from the model
     * @return the mapped dto, or null if the receipt is null
     */
    private PlayerStatsChangedDTO mapStatsChange(PlayerStatsChangeReceipt receipt) {
        if (receipt == null) return null;
        return new PlayerStatsChangedDTO(
                receipt.getPlayerNickname(),
                receipt.getFoodChange(),
                receipt.getPrestigeChange(),
                receipt.getCurrentFood(),
                receipt.getCurrentPrestige(),
                receipt.getReason()
        );
    }

    /**
     * Maps a list of player stats change receipts to a list of dtos.
     *
     * @param receipts the list of receipts from the model
     * @return the mapped list of dtos
     */
    private List<PlayerStatsChangedDTO> mapStatsChangesList(List<PlayerStatsChangeReceipt> receipts) {
        if (receipts == null) return new ArrayList<>();
        return receipts.stream()
                .map(this::mapStatsChange)
                .collect(Collectors.toList());
    }

    /**
     * Maps a totem return receipt to its corresponding dto.
     *
     * @param receipt the receipt from the model
     * @return the mapped dto, or null if the receipt is null
     */
    private TotemReturnedDTO mapTotemReturn(TotemReturnReceipt receipt) {
        if (receipt == null) return null;
        return new TotemReturnedDTO(
                receipt.getPlayerNickname(),
                receipt.getTurnOrderPosition(),
                mapStatsChange(receipt.getStatsChange()),
                receipt.getNextPlayerNickname()
        );
    }

    /**
     * A list of building card models is mapped to a list of card dtos.
     *
     * @param buildings the list of building card models to be mapped
     * @return the mapped list of card dtos
     */
    private List<CardDTO> mapBuildingCardsList(List<BuildingCard> buildings) {
        if (buildings == null) return new ArrayList<>();

        // building cards are processed as a stream and mapped individually
        return buildings.stream()
                .map(building -> new CardDTO(
                        building.getId()
                ))
                .collect(Collectors.toList());
    }

    /**
     * A list of tribe card models is mapped to a list of card dtos.
     *
     * @param characters the list of tribe card models to be mapped
     * @return the mapped list of card dtos
     */
    private List<CardDTO> mapCharacterCardsList(List<Card> characters) {
        if (characters == null) return new ArrayList<>();

        // character cards are processed as a stream, cast and mapped individually
        return characters.stream()
                .map(card -> {
                    // card is safely cast to character card based on game rules
                    CharacterCard character = (CharacterCard) card;

                    return new CardDTO(
                            character.getId()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Player model is mapped to a player dto.
     *
     * @param player the player model
     * @return the mapped player dto
     */
    private PlayerDTO mapPlayer(Player player) {
        if (player == null) return null;

        // player cards are mapped to dtos
        List<CardDTO> characterCards = mapCharacterCardsList(new ArrayList<>(player.getCharacterList()));
        List<CardDTO> buildingCards = mapBuildingCardsList(new ArrayList<>(player.getBuildingList()));

        return new PlayerDTO(
                player.getNickname(),
                player.getTotem(),
                player.getFood(),
                player.getPrestige(),
                this.activeClients.containsKey(player.getNickname()),
                characterCards,
                buildingCards
        );
    }

    /**
     * Offer tile model is mapped to an offer tile dto.
     *
     * @param tile the offer tile model
     * @return the mapped offer tile dto
     */
    private OfferTileDTO mapOfferTile(OfferTile tile) {
        if (tile == null) return null;

        // occupying player nickname is extracted if present
        String occupyingPlayer = tile.isFree() ? null : tile.getOccupyingPlayer().getNickname();

        return new OfferTileDTO(tile.getId(), occupyingPlayer);
    }
}
