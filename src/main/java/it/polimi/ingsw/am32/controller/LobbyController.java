package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.model.GameCreator;
import it.polimi.ingsw.am32.model.GameResult;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.LobbyErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidLobbyActionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

/**
 * Controller class responsible for managing the lobby, player connections, and game creation.
 */
public class LobbyController implements GameLifecycleCallback, Runnable {
    private final Map<String, VirtualView> connectedPlayers;
    private final Map<Integer, GameController> activeGames;
    private final Map<String, GameController> playerGameMap;
    private final Map<String, String> connectionIdToNickname;
    private int nextGameId;
    private final Object lobbyLock;
    private final BlockingQueue<PrioritizedTask> commandQueue;

    /**
     * A new lobby controller is constructed with default collections and initial game id.
     */
    public LobbyController() {
        this.connectedPlayers = new HashMap<>();
        this.activeGames = new HashMap<>();
        this.playerGameMap = new HashMap<>();
        this.connectionIdToNickname = new HashMap<>();
        this.nextGameId = 1;
        this.lobbyLock = new Object();
        // command queue is initialized
        this.commandQueue = new PriorityBlockingQueue<>();
        // thread is started
        new Thread(this).start();
    }

    /**
     * The termination of a game is processed.
     * Data structures are cleared.
     *
     * @param gameId the id of the ended game
     * @param result the final result of the game
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
     * The abortion of an abandoned game is processed.
     * Data structures are cleared.
     *
     * @param gameId the id of the abandoned game
     */
    @Override
    public void abortGame(int gameId) {
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
     * The main execution loop for the lobby controller thread is defined.
     * Tasks from the command queue are processed sequentially based on priority.
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
                    // task execution failure is logged
                    LogManager.getInstance().log(LogLevel.ERROR, "[LobbyController] task execution failed in lobby", e);
                }
            }
        } catch (InterruptedException e) {
            // thread interruption is handled
            Thread.currentThread().interrupt();
        }
    }

    /**
     * An internal system task is added to the execution queue.
     *
     * @param task the runnable task to be executed
     */
    public void enqueueSystemTask(Runnable task) {
        // internal system task is enqueued with high priority
        this.commandQueue.add(new PrioritizedTask(1, task));
    }

    /**
     * A player login request is processed and entrance into the lobby is handled.
     *
     * @param nickname the requested nickname
     * @param rawView the raw virtual view associated with the connection
     */
    public void processLogin(String nickname, VirtualView rawView) {
        // raw view is wrapped in the asynchronous adapter
        VirtualView view = new AsyncVirtualView(rawView);

        // task is enqueued
        this.enqueueSystemTask(() -> {
            GameController game;
            // connection id is retrieved
            String connectionID = view.getConnectionID();

            synchronized (this.lobbyLock) {
                game = this.playerGameMap.get(nickname);

                if (game != null) {
                    if (game.isPlayerActive(nickname)) {
                        // rejection result is notified
                        view.notifyLoginResult(new LoginResultEvent(false, "nickname already taken"));
                        return;
                    }
                    // map is updated with new connection id
                    this.connectionIdToNickname.put(connectionID, nickname);
                } else {
                    // player is added to lobby if absent
                    if (!this.connectedPlayers.containsKey(nickname)) {
                        this.connectedPlayers.put(nickname, view);
                        // map is updated with connection id
                        this.connectionIdToNickname.put(connectionID, nickname);
                    } else {
                        // rejection result is notified
                        view.notifyLoginResult(new LoginResultEvent(false, "nickname already taken"));
                        return;
                    }
                }
            }

            if (game != null) {
                // reconnect task is enqueued
                game.enqueueSystemTask(() -> game.processReconnect(nickname, view));
            } else {
                // successful login result is notified
                view.notifyLoginResult(new LoginResultEvent(true, "login successful"));
                // available lobby list is broadcasted
                view.notifyAvailableLobbies(buildAvailableLobbiesEvent());
            }
        });
    }

    /**
     * A new game creation request is processed.
     *
     * @param nickname the nickname of the creator
     * @param view the player connection view
     * @param numOfPlayers the targeted number of players for the new game
     */
    public void processCreateGame(String nickname, VirtualView view, int numOfPlayers) {
        int gameId;
        synchronized (this.lobbyLock) {
            // game id is retrieved and incremented
            gameId = this.nextGameId++;
        }

        // game creator is instantiated
        GameCreator gameCreator = new GameCreator(gameId);
        GameState gameState;

        try {
            // game state is instantiated via creator
            gameState = gameCreator.createGame(numOfPlayers);
        } catch (InvalidLobbyActionException e) {
            // domain-specific lobby error is notified
            view.notifyLobbyError(new LobbyErrorEvent(e.getReason(), e.getMessage()));
            return;
        } catch (RuntimeException e) {
            // exception is logged with stack trace
            LogManager.getInstance().log(
                    LogLevel.ERROR,
                    "[LobbyController - processCreateGame] critical error during game creation",
                    e
            );
            // internal error is notified for unexpected failures
            view.notifyLobbyError(new LobbyErrorEvent(LobbyErrorReason.JOIN_FAILED, "internal error during game creation"));
            return;
        }

        // game controller is instantiated
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
        gameController.enqueueSystemTask(() -> gameController.processAddPlayerToGame(nickname, view, player, this::notifyAvailableLobbies));
        // waiting room update task is enqueued
        gameController.enqueueSystemTask(gameController::notifyLobbyRoomUpdate);
        // controller thread is started
        new Thread(gameController).start();
    }

    /**
     * A game join request is processed asynchronously by delegating validation to the game controller.
     *
     * @param nickname the nickname of the player joining
     * @param view the player connection view
     * @param gameId the id of the target game
     */
    public void processJoinGame(String nickname, VirtualView view, int gameId) {
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
                this.notifyAvailableLobbies();
            }));
        } else {
            // error is notified if game id is not found
            view.notifyLobbyError(new LobbyErrorEvent(LobbyErrorReason.GAME_NOT_FOUND, "game id " + gameId + " not found"));
        }
    }

    /**
     * A lobby command received from the network is handled.
     *
     * @param command the lobby command to be executed
     * @param connectionID the unique identifier of the connection issuing the command
     */
    public void processLobbyCommand(LobbyCommand command, String connectionID) {
        // task is enqueued
        this.enqueueSystemTask(() -> {
            VirtualView view;
            String nickname;
            synchronized (this.lobbyLock) {
                // nickname is resolved from connection id
                nickname = this.connectionIdToNickname.get(connectionID);
                if (nickname == null) {
                    return;
                }
                // virtual view is retrieved from connected players map
                view = this.connectedPlayers.get(nickname);
            }
            if (view != null) {
                // command is executed
                command.execute(this, view, nickname);
            }
        });
    }

    /**
     * A game command received from the network is routed to the appropriate game controller.
     *
     * @param command the game command to be executed
     * @param connectionID the unique identifier of the connection issuing the command
     */
    public void processGameCommand(GameCommand command, String connectionID) {
        // task is enqueued
        this.enqueueSystemTask(() -> {
            GameController game;
            String nickname;
            synchronized (this.lobbyLock) {
                // nickname is resolved from connection id
                nickname = this.connectionIdToNickname.get(connectionID);
                if (nickname == null) {
                    return;
                }
                // game controller is retrieved from player game map
                game = this.playerGameMap.get(nickname);
            }
            if (game != null) {
                // command is enqueued in the target game controller
                game.enqueueCommand(command, nickname);
            }
        });
    }

    /**
     * A network drop event is handled by notifying the relevant game or managing an unstarted game.
     *
     * @param connectionID the unique identifier of the dropped connection
     */
    public void processPlayerNetworkDrop(String connectionID) {
        // task is enqueued
        this.enqueueSystemTask(() -> {
            String nickname;
            GameController game;

            synchronized (this.lobbyLock) {
                // nickname is retrieved and removed from translation map
                nickname = this.connectionIdToNickname.remove(connectionID);

                if (nickname == null) {
                    // player was anonymous, no further action is required
                    return;
                }

                // view is retrieved from connected players
                VirtualView view = this.connectedPlayers.get(nickname);

                // view id is checked to prevent ghost disconnects in the lobby
                if (view != null && view.getConnectionID().equals(connectionID)) {
                    // player is removed from lobby
                    this.connectedPlayers.remove(nickname);
                    // adapter is destroyed to free up the thread
                    view.destroy();
                }

                // game controller is retrieved from player game map
                game = this.playerGameMap.get(nickname);
            }

            if (game != null) {
                if (!game.isStarted()) {
                    // pregame disconnect task is enqueued with cleanup callback
                    game.enqueueSystemTask(() -> game.processPregameDisconnect(nickname, () -> {
                        synchronized (this.lobbyLock) {
                            // player is removed from player game map
                            this.playerGameMap.remove(nickname);

                            if (game.getCurrentPlayersCount() == 0) {
                                // game is removed from active games
                                this.activeGames.remove(game.getGameId());
                                // player mappings for this game are cleared
                                this.playerGameMap.entrySet().removeIf(entry -> entry.getValue().equals(game));
                            }
                        }

                        // available games list is updated
                        notifyAvailableLobbies();
                    }));
                } else {
                    // disconnect task is enqueued
                    game.enqueueSystemTask(() -> game.processDisconnect(nickname));
                }
            }
        });
    }

    /**
     * The updated list of available lobbies is broadcasted to all players in the lobby menu.
     */
    private void notifyAvailableLobbies() {
        // available lobby list event is built
        AvailableLobbiesEvent event = buildAvailableLobbiesEvent();
        List<VirtualView> viewsToNotify;

        synchronized (this.lobbyLock) {
            // snapshot of connected players is taken
            viewsToNotify = new ArrayList<>(this.connectedPlayers.values());
        }

        // event is pushed to all players currently in the menu
        for (VirtualView view : viewsToNotify) {
            view.notifyAvailableLobbies(event);
        }
    }

    /**
     * The complete list of available lobbies for newly connected players is built.
     *
     * @return the available lobbies event
     */
    private AvailableLobbiesEvent buildAvailableLobbiesEvent() {
        List<LobbyInfoDTO> availableLobbies;
        synchronized (this.lobbyLock) {
            // available lobbies are filtered and mapped to dto
            availableLobbies = this.activeGames.values().stream()
                    .filter(gc -> !gc.isStarted() && gc.getCurrentPlayersCount() < gc.getTargetPlayersCount())
                    .map(gc -> new LobbyInfoDTO(
                            gc.getGameId(),
                            gc.getCurrentPlayersCount(),
                            gc.getPlayersNicknames(),
                            gc.getTargetPlayersCount()
                    ))
                    .collect(Collectors.toList());
        }
        return new AvailableLobbiesEvent(availableLobbies);
    }
}
