package it.polimi.ingsw.am32.client.view.tui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.UI;
import it.polimi.ingsw.am32.client.view.tui.components.global.LegendPanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.LogPanel;
import it.polimi.ingsw.am32.client.view.tui.components.ingame.BoardPanel;
import it.polimi.ingsw.am32.client.view.tui.components.ingame.HeaderPanel;
import it.polimi.ingsw.am32.client.view.tui.components.ingame.SidePanel;
import it.polimi.ingsw.am32.client.view.tui.handlers.*;
import it.polimi.ingsw.am32.client.view.tui.scenes.*;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.log.LogObserver;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Text user interface implementation handling fully drawn scenes and interaction.
 */
public class TuiUI implements UI, LogObserver {
    private final ClientController controller;
    private boolean isRunning;
    private boolean isConnected;
    private boolean isWaitingResponse;
    private Terminal terminal;
    private LineReader lineReader;
    private TuiState currentState;
    private final Map<TuiState, Map<String, InputHandler>> commandRegistry;

    // global tui components
    private ScreenManager screenManager;
    private LogPanel logPanel;
    private LegendPanel legendPanel;

    // scenes
    private MenuScene menuScene;
    private LobbyScene lobbyScene;
    private InGameScene inGameScene;
    private EndGameScene endGameScene;

    /**
     * Enumeration representing the possible reasons for tui termination.
     */
    public enum ExitReason {
        USER_QUIT,
        CONNECTION_LOST
    }

    private ExitReason exitReason = null;
    private Thread mainUiThread;
    private final AtomicBoolean forceRefreshPrompt = new AtomicBoolean(false);

    /**
     * The tui ui is instantiated.
     *
     * @param controller the client controller.
     */
    public TuiUI(ClientController controller) {
        this.controller = controller;
        this.isRunning = false;
        this.isConnected = false;
        this.currentState = null;
        this.commandRegistry = new EnumMap<>(TuiState.class);

        // registries are initialized
        initializeRegistries();

        // ui reference is passed to controller
        this.controller.setUI(this);
    }

    /**
     * The command registries are populated for each state.
     */
    private void initializeRegistries() {
        InputHandler quit = new QuitHandler();

        Map<String, InputHandler> menuRegistry = new HashMap<>();
        menuRegistry.put("create", new CreateGameHandler());
        menuRegistry.put("join", new JoinGameHandler());
        menuRegistry.put("quit", quit);

        Map<String, InputHandler> inGameRegistry = new HashMap<>();
        inGameRegistry.put("place", new PlaceHandler());
        inGameRegistry.put("take", new TakeHandler());
        inGameRegistry.put("stats", new StatsHandler());
        inGameRegistry.put("quit", quit);
        // help handler is omitted pending future implementation

        commandRegistry.put(TuiState.MENU, menuRegistry);
        commandRegistry.put(TuiState.LOBBY, Map.of("quit", quit));
        commandRegistry.put(TuiState.IN_GAME, inGameRegistry);
        commandRegistry.put(TuiState.SUSPENDED, Map.of("quit", quit));
        commandRegistry.put(TuiState.END_GAME, Map.of("quit", quit));
    }

