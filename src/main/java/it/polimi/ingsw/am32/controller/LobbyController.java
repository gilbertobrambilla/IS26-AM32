package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import it.polimi.ingsw.am32.model.GameCreator;
import it.polimi.ingsw.am32.model.GameResult;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.LobbyErrorReason;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller class responsible for managing the lobby, player connections, and game creation.
 */
public class LobbyController implements GameLifecycleCallback {
    private final Map<String, VirtualView> connectedPlayers;
    private final Map<Integer, GameController> activeGames;
    private final Map<String, GameController> playerGameMap;
    private int nextGameId;
    private final Object lobbyLock;

    /**
     * Constructs a new LobbyController with default collections and initial game id.
     */
    public LobbyController() {
        this.connectedPlayers = new HashMap<>();
        this.activeGames = new HashMap<>();
        this.playerGameMap = new HashMap<>();
        this.nextGameId = 1;
        this.lobbyLock = new Object();
    }

    /**
     * Processes a new player connection request.
     * Uniqueness of the nickname is verified across both the lobby and active games.
     *
     * @param nickname The requested nickname.
     * @param view     The player's connection view.
     */
    public void connectPlayer(String nickname, VirtualView view) {
        GameController game;
        boolean addedToLobby = false;

        synchronized (this.lobbyLock) {
            game = this.playerGameMap.get(nickname);
            if (game == null) {
                // player is added to lobby if absent
                if (!this.connectedPlayers.containsKey(nickname)) {
                    this.connectedPlayers.put(nickname, view);
                    addedToLobby = true;
                }
            }
        }

        if (game != null) {
            // reconnect task is enqueued
            game.enqueueSystemTask(() -> game.processReconnect(nickname, view));
        } else if (addedToLobby) {
            view.notifyConnectionResult(new ConnectionResultEvent(true, "connection successful"));
            // available lobby list is sent to the newly connected player
            view.notifyLobbyState(buildAvailableGamesListEvent());
        } else {
            // connection rejection is notified
            view.notifyConnectionResult(new ConnectionResultEvent(false, "nickname is already taken in the lobby"));
            // virtual view is destroyed cleanly
            view.destroy();
        }
    }

    /**
     * Processes a new game creation request.
     *
     * @param nickname     The nickname of the creator.
     * @param view         The player's connection view.
     * @param numOfPlayers The targeted number of players for the new game.
     */
    public void createGame(String nickname, VirtualView view, int numOfPlayers) {
        int gameId;
        synchronized (this.lobbyLock) {
            // game id is retrieved and incremented
            gameId = this.nextGameId++;
        }

        // game and state are instantiated via creator
        GameCreator gameCreator = new GameCreator(gameId);
        GameState gameState = gameCreator.createGame(numOfPlayers);
        GameController gameController = new GameController(gameId, gameState, this);

        synchronized (this.lobbyLock) {
            // game is mapped
            this.activeGames.put(gameId, gameController);
            this.playerGameMap.put(nickname, gameController);
            // player is removed from lobby
            this.connectedPlayers.remove(nickname);
        }

        // player is instantiated
        Player player = new Player(nickname);
        // player addition task is enqueued with callback
        gameController.enqueueSystemTask(() -> gameController.addPlayerToGame(nickname, view, player, this::broadcastAvailableGamesUpdate));
        // waiting room update task is enqueued
        gameController.enqueueSystemTask(gameController::broadcastWaitingRoomState);
        // controller thread is started
        new Thread(gameController).start();
    }

    /**
     * Processes a game join request asynchronously by delegating validation to the game controller.
     *
     * @param nickname The nickname of the player joining.
     * @param view     The player's connection view.
     * @param gameId   The ID of the target game.
     */
    public void joinGame(String nickname, VirtualView view, int gameId) {
        if (view == null) return;

        GameController game;
        synchronized (this.lobbyLock) {
            // game controller is retrieved from active games
            game = this.activeGames.get(gameId);
        }

        if (game != null) {
            // join request task is enqueued to the game controller with callback
            game.enqueueSystemTask(() -> game.processJoinRequest(nickname, view, () -> {
                synchronized (this.lobbyLock) {
                    // player is mapped to game
                    this.playerGameMap.put(nickname, game);
                    // player is removed from lobby
                    this.connectedPlayers.remove(nickname);
                }
                // menu is notified of the updated available games list
                this.broadcastAvailableGamesUpdate();
            }));
        } else {
            // error is notified if game id is not found
            view.notifyLobbyError(new LobbyErrorEvent(LobbyErrorReason.GAME_NOT_FOUND, "game id " + gameId + " not found"));
        }
    }

