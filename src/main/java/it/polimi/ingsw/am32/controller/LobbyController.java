package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.state.GameInfoDTO;
import it.polimi.ingsw.am32.model.GameCreator;
import it.polimi.ingsw.am32.model.GameResult;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LobbyController implements GameLifecycleCallback {

    private final ConcurrentMap<String, VirtualView> connectedPlayers;
    private final ConcurrentMap<Integer, GameController> activeGames;
    private final ConcurrentMap<String, GameController> playerGameMap;
    private final AtomicInteger nextGameId;

    public LobbyController() {
        this.connectedPlayers = new ConcurrentHashMap<>();
        this.activeGames = new ConcurrentHashMap<>();
        this.playerGameMap = new ConcurrentHashMap<>();
        this.nextGameId = new AtomicInteger(1);
    }

    /**
     * A new player connection request is processed.
     * Uniqueness of the nickname is verified across both the lobby and active games.
     *
     * @param nickname The requested nickname.
     * @param view     The player's connection view.
     */
    public void connectPlayer(String nickname, VirtualView view) {
        GameController game = this.playerGameMap.get(nickname);

        if (game != null) {
            // player is already in an active game, reconnect task is enqueued
            game.enqueueSystemTask(() -> game.processReconnect(nickname, view));
        } else {
            // player is added to lobby
            if (this.connectedPlayers.putIfAbsent(nickname, view) == null) {
                view.notifyConnectionResult(true, "connection successful");
                // lobby state is broadcasted
                broadcastLobbyState();
            } else {
                view.notifyConnectionResult(false, "nickname is already taken in the lobby");
            }
        }
    }

    /**
     * A new game creation request is processed.
     *
     * @param nickname     The nickname of the creator.
     * @param numOfPlayers The targeted number of players for the new game.
     */
    public void createGame(String nickname, int numOfPlayers) {
        // player view is retrieved
        VirtualView view = this.connectedPlayers.get(nickname);
        if (view == null) return; // player not in lobby

        // game id is incremented
        int gameId = this.nextGameId.getAndIncrement();

        // game and state are instantiated via creator
        GameCreator gameCreator = new GameCreator(gameId);
        GameState gameState = gameCreator.createGame(numOfPlayers);
        GameController gameController = new GameController(gameId, gameState, this);

        // game is mapped
        this.activeGames.put(gameId, gameController);
        this.playerGameMap.put(nickname, gameController);

        // player is removed from lobby
        this.connectedPlayers.remove(nickname);

        // controller thread is started
        new Thread(gameController).start();

        // player is added to game
        Player player = new Player(nickname);
        gameController.addPlayerToGame(nickname, view, player);

        // client is notified
        view.notifyGameStarted(gameId);
        // lobby state is broadcasted
        broadcastLobbyState();
    }

    /**
     * A game join request is processed.
     *
     * @param nickname The nickname of the player joining.
     * @param gameId   The ID of the target game.
     */
    public void joinGame(String nickname, int gameId) {
        VirtualView view = this.connectedPlayers.get(nickname);
        GameController game = this.activeGames.get(gameId);

        if (view != null && game != null) {
            // capacity and game state are checked
            // note: assumed getters for player counts and start status
            if (game.getCurrentPlayersCount() < game.getTargetPlayersCount() && !game.isStarted()) {
                // player is mapped to game
                this.playerGameMap.put(nickname, game);
                // player is removed from lobby
                this.connectedPlayers.remove(nickname);

                // player is added to game
                Player player = new Player(nickname);
                game.addPlayerToGame(nickname, view, player);

                // client is notified
                view.notifyGameStarted(gameId);
                // lobby state is broadcasted
                broadcastLobbyState();
            } else {
                view.notifyConnectionResult(false, "game is full or already started");
            }
        }
    }

    /**
     * A network drop event is handled by notifying the relevant game or removing the player from the lobby.
     *
     * @param nickname The nickname of the disconnected player.
     */
    public void handlePlayerNetworkDrop(String nickname) {
        // player is removed from lobby
        this.connectedPlayers.remove(nickname);

        GameController game = this.playerGameMap.get(nickname);
        if (game != null) {
            // disconnect task is enqueued in game controller
            game.enqueueSystemTask(() -> game.processDisconnect(nickname));
        }
        // lobby state is broadcasted
        broadcastLobbyState();
    }

    /**
     * The termination of a game is processed. Data structures are cleared.
     *
     * @param gameId The ID of the ended game.
     * @param result The final result of the game.
     */
    @Override
    public void endGame(int gameId, GameResult result) {
        // game is removed from active games
        GameController removedGame = this.activeGames.remove(gameId);
        if (removedGame != null) {
            // player mappings are cleared
            this.playerGameMap.entrySet().removeIf(entry -> entry.getValue().equals(removedGame));
        }
        // lobby state is broadcasted
        broadcastLobbyState();
    }

    /**
     * The current state of available games is broadcasted to all players in the lobby.
     */
    private void broadcastLobbyState() {
        // available games are filtered and mapped to dtos
        List<GameInfoDTO> availableGames = this.activeGames.values().stream()
                .filter(gc -> !gc.isStarted() && gc.getCurrentPlayersCount() < gc.getTargetPlayersCount())
                .map(gc -> new GameInfoDTO(gc.getGameId(), gc.getCurrentPlayersCount(), gc.getTargetPlayersCount()))
                .collect(Collectors.toList());

        // lobby state is broadcasted to all waiting players
        for (VirtualView view : this.connectedPlayers.values()) {
            view.notifyLobbyState(availableGames);
        }
    }
}
