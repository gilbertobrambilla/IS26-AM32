package it.polimi.ingsw.am32.client.view.tui;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.network.NetworkFactory;
import it.polimi.ingsw.am32.client.view.UI;
import it.polimi.ingsw.am32.client.view.tui.components.global.LegendPanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.LogPanel;
import it.polimi.ingsw.am32.client.view.tui.components.ingame.BoardPanel;
import it.polimi.ingsw.am32.client.view.tui.components.ingame.HeaderPanel;
import it.polimi.ingsw.am32.client.view.tui.components.ingame.SidePanel;
import it.polimi.ingsw.am32.client.view.tui.handlers.*;
import it.polimi.ingsw.am32.client.view.tui.scenes.*;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import it.polimi.ingsw.am32.dto.setup.EventCardDTO;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.log.LogObserver;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.network.client.VirtualServer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Text user interface implementation handling fully drawn scenes and interaction.
 */
public class TuiUI implements UI, LogObserver {
    private final String serverIp;
    private ClientController controller;
    private boolean isRunning;
    private boolean isNetworkConnected;
    private boolean isLoggedIn;
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
     * @param serverIp the server ip address
     */
    public TuiUI(String serverIp) {
        this.serverIp = serverIp;
        this.controller = null;
        this.isRunning = false;
        this.isNetworkConnected = false;
        this.isLoggedIn = false;
        this.currentState = null;
        this.commandRegistry = new EnumMap<>(TuiState.class);

        // registries are initialized
        initializeRegistries();
    }

    /**
     * The command registries are populated for each state.
     */
    private void initializeRegistries() {
        InputHandler quit = new QuitHandler();
        InputHandler help = new HelpHandler();

        Map<String, InputHandler> menuRegistry = new HashMap<>();
        menuRegistry.put("create", new CreateGameHandler());
        menuRegistry.put("join", new JoinGameHandler());
        menuRegistry.put("quit", quit);
        menuRegistry.put("help", help);

        Map<String, InputHandler> inGameRegistry = new HashMap<>();
        inGameRegistry.put("place", new PlaceHandler());
        inGameRegistry.put("resolve", new ResolveHandler());
        inGameRegistry.put("stats", new StatsHandler());
        inGameRegistry.put("quit", quit);
        inGameRegistry.put("help", help);

        commandRegistry.put(TuiState.MENU, menuRegistry);
        commandRegistry.put(TuiState.LOBBY, Map.of("quit", quit, "help", help));
        commandRegistry.put(TuiState.IN_GAME, inGameRegistry);
        commandRegistry.put(TuiState.SUSPENDED, Map.of("quit", quit, "help", help));

        // only quit command is mapped for endgame state
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

        // dictionary is loaded early in the lifecycle using the asset manager
        InputStream dictionaryStream = AssetManager.getInstance().getDictionaryStream("/assets/strings_en.json");
        GameDictionary.getInstance().loadFromJson(dictionaryStream);

        // game assets are loaded into memory
        AssetManager.getInstance().loadAssets();

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
                if (controller == null) {
                    // protocol is requested from user
                    String protocol = lineReader.readLine("Choose connection protocol (1: RMI, 2: Socket): ").trim();
                    VirtualServer virtualServer;

                    // crash callback is defined
                    Runnable onCrash = () -> {
                        if (controller != null) {
                            controller.onFatalError("Server connection lost");
                        }
                    };

                    if (protocol.equals("1")) {
                        LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] Connecting to " + serverIp + " via RMI...");
                        virtualServer = NetworkFactory.createRmiConnection(serverIp, onCrash);
                    } else if (protocol.equals("2")) {
                        LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] Connecting to " + serverIp + " via Socket...");
                        virtualServer = NetworkFactory.createSocketConnection(serverIp, onCrash);
                    } else {
                        LogManager.getInstance().log(LogLevel.WARNING, "[SYSTEM] Invalid protocol selection.");
                        continue;
                    }

                    if (virtualServer == null) {
                        LogManager.getInstance().log(LogLevel.ERROR, "[NETWORK] Connection failed. The server might be offline.");
                        continue;
                    }

                    // controller is instantiated and wired
                    this.controller = new ClientController(virtualServer);
                    virtualServer.setClientController(this.controller);
                    this.controller.setUI(this);

                    // physical connection is requested
                    isWaitingResponse = true;
                    this.controller.requestConnect();