    /**
     * Main ui loop is started and jline components are initialized.
     */
    public void start() {
        // main thread reference is saved
        this.mainUiThread = Thread.currentThread();

        try {
            // terminal is built
            this.terminal = TerminalBuilder.builder().system(true).build();
            // line reader is built
            this.lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        } catch (IOException e) {
            // fallback error is printed
            System.err.println("terminal initialization failed: " + e.getMessage());
            return;
        }

        // observer is registered with log manager
        LogManager.getInstance().addObserver(this);

        // global components are initialized
        this.screenManager = new ScreenManager(terminal);
        this.screenManager.setLineReader(lineReader);
        this.logPanel = new LogPanel(6);
        this.legendPanel = new LegendPanel();

        // specific game components are initialized
        HeaderPanel headerPanel = new HeaderPanel();
        BoardPanel boardPanel = new BoardPanel();
        SidePanel sidePanel = new SidePanel();

        // scenes are constructed with shared components
        this.menuScene = new MenuScene(logPanel, legendPanel);
        this.lobbyScene = new LobbyScene(logPanel, legendPanel);
        this.inGameScene = new InGameScene(headerPanel, boardPanel, sidePanel, logPanel, legendPanel);
        this.endGameScene = new EndGameScene();

        this.isRunning = true;

        while (isRunning) {
            try {
                if (!isConnected) {
                    // nickname is requested sequentially before scene take-over
                    String nickname = lineReader.readLine("Insert your nickname: ").trim();
                    if (!nickname.isEmpty()) {
                        // wait flag is set
                        isWaitingResponse = true;
                        // connection is requested
                        controller.requestConnect(nickname);
                        // thread is paused if response is still pending
                        synchronized (this) {
                            if (isWaitingResponse) {
                                wait(10000);
                            }
                        }
                    }
                } else {
                    // dynamic prompt is generated
                    String prompt = getDynamicPrompt();
                    // user input is read
                    String input = lineReader.readLine(prompt).trim();
                    if (!input.isEmpty()) {
                        // command is parsed and executed
                        parseCommand(input);
                        // ui thread is paused if waiting for server response
                        synchronized (this) {
                            if (isWaitingResponse) {
                                wait(10000);
                            }
                        }
                    }
                }
            } catch (UserInterruptException | EndOfFileException e) {
                // programmatic interrupt for prompt refresh is handled atomically
                if (this.forceRefreshPrompt.compareAndSet(true, false)) {
                    // terminal is wiped and redrawn cleanly before the next readLine
                    screenManager.render();
                    continue;
                }

                // exit reason is preserved if already set by network drop
                if (this.exitReason == null) {
                    // exit reason is set to user quit
                    this.exitReason = ExitReason.USER_QUIT;
                }
                // loop is terminated
                this.isRunning = false;
            } catch (InterruptedException e) {
                // programmatic interrupt during wait is handled safely
                if (this.forceRefreshPrompt.compareAndSet(true, false)) {
                    // terminal is wiped and redrawn cleanly before the next readLine
                    screenManager.render();
                    continue;
                }
                // thread interruption is restored for unexpected interrupts
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Dynamic prompt string is generated.
     *
     * @return the formatted prompt string.
     */
    private String getDynamicPrompt() {
        if (currentState == null) return "> ";
        switch (currentState) {
            case MENU: return "[MENU] > ";
            case LOBBY: return "[LOBBY] > ";
            case IN_GAME: return "> ";
            case SUSPENDED: return "[SUSPENDED] > ";
            case END_GAME: return "[GAME OVER] > ";
            default: return "> ";
        }
    }

    /**
     * The user input is parsed and routed via the command registry.
     *
     * @param input the raw user input.
     */
    private void parseCommand(String input) {
        String[] tokens = input.split("\\s+");
        String commandKeyword = tokens[0].toLowerCase();

        Map<String, InputHandler> currentRegistry = commandRegistry.get(currentState);

        if (currentRegistry == null || !currentRegistry.containsKey(commandKeyword)) {
            // error for unknown command is logged
            LogManager.getInstance().log(LogLevel.ERROR, "[INPUT] Command not found: " + commandKeyword);
            return;
        }

        try {
            // wait flag is set for commands requiring server confirmation
            if ("create".equals(commandKeyword) || "join".equals(commandKeyword)) {
                isWaitingResponse = true;
            }

            InputHandler handler = currentRegistry.get(commandKeyword);
            handler.execute(controller, tokens);
        } catch (IllegalArgumentException e) {
            // wait flag is cleared if local validation fails
            isWaitingResponse = false;
            // handler exception message is logged
            LogManager.getInstance().log(LogLevel.ERROR, "[INPUT] " + e.getMessage());
        }
    }

    /**
     * Internal state is updated and the screen is triggered to re-render.
     *
     * @param newState the target application state.
     */
    private void updateStateAndScene(TuiState newState) {
        // boolean flag evaluates if the prompt text actually needs to change
        boolean promptWillChange = (this.currentState != newState);
        this.currentState = newState;

        // legend is updated with active handlers
        Map<String, InputHandler> currentRegistry = commandRegistry.get(currentState);
        if (currentRegistry != null) {
            legendPanel.updateCommands(currentRegistry.values());
        }

        // active scene is assigned
        switch (currentState) {
            case MENU:
                screenManager.setActiveScene(menuScene);
                break;
            case LOBBY:
                screenManager.setActiveScene(lobbyScene);
                break;
            case IN_GAME:
                // game state is injected into the scene
                inGameScene.updateData(controller.getLightGameState());
                screenManager.setActiveScene(inGameScene);
                break;
            case SUSPENDED:
                // suspended state is handled safely
                break;
            case END_GAME:
                screenManager.setActiveScene(endGameScene);
                break;
        }

        // dynamic prompt is updated in the screen manager
        screenManager.setPrompt(getDynamicPrompt());

        if (promptWillChange && mainUiThread != null && mainUiThread != Thread.currentThread()) {
            // flag is set to distinguish programmatic interrupt from user quit
            this.forceRefreshPrompt.set(true);
            // main ui thread is interrupted to break current readLine and apply new prompt
            mainUiThread.interrupt();
        } else {
            // screen is safely redrawn inline if prompt text hasn't changed
            safeRender();
        }
    }

    /**
     * Screen is safely rendered avoiding conflicts with active line reader.
     */
    private synchronized void safeRender() {
        if (lineReader != null && lineReader.isReading()) {
            // screen is cleared and rendered over the active prompt
            lineReader.callWidget(LineReader.CLEAR);
            screenManager.render();
            lineReader.callWidget(LineReader.REDRAW_LINE);
            lineReader.callWidget(LineReader.REDISPLAY);
            terminal.writer().flush();
        } else {
            // screen is rendered normally
            screenManager.render();
        }
    }

    @Override
    public void onNewLog(LogLevel level, String message) {
        // ansi color is applied based on severity
        String coloredMessage = LogManager.getAnsiColor(level) + message + "\033[0m";

        if (currentState == null) {
            // message is printed above prompt during setup phase
            if (lineReader != null && lineReader.isReading()) {
                lineReader.printAbove(coloredMessage);
            } else {
                System.out.println(coloredMessage);
            }
        } else {
            // message is pushed directly to log panel
            logPanel.addMessage(coloredMessage);
            // screen is safely redrawn
            safeRender();
        }
    }

    /**
     * The reason for the interface termination is retrieved.
     *
     * @return the exit reason.
     */
    public ExitReason getExitReason() {
        // exit reason is returned
        return exitReason;
    }

    /**
     * Voluntary user quit is triggered.
     */
    public void triggerUserQuit() {
        // exit reason is set
        this.exitReason = ExitReason.USER_QUIT;
        // loop is terminated
        this.isRunning = false;

        // waiting threads are unblocked
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    // UI interface implementation

    /**
     * Connection result is processed and displayed on the terminal.
     *
     * @param event the connection result event.
     */
    @Override
    public void showConnectionResult(ConnectionResultEvent event) {
        if (event.isSuccess()) {
            // connection status is set
            this.isConnected = true;

            if (event.isReconnecting()) {
                // reconnection message is logged without changing scene
                LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] Reconnecting to game...");
            } else {
                // welcome message is logged
                LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] " + event.getMessage());
                // state and scene are updated to menu
                updateStateAndScene(TuiState.MENU);
            }
        } else {
            // error message is logged
            LogManager.getInstance().log(LogLevel.ERROR, "[NETWORK] Connection failed: " + event.getMessage());
        }

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    @Override
    public void updateAvailableGames(AvailableLobbiesEvent event) {
        // data list is initialized
        List<MenuScene.MenuLobbyData> lobbyData = new ArrayList<>();

        // event data is mapped to local dtos
        event.getLobbies().forEach(g -> lobbyData.add(
                new MenuScene.MenuLobbyData(g.getGameId(), g.getCurrentPlayersCount(), g.getTargetPlayers())
        ));

        // available games are injected into the menu scene
        menuScene.setAvailableGames(lobbyData);

        // screen is redrawn if the user is in the menu state
        if (currentState == TuiState.MENU) {
            safeRender();
        }
    }

    @Override
    public void updateLobbyRoom(LobbyStateUpdatedEvent event) {
        // info dto is extracted
        var info = event.getLobbyInfo();

        // data is injected into the lobby scene including the player list
        lobbyScene.setLobbyData(
                info.getGameId(),
                info.getCurrentPlayersCount(),
                info.getTargetPlayers(),
                info.getCurrentPlayers(),
                controller.getMyNickname()
        );

        // state and scene are updated
        updateStateAndScene(TuiState.LOBBY);

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    @Override
    public void showPlayerFocus(String nickname) {
        // focus is updated conceptually, screen triggers will be resolved in specific panels
        LogManager.getInstance().log(LogLevel.INFO, "[VIEW] Focus set to: " + (nickname == null ? "none" : nickname));
    }

    /**
     * Game start is shown.
     *
     * @param event the game started event.
     */
    @Override
    public void showGameStarted(GameStartedEvent event) {
        // start message is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Game started. Turn of " + event.getNextPlayer() + ".");
        // state and scene are updated
        updateStateAndScene(TuiState.IN_GAME);

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    /**
     * Game state is restored and rendered on the terminal.
     *
     * @param event the game state event.
     */
    @Override
    public void restoreGameState(GameStateEvent event) {
        // state and scene are updated
        updateStateAndScene(TuiState.IN_GAME);

        // message is logged
        LogManager.getInstance().log(LogLevel.INFO, "[GAME] Game state restored.");

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    @Override
    public void updateGamePhase(GamePhaseChangedEvent event) {
        // phase change is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Phase changed to " + event.getPhase() + ". Turn of " + event.getNextPlayer() + ".");
        // state and scene are updated to refresh all panels
        updateStateAndScene(TuiState.IN_GAME);
    }

    @Override
    public void showRoundSummary(RoundEndedEvent event) {
        // round summary is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Round " + event.getRound() + " ended.");
    }

    @Override
    public void showGameResults(GameEndedEvent event) {
        // state and scene are updated
        endGameScene.setWinners(event.getResults().getWinners());
        updateStateAndScene(TuiState.END_GAME);
    }

    @Override
    public void showLobbyError(LobbyErrorEvent event) {
        // error is logged
        LogManager.getInstance().log(LogLevel.ERROR, "[LOBBY] " + event.getReason() + ": " + event.getErrorMessage());
        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    @Override
    public void showTotemPlaced(TotemPlacedEvent event) {
        // totem placement is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Totem placed by " + event.getPlayer() + " on tile " + event.getOfferTileId() + ".");
    }

    @Override
    public void showActionResolved(ActionResolvedEvent event) {
        // action resolution is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Action resolved by " + event.getPlayer() + ".");
    }

    @Override
    public void showEventResolved(EventResolvedEvent event) {
        // event resolution is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Event " + event.getEventCardId() + " resolved.");
    }

    @Override
    public void showExtraAcquisition(ExtraAcquisitionResolvedEvent event) {
        // extra acquisition is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Extra card acquired by " + event.getPlayer() + ".");
    }

    @Override
    public void showError(GameErrorEvent event) {
        // error is logged
        LogManager.getInstance().log(LogLevel.ERROR, "[GAME] " + event.getErrorMessage());
    }

    @Override
    public void showPlayerDisconnected(PlayerDisconnectedEvent event) {
        // disconnection warning is logged
        LogManager.getInstance().log(LogLevel.WARNING, "[GAME] Player disconnected: " + event.getPlayer());
    }

    @Override
    public void showPlayerReconnected(PlayerReconnectedEvent event) {
        // reconnection is logged
        LogManager.getInstance().log(LogLevel.INFO, "[GAME] Player reconnected: " + event.getPlayer());
    }

    @Override
    public void showGameSuspended(GameSuspendedEvent event) {
        // suspension warning is logged
        LogManager.getInstance().log(LogLevel.WARNING, "[GAME] Game suspended. Waiting for players...");
        // state and scene are updated
        updateStateAndScene(TuiState.SUSPENDED);
    }

    @Override
    public void showGameResumed(GameResumedEvent event) {
        // resumption is logged
        LogManager.getInstance().log(LogLevel.INFO, "[GAME] Game resumed.");
        // state and scene are updated
        updateStateAndScene(TuiState.IN_GAME);
    }

    @Override
    public void notifyDisconnection(String message) {
        // critical error is logged
        LogManager.getInstance().log(LogLevel.ERROR, "[NETWORK] Connection lost: " + message);

        // exit reason is set
        this.exitReason = ExitReason.CONNECTION_LOST;
        // loop is terminated
        this.isRunning = false;

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }

        // delay is added for better user experience before returning to setup
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            // interruption is safely ignored
            Thread.currentThread().interrupt();
        }

        // main ui thread is interrupted to break line reader blocking
        if (this.mainUiThread != null) {
            this.mainUiThread.interrupt();
        }
    }
}
