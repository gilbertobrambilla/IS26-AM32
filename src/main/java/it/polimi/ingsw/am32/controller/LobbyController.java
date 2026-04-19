package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.messages.lobby.events.AvailableGamesListEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyStateUpdatedEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.models.GameInfoDTO;
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
                // available lobby list is sent to the newly connected player
                view.notifyLobbyState(buildAvailableGamesListEvent());
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

        // lobby is notified of the newly created room via delta update
        broadcastRoomUpdate(gameController);
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
            // capacity and game state are checked to prevent race conditions
            if (game.getCurrentPlayersCount() < game.getTargetPlayersCount() && !game.isStarted()) {
                // player is mapped to game
                this.playerGameMap.put(nickname, game);
                // player is removed from lobby
                this.connectedPlayers.remove(nickname);

                // player is added to game
                Player player = new Player(nickname);
                game.addPlayerToGame(nickname, view, player);

                if (game.getCurrentPlayersCount() == game.getTargetPlayersCount()) {
                    // room full update is broadcasted so clients hide the room
                    broadcastRoomUpdate(game);
                    // game initialization is triggered
                    game.startGame();
                } else {
                    // delta update is broadcasted to lobby
                    broadcastRoomUpdate(game);
                }
            } else {
                // connection is rejected due to race condition or full game
                view.notifyConnectionResult(false, "game is full or already started");
            }
        }
    }

    /**
     * A network drop event is handled by notifying the relevant game or destroying an unstarted game.
     *
     * @param nickname The nickname of the disconnected player.
     */
    public void handlePlayerNetworkDrop(String nickname) {
        // player is removed from lobby
        this.connectedPlayers.remove(nickname);

        GameController game = this.playerGameMap.get(nickname);
        if (game != null) {
            if (!game.isStarted()) {
                // unstarted game is destroyed and remaining players are notified
                destroyUnstartedGame(game);
            } else {
                // disconnect task is enqueued in game controller
                game.enqueueSystemTask(() -> game.processDisconnect(nickname));
            }
        }
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
        // no broadcast is needed as the game was already hidden from lobby
    }

    /**
     * An unstarted game is destroyed and remaining players are notified and returned to the lobby.
     *
     * @param game The unstarted game to destroy.
     */
    private void destroyUnstartedGame(GameController game) {
        int gameId = game.getGameId();

        // game is removed from active games
        this.activeGames.remove(gameId);

        // game controller is notified to inform its active clients
        game.enqueueSystemTask(game::destroyPregame);

        // player mappings for this game are cleared
        this.playerGameMap.entrySet().removeIf(entry -> entry.getValue().equals(game));

        // lobby is notified via delta that the room is empty and destroyed
        LobbyStateUpdatedEvent dto = new LobbyStateUpdatedEvent(gameId, java.util.Collections.emptyList(), game.getTargetPlayersCount());
        for (VirtualView view : this.connectedPlayers.values()) {
            view.notifyLobbyRoomUpdated(dto);
        }
    }

    /**
     * A delta update regarding a specific game room is broadcasted to all players in the lobby.
     *
     * @param game The game controller associated with the updated room.
     */
    private void broadcastRoomUpdate(GameController game) {
        // room update event is built using the game's current state
        LobbyStateUpdatedEvent dto = new LobbyStateUpdatedEvent(game.getGameId(), game.getPlayersNicknames(), game.getTargetPlayersCount());

        // event is pushed to all players currently in the lobby
        for (VirtualView view : this.connectedPlayers.values()) {
            view.notifyLobbyRoomUpdated(dto);
        }
    }

    /**
     * The complete list of available games is built for newly connected players.
     *
     * @return The built AvailableGamesListEvent DTO.
     */
    private AvailableGamesListEvent buildAvailableGamesListEvent() {
        // available games are filtered and mapped to dtos
        List<GameInfoDTO> availableGames = this.activeGames.values().stream()
                .filter(gc -> !gc.isStarted() && gc.getCurrentPlayersCount() < gc.getTargetPlayersCount())
                .map(gc -> new GameInfoDTO(gc.getGameId(), gc.getCurrentPlayersCount(), gc.getTargetPlayersCount()))
                .collect(Collectors.toList());

        return new AvailableGamesListEvent(availableGames);
    }
}