    /**
     * Handles a network drop event by notifying the relevant game or managing an unstarted game.
     *
     * @param nickname The nickname of the disconnected player.
     */
    public void handlePlayerNetworkDrop(String nickname) {
        GameController game;
        synchronized (this.lobbyLock) {
            // player is removed from lobby
            this.connectedPlayers.remove(nickname);
            game = this.playerGameMap.get(nickname);
            if (game != null && !game.isStarted()) {
                // player is removed from player game map
                this.playerGameMap.remove(nickname);
            }
        }

        if (game != null) {
            if (!game.isStarted()) {
                if (game.getCurrentPlayersCount() <= 1) {
                    // unstarted game is destroyed if empty
                    destroyUnstartedGame(game);
                } else {
                    // pregame disconnect task is enqueued in game controller with callback
                    game.enqueueSystemTask(() -> game.processPregameDisconnect(nickname, this::broadcastAvailableGamesUpdate));
                }
            } else {
                // disconnect task is enqueued in game controller
                game.enqueueSystemTask(() -> game.processDisconnect(nickname));
            }
        }
    }

    /**
     * Processes the termination of a game.
     * Data structures are cleared.
     *
     * @param gameId The ID of the ended game.
     * @param result The final result of the game.
     */
    @Override
    public void endGame(int gameId, GameResult result) {
        synchronized (this.lobbyLock) {
            // game is removed from active games
            GameController removedGame = this.activeGames.remove(gameId);
            if (removedGame != null) {
                // player mappings are cleared
                this.playerGameMap.entrySet().removeIf(entry -> entry.getValue().equals(removedGame));
            }
        }
    }

    /**
     * Destroys an unstarted game and notifies remaining players if any.
     *
     * @param game The unstarted game to destroy.
     */
    private void destroyUnstartedGame(GameController game) {
        int gameId = game.getGameId();
        synchronized (this.lobbyLock) {
            // game is removed from active games
            this.activeGames.remove(gameId);
            // player mappings for this game are cleared
            this.playerGameMap.entrySet().removeIf(entry -> entry.getValue().equals(game));
        }
        // game controller is notified to inform its active clients
        game.enqueueSystemTask(game::destroyPregame);
        // menu is notified via available games list
        broadcastAvailableGamesUpdate();
    }

    /**
     * Broadcasts the updated list of available games to all players in the lobby menu.
     */
    private void broadcastAvailableGamesUpdate() {
        // available games list event is built
        AvailableLobbiesEvent event = buildAvailableGamesListEvent();
        List<VirtualView> viewsToNotify;

        synchronized (this.lobbyLock) {
            // snapshot of connected players is taken
            viewsToNotify = new ArrayList<>(this.connectedPlayers.values());
        }

        // event is pushed to all players currently in the menu
        for (VirtualView view : viewsToNotify) {
            view.notifyLobbyState(event);
        }
    }

    /**
     * Builds the complete list of available games for newly connected players.
     *
     * @return The built AvailableLobbiesEvent.
     */
    private AvailableLobbiesEvent buildAvailableGamesListEvent() {
        List<LobbyInfoDTO> availableGames;
        synchronized (this.lobbyLock) {
            // available games are filtered and mapped to dtos
            availableGames = this.activeGames.values().stream()
                    .filter(gc -> !gc.isStarted() && gc.getCurrentPlayersCount() < gc.getTargetPlayersCount())
                    .map(gc -> new LobbyInfoDTO(
                            gc.getGameId(),
                            gc.getCurrentPlayersCount(),
                            gc.getPlayersNicknames(),
                            gc.getTargetPlayersCount()
                    ))
                    .collect(Collectors.toList());
        }
        return new AvailableLobbiesEvent(availableGames);
    }

    /**
     * Handles a lobby command received from the network.
     *
     * @param command  The lobby command to be executed.
     * @param nickname The nickname of the player issuing the command.
     */
    public void handleLobbyCommand(LobbyCommand command, String nickname) {
        VirtualView view;
        synchronized (this.lobbyLock) {
            // virtual view is retrieved from connected players map
            view = this.connectedPlayers.get(nickname);
        }
        if (view != null) {
            // command is executed
            command.execute(this, view, nickname);
        }
    }

    /**
     * Routes a game command received from the network to the appropriate game controller.
     *
     * @param command  The game command to be executed.
     * @param nickname The nickname of the player issuing the command.
     */
    public void handleGameCommand(GameCommand command, String nickname) {
        GameController game;
        synchronized (this.lobbyLock) {
            // game controller is retrieved from player game map
            game = this.playerGameMap.get(nickname);
        }
        if (game != null) {
            // command is enqueued in the target game controller
            game.enqueueCommand(command, nickname);
        }
    }
}
