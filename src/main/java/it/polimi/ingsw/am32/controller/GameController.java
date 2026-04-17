package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.dto.results.GameResultDTO;
import it.polimi.ingsw.am32.dto.results.LeaderboardEntryDTO;
import it.polimi.ingsw.am32.model.GameResult;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.dao.DatabaseConnection;
import it.polimi.ingsw.am32.model.dao.LeaderboardDAO;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import it.polimi.ingsw.am32.model.exceptions.InvalidActionException;
import it.polimi.ingsw.am32.model.tiles.OfferTile;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

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

    public GameController(int gameId, GameState gameState, GameLifecycleCallback lifecycleCallback) {
        this.gameId = gameId;
        this.gameState = gameState;
        this.lifecycleCallback = lifecycleCallback;
        this.activeClients = new ConcurrentHashMap<>();
        this.commandQueue = new LinkedBlockingQueue<>();
        this.isSuspended = false;
        this.started = false;
    }

    public void addPlayerToGame(String nickname, GameView view, Player player) {
        // player is added to active clients and model
        this.activeClients.put(nickname, new PlayerSession(player, view));
        this.gameState.addPlayer(player);
        this.gameState.addObserver(view);
    }

    public void startGame() {
        // game is initialized and started flag is set
        this.gameState.initGame();
        this.started = true;
        broadcastSnapshot();
    }

    public void enqueueCommand(GameCommand command, String playerNickname) {
        // game command is enqueued for sequential execution
        this.commandQueue.add(() -> command.execute(this, playerNickname));
    }

    public void enqueueSystemTask(Runnable task) {
        // internal system task is enqueued
        this.commandQueue.add(task);
    }

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

    private void autoAdvanceGameIfNecessary() {
        if (this.isSuspended) return;

        boolean advanced = true;
        // phases without user input are resolved automatically
        while (advanced && !this.isSuspended) {
            advanced = false;

            if (this.gameState.getCurrentPhase() == GamePhase.RESOLVE_EVENTS) {
                this.gameState.resolveEvents();
                advanced = true;
                broadcastSnapshot();
            }

            if (this.gameState.getCurrentPhase() == GamePhase.END_ROUND) {
                // endgame conditions are checked
                if (this.gameState.getCurrentRound() == 10 || this.gameState.getTribeDeckSize() == 0) {
                    handleGameEnd();
                    return;
                } else {
                    this.gameState.endRound();
                    advanced = true;
                    broadcastSnapshot();
                }
            }
        }

        // disconnected player turn is evaluated
        checkAndSkipDisconnectedPlayerTurn();
    }

    private void checkAndSkipDisconnectedPlayerTurn() {
        if (this.isSuspended || this.gameState.getCurrentPlayer() == null) return;

        String currentNickname = this.gameState.getCurrentPlayer().getNickname();

        // turn is skipped if current player is not connected
        if (!this.activeClients.containsKey(currentNickname)) {
            this.forceSkipCurrentPlayerTurn();
            broadcastSnapshot();
            // next steps are enqueued
            enqueueSystemTask(this::autoAdvanceGameIfNecessary);
        }
    }

    public void processPlaceTotem(String nickname, String tileId) {
        if (this.isSuspended) return;

        PlayerSession session = this.activeClients.get(nickname);
        if (session != null) {
            try {
                // totem placement is executed
                this.gameState.placeTotem(session.getPlayer().getNickname(), tileId);
                broadcastSnapshot();
                autoAdvanceGameIfNecessary();
            } catch (InvalidActionException e) {
                session.getView().notifyError(e.getReason(), e.getMessage());
            }
        }
    }

    public void processResolveAction(String nickname, List<String> characters, List<String> buildings) {
        if (this.isSuspended) return;

        PlayerSession session = this.activeClients.get(nickname);
        if (session != null) {
            try {
                // action resolution is executed
                this.gameState.resolveAction(session.getPlayer().getNickname(), characters, buildings);
                broadcastSnapshot();
                autoAdvanceGameIfNecessary();
            } catch (InvalidActionException e) {
                session.getView().notifyError(e.getReason(), e.getMessage());
            }
        }
    }

    public void processDisconnect(String nickname) {
        // player session is removed and observers are updated
        PlayerSession session = this.activeClients.remove(nickname);
        if (session != null) {
            this.gameState.removeObserver(session.getView());
            checkSuspendCondition();

            if (!this.isSuspended) {
                autoAdvanceGameIfNecessary();
            }
        }
    }

    public void processReconnect(String nickname, GameView newView) {
        // player is retrieved from model to restore session
        Player reconnectedPlayer = this.gameState.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);

        if (reconnectedPlayer != null) {
            // session is restored and game is resumed if possible
            PlayerSession session = new PlayerSession(reconnectedPlayer, newView);
            this.activeClients.put(nickname, session);
            this.gameState.addObserver(newView);

            synchronized (suspendLock) {
                if (this.isSuspended && this.activeClients.size() >= 2) {
                    this.isSuspended = false;
                    suspendLock.notifyAll();

                    for (PlayerSession s : this.activeClients.values()) {
                        s.getView().notifyGameResumed();
                    }
                }
            }

            broadcastSnapshot();
            if (!isGameSuspended()) {
                autoAdvanceGameIfNecessary();
            }
        }
    }

    private void handleForfeitVictory() {
        if (!this.isSuspended || this.activeClients.isEmpty()) return;
        // game end is triggered due to timeout
        handleGameEnd();
    }

    private void handleGameEnd() {
        GameResult modelResult = this.gameState.calculateResults();
        int numOfPlayers = this.gameState.getPlayers().size();

        List<String> winners = modelResult.getWinners().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());

        Map<String, int[]> matchScores = new HashMap<>();
        for (Map.Entry<Player, int[]> entry : modelResult.getLeaderboard().entrySet()) {
            matchScores.put(entry.getKey().getNickname(), entry.getValue());
        }

        List<LeaderboardEntryDTO> globalRanking = new ArrayList<>();
        Connection conn = null;
        try {
            // leaderboard data is saved and retrieved from database
            conn = DatabaseConnection.getInstance().getConnection();
            LeaderboardDAO leaderboardDAO = new LeaderboardDAO(conn);
            leaderboardDAO.saveMatchResults(matchScores, numOfPlayers);
            globalRanking = leaderboardDAO.fetchLeaderboard(numOfPlayers);
        } catch (Exception e) {
            System.err.println("error during database operations: " + e.getMessage());
        } finally {
            // database connection is closed
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // clients are notified of game end
        for (PlayerSession session : this.activeClients.values()) {
            String currentNickname = session.getPlayer().getNickname();
            int globalPos = calculatePlayerPosition(currentNickname, globalRanking);

            GameResultDTO resultDTO = new GameResultDTO(winners, matchScores, globalRanking, globalPos);
            session.getView().notifyGameEnded(resultDTO);
        }

        // lifecycle callback is invoked to clear resources in lobby
        this.lifecycleCallback.endGame(this.gameId, modelResult);
    }

    private int calculatePlayerPosition(String nickname, List<LeaderboardEntryDTO> ranking) {
        for (LeaderboardEntryDTO entry : ranking) {
            if (entry.getNickname().equals(nickname)) {
                return entry.getPosition();
            }
        }
        return -1;
    }

    private void broadcastSnapshot() {
        // snapshots are sent to all connected views via observer pattern
        // todo
    }

    public void forceSkipCurrentPlayerTurn() {
        Player currentPlayer = this.gameState.getCurrentPlayer();
        if (currentPlayer == null) return;

        String currentNickname = currentPlayer.getNickname();
        GamePhase currentPhase = this.gameState.getCurrentPhase();

        if (currentPhase == GamePhase.PLACE_TOTEMS) {
            List<OfferTile> track = this.gameState.getOfferTrack();
            for (int i = track.size() - 1; i >= 0; i--) {
                if (track.get(i).isFree()) {
                    // totem is placed automatically on available tile
                    this.gameState.placeTotem(currentNickname, track.get(i).getId());
                    return;
                }
            }
        } else if (currentPhase == GamePhase.RESOLVE_ACTIONS || currentPhase == GamePhase.EXTRA_ACQUISITION) {
            // action is forfeited automatically
            this.gameState.forfeitAction(currentNickname);
        }
    }

    // getters for lobby controller

    public int getGameId() {
        return this.gameId;
    }

    public int getCurrentPlayersCount() {
        // total players registered in the game state are returned
        return this.gameState.getPlayers().size();
    }

    public int getTargetPlayersCount() {
        // required players for game start are returned
        return this.gameState.getTargetPlayers();
    }

    public boolean isStarted() {
        return this.started;
    }

    // suspend management

    private boolean isGameSuspended() {
        synchronized (suspendLock) {
            return this.isSuspended;
        }
    }

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