                    // thread is paused if response is still pending
                    synchronized (this) {
                        if (isWaitingResponse) {
                            wait(10000);
                        }
                    }
                } else if (isNetworkConnected && !isLoggedIn) {
                    // nickname is requested sequentially before scene take-over
                    String nickname = lineReader.readLine("Insert your nickname: ").trim();
                    if (!nickname.isEmpty()) {
                        // wait flag is set
                        isWaitingResponse = true;
                        // login is requested
                        controller.requestLogin(nickname);
                        // thread is paused if response is still pending
                        synchronized (this) {
                            if (isWaitingResponse) {
                                wait(10000);
                            }
                        }
                    }
                } else if (isNetworkConnected && isLoggedIn) {
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
     * @return the formatted prompt string
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
     * @param input the raw user input
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
     * The internal state is updated and the screen is triggered to re-render.
     *
     * @param newState the target application state
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
     * Screen is safely rendered avoiding conflicts with the active line reader.
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
     * @return the exit reason
     */
    public ExitReason getExitReason() {
        // exit reason is returned
        return exitReason;
    }

    /**
     * Voluntary user quit is triggered (waiting for server acknowledgment).
     */
    public void triggerUserQuit() {
        // wait flag is set to block until server confirmation
        synchronized (this) {
            isWaitingResponse = true;
        }
    }

    // UI interface implementation

    /**
     * Connection result is processed and displayed in the terminal.
     *
     * @param event the connection result event
     */
    @Override
    public void showConnectionResult(ConnectionResultEvent event) {
        if (event.isSuccess()) {
            // network connection flag is updated
            this.isNetworkConnected = true;
            // success is logged
            LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] Connected to server successfully.");
        } else {
            // error is logged
            LogManager.getInstance().log(LogLevel.ERROR, "[NETWORK] Connection failed.");
            // controller is destroyed to restart flow
            if (this.controller != null) {
                this.controller.shutdown();
                this.controller = null;
            }
        }

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    /**
     * Connection result is processed and displayed in the terminal.
     *
     * @param event the login result event
     */
    @Override
    public void showLoginResult(LoginResultEvent event) {
        if (event.isSuccess()) {
            // connection status is set
            this.isLoggedIn = true;

            // welcome message is logged
            LogManager.getInstance().log(LogLevel.INFO, "[NETWORK] " + event.getMessage());
        } else {
            // error message is logged
            LogManager.getInstance().log(LogLevel.ERROR, "[NETWORK] Login failed: " + event.getMessage());
        }

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    /**
     * Available games list is updated.
     *
     * @param event the available lobbies event
     */
    @Override
    public void updateAvailableLobbies(AvailableLobbiesEvent event) {
        // data list is initialized
        List<MenuScene.MenuLobbyData> lobbyData = new ArrayList<>();

        // event data is mapped to local dto
        event.getLobbies().forEach(g -> lobbyData.add(
                new MenuScene.MenuLobbyData(g.getGameId(), g.getCurrentPlayersCount(), g.getTargetPlayers())
        ));

        // available games are injected into the menu scene
        menuScene.setAvailableGames(lobbyData);

        // state and scene are updated to menu
        updateStateAndScene(TuiState.MENU);
    }

    /**
     * Lobby room state is updated.
     *
     * @param event the lobby room update event
     */
    @Override
    public void updateLobbyRoom(LobbyRoomUpdateEvent event) {
        // info dto is extracted
        var info = event.getLobbyInfo();

        // data is injected into the lobby scene
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

    /**
     * Focus is set on a specific player in the user interface.
     *
     * @param nickname the player nickname
     */
    @Override
    public void showPlayerFocus(String nickname) {
        // focus is injected into the in game scene
        inGameScene.setPlayerFocus(nickname);

        // log message is generated
        LogManager.getInstance().log(LogLevel.INFO, "[VIEW] Focus set to: " + (nickname == null ? "none" : nickname));

        // screen is redrawn if currently in game
        if (currentState == TuiState.IN_GAME) {
            safeRender();
        }
    }

    /**
     * Game start is shown.
     *
     * @param event the game started event
     */
    @Override
    public void showGameStarted(GameStartedEvent event) {
        // start message is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Game started. Turn of " + event.getGameState().getCurrentPlayer() + ".");

        // state and scene are updated
        updateStateAndScene(TuiState.IN_GAME);

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    /**
     * Game state is restored.
     *
     * @param event the game state event
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

    /**
     * Game phase is updated.
     *
     * @param event the game phase changed event
     */
    @Override
    public void updateGamePhase(GamePhaseChangedEvent event) {
        // dictionary instance is retrieved
        GameDictionary dict = GameDictionary.getInstance();

        // raw phase is translated into a user-friendly string
        String translatedPhase = dict.translate(event.getPhase().toString());

        // base message is constructed
        String logMessage = "[GAME] Phase changed to " + translatedPhase;

        // turn info is appended if a player is active
        if (event.getCurrentPlayer() != null && !event.getCurrentPlayer().isEmpty() && !"null".equalsIgnoreCase(event.getCurrentPlayer())) {
            logMessage += ". Turn of " + event.getCurrentPlayer() + ".";
        } else {
            logMessage += ".";
        }

        // phase change is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, logMessage);

        // state and scene are updated to refresh all panels
        updateStateAndScene(TuiState.IN_GAME);
    }

    /**
     * A round summary is shown.
     *
     * @param event the round-ended event
     */
    @Override
    public void showRoundEnded(RoundEndedEvent event) {
        // round summary is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Round " + event.getNewRound() + " started.");
    }

    /**
     * Game results are shown.
     *
     * @param event the game ended event
     */
    @Override
    public void showGameEnded(GameEndedEvent event) {
        // state and scene are updated
        endGameScene.setWinners(event.getResults().getWinners());
        updateStateAndScene(TuiState.END_GAME);
    }

    /**
     * Global leaderboard results are shown.
     *
     * @param event the leaderboard event
     */
    @Override
    public void showLeaderboard(LeaderboardEvent event) {
        // leaderboard results are injected into the scene
        endGameScene.setGlobalLeaderboard(event.getGlobalLeaderboard(), event.getGlobalPosition(), event.isDbAvailable());
        // screen is redrawn
        safeRender();
    }

    /**
     * Join game error is shown.
     *
     * @param event the lobby error event
     */
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

    /**
     * Totem placement is shown.
     *
     * @param event the totem placed event
     */
    @Override
    public void showTotemPlaced(TotemPlacedEvent event) {
        // totem placement is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Totem placed by " + event.getPlayer() + " on tile " + event.getOfferTileId() + ".");
        // scene is redrawn
        updateStateAndScene(TuiState.IN_GAME);
    }

    /**
     * Action resolution is shown.
     *
     * @param event the action resolved event
     */
    @Override
    public void showActionResolved(ActionResolvedEvent event) {
        // action resolution is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Action resolved by " + event.getPlayer() + ".");
        // scene redrawing is skipped to avoid double frames
    }

    /**
     * Event resolution is shown.
     *
     * @param event the event resolved event
     */
    @Override
    public void showEventResolved(EventResolvedEvent event) {
        // event dto is retrieved
        EventCardDTO dto = AssetManager.getInstance().getEventCard(event.getEventCardId());

        // event name is translated
        String eventName = event.getEventCardId();
        if (dto != null) {
            eventName = GameDictionary.getInstance().translate(dto.getEventType());
        }

        // event resolution is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Event " + eventName + " resolved.");
        // scene is redrawn
        updateStateAndScene(TuiState.IN_GAME);
    }

    /**
     * Extra acquisition is shown.
     *
     * @param event the extra acquisition resolved event
     */
    @Override
    public void showExtraAcquisition(ExtraAcquisitionResolvedEvent event) {
        // extra acquisition is logged
        LogManager.getInstance().log(LogLevel.GAME_EVENT, "[GAME] Extra card acquired by " + event.getPlayer() + ".");
        // scene is redrawn
        updateStateAndScene(TuiState.IN_GAME);
    }

    /**
     * Error message is shown.
     *
     * @param event the game error event
     */
    @Override
    public void showGameError(GameErrorEvent event) {
        // error is logged
        LogManager.getInstance().log(LogLevel.ERROR, "[GAME] " + event.getReason() + ": " + event.getErrorMessage());
    }

    /**
     * Player disconnection is shown.
     *
     * @param event the player disconnected event
     */
    @Override
    public void showPlayerDisconnected(PlayerDisconnectedEvent event) {
        // disconnection warning is logged
        LogManager.getInstance().log(LogLevel.WARNING, "[GAME] Player disconnected: " + event.getPlayer());
    }

    /**
     * Player reconnection is shown.
     *
     * @param event the player reconnected event
     */
    @Override
    public void showPlayerReconnected(PlayerReconnectedEvent event) {
        // reconnection is logged
        LogManager.getInstance().log(LogLevel.INFO, "[GAME] Player reconnected: " + event.getPlayer());
    }

    /**
     * Game suspension is shown.
     *
     * @param event the game suspended event
     */
    @Override
    public void showGameSuspended(GameSuspendedEvent event) {
        // suspension warning is logged
        LogManager.getInstance().log(LogLevel.WARNING, "[GAME] Game suspended. Waiting for players...");
        // state and scene are updated
        updateStateAndScene(TuiState.SUSPENDED);
    }

    /**
     * Game resumption is shown.
     *
     * @param event the game resumed event
     */
    @Override
    public void showGameResumed(GameResumedEvent event) {
        // resumption is logged
        LogManager.getInstance().log(LogLevel.INFO, "[GAME] Game resumed.");
        // state and scene are updated
        updateStateAndScene(TuiState.IN_GAME);
    }

    /**
     * Fatal connection loss is shown.
     *
     * @param message the error message
     */
    @Override
    public void showConnectionLost(String message) {
        // critical error is logged
        LogManager.getInstance().log(LogLevel.ERROR, "[NETWORK] Connection lost: " + message);

        // state and connection flags are reset
        this.isNetworkConnected = false;
        this.isLoggedIn = false;
        this.currentState = null;

        // old controller is shut down and discarded
        if (this.controller != null) {
            this.controller.shutdown();
            this.controller = null;
        }

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    /**
     * Voluntary quit confirmation is shown.
     */
    @Override
    public void showQuitConfirmation() {
        // goodbye message is logged
        LogManager.getInstance().log(LogLevel.INFO, "[SYSTEM] Disconnected successfully. Goodbye!");

        // exit reason is set
        this.exitReason = ExitReason.USER_QUIT;
        // loop is terminated
        this.isRunning = false;

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }

        // main ui thread is interrupted to break line reader blocking
        if (this.mainUiThread != null) {
            this.mainUiThread.interrupt();
        }
    }
}
