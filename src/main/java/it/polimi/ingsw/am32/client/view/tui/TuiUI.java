package it.polimi.ingsw.am32.client.view.tui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.UI;
import it.polimi.ingsw.am32.client.view.tui.handlers.*;
import it.polimi.ingsw.am32.client.view.tui.components.*;
import it.polimi.ingsw.am32.client.view.tui.scenes.*;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Text user interface implementation handling both sequential and fullscreen interaction.
 */
public class TuiUI implements UI {
    private final ClientController controller;
    private boolean isRunning;
    private boolean isConnected;
    private boolean isWaitingResponse;
    private Terminal terminal;
    private LineReader lineReader;
    private TuiState currentState;
    private final Map<TuiState, Map<String, InputHandler>> commandRegistry;

    // tui components
    private ScreenManager screenManager;
    private HeaderPanel headerPanel;
    private LogPanel logPanel;
    private BoardPanel boardPanel;
    private SidePanel sidePanel;
    private LegendPanel legendPanel;
    private InGameScene inGameScene;
    private EndGameScene endGameScene;

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

        // fullscreen components are initialized
        this.screenManager = new ScreenManager(terminal);
        this.screenManager.setLineReader(lineReader);
        this.headerPanel = new HeaderPanel();
        this.logPanel = new LogPanel(3);
        this.boardPanel = new BoardPanel();
        this.sidePanel = new SidePanel();
        this.legendPanel = new LegendPanel();

        // legend panel is injected into scene
        this.inGameScene = new InGameScene(headerPanel, boardPanel, sidePanel, logPanel, legendPanel);
        this.endGameScene = new EndGameScene();
        this.isRunning = true;

        while (isRunning) {
            try {
                if (!isConnected) {
                    // nickname is requested directly
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
                // termination is triggered
                this.isRunning = false;
            } catch (InterruptedException e) {
                // thread interruption is safely ignored
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
        if (currentState == null) {
            return "> ";
        }
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
            printOrLog("[ERROR] command not recognized in this state.");
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
            printOrLog("[ERROR] " + e.getMessage());
        }
    }

    /**
     * Message is intelligently printed sequentially or routed to the log panel.
     *
     * @param message the text to be displayed.
     */
    private void printOrLog(String message) {
        if (currentState == null || currentState == TuiState.MENU || currentState == TuiState.LOBBY) {
            // sequential print is executed for cli states
            if (lineReader != null && lineReader.isReading()) {
                lineReader.printAbove(message);
            } else {
                System.out.println(message);
            }
        } else {
            // message is pushed to log panel for fullscreen states
            logPanel.addMessage(message);
            screenManager.render();
        }
    }

    // UI interface implementation

    @Override
    public void showConnectionResult(ConnectionResultEvent event) {
        if (event.isSuccess()) {
            // state is updated
            this.currentState = TuiState.MENU;
            this.isConnected = true;
            printOrLog("Connection successful. Welcome!");
        } else {
            printOrLog("Connection failed: " + event.getMessage());
        }

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    @Override
    public void updateAvailableGames(AvailableLobbiesEvent event) {
        printOrLog("=== AVAILABLE GAMES ===");
        event.getLobbies().forEach(g -> printOrLog("ID: " + g.getGameId() + " | Players: " + g.getCurrentPlayersCount() + "/" + g.getTargetPlayers()));
    }

    @Override
    public void updateLobbyRoom(LobbyStateUpdatedEvent event) {
        // state is updated
        this.currentState = TuiState.LOBBY;

        // info dto is extracted
        var info = event.getLobbyInfo();

        // formatted string is printed using the info dto
        printOrLog("Game ID: " + info.getGameId() + " | Players: " + info.getCurrentPlayersCount() + "/" + info.getTargetPlayers());

        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    @Override
    public void showPlayerFocus(String nickname) {
        // focus is updated on the side panel
        sidePanel.setFocus(nickname);
        // screen is re-rendered
        screenManager.render();
    }

    @Override
    public void showGameStarted(GameStartedEvent event) {
        // state is updated
        this.currentState = TuiState.IN_GAME;

        var state = controller.getLightGameState();
        // panel data is populated
        headerPanel.updateData(state.getCurrentPhase().toString(), state.getCurrentRound(), state.getCurrentEra());
        boardPanel.updateData(state);
        sidePanel.updateData(state);

        // scene is activated and rendered
        screenManager.setActiveScene(inGameScene);
        printOrLog("Game started. Turn of " + event.getNextPlayer() + ".");
    }

    @Override
    public void restoreGameState(GameStateEvent event) {
        // state is updated
        this.currentState = TuiState.IN_GAME;

        var state = controller.getLightGameState();
        // panel data is populated
        headerPanel.updateData(state.getCurrentPhase().toString(), state.getCurrentRound(), state.getCurrentEra());
        boardPanel.updateData(state);
        sidePanel.updateData(state);

        // scene is activated and rendered
        screenManager.setActiveScene(inGameScene);
        printOrLog("Game state restored.");
    }

    @Override
    public void updateGamePhase(GamePhaseChangedEvent event) {
        var state = controller.getLightGameState();
        // header is updated
        headerPanel.updateData(event.getPhase().toString(), state.getCurrentRound(), state.getCurrentEra());
        printOrLog("Phase changed to " + event.getPhase() + ". Turn of " + event.getNextPlayer() + ".");
    }

    @Override
    public void showRoundSummary(RoundEndedEvent event) {
        printOrLog("Round " + event.getRound() + " ended.");
    }

    @Override
    public void showGameResults(GameEndedEvent event) {
        // state is updated
        this.currentState = TuiState.END_GAME;

        // winners are passed to the end game scene
        endGameScene.setWinners(event.getResults().getWinners());

        // scene is switched and rendered
        screenManager.setActiveScene(endGameScene);
        screenManager.render();
    }

    @Override
    public void showLobbyError(LobbyErrorEvent event) {
        // error message is formatted and printed in red
        printOrLog("\033[31m[ERROR] " + event.getReason() + ": " + event.getErrorMessage() + "\033[0m");
        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }

    @Override
    public void showTotemPlaced(TotemPlacedEvent event) {
        // message is printed in green
        printOrLog("\033[32mTotem placed by " + event.getPlayer() + " on tile " + event.getOfferTileId() + ".\033[0m");
    }

    @Override
    public void showActionResolved(ActionResolvedEvent event) {
        // message is printed in cyan
        printOrLog("\033[36mAction resolved by " + event.getPlayer() + ".\033[0m");
    }

    @Override
    public void showEventResolved(EventResolvedEvent event) {
        // message is printed in cyan
        printOrLog("\033[36mEvent " + event.getEventCardId() + " resolved.\033[0m");
    }

    @Override
    public void showExtraAcquisition(ExtraAcquisitionResolvedEvent event) {
        // message is printed in cyan
        printOrLog("\033[36mExtra card acquired by " + event.getPlayer() + ".\033[0m");
    }

    @Override
    public void showError(GameErrorEvent event) {
        // error message is printed in red
        printOrLog("\033[31m[ERROR] " + event.getErrorMessage() + "\033[0m");
    }

    @Override
    public void showPlayerDisconnected(PlayerDisconnectedEvent event) {
        // message is printed in yellow
        printOrLog("\033[33mPlayer disconnected: " + event.getPlayer() + "\033[0m");
    }

    @Override
    public void showPlayerReconnected(PlayerReconnectedEvent event) {
        // message is printed in green
        printOrLog("\033[32mPlayer reconnected: " + event.getPlayer() + "\033[0m");
    }

    @Override
    public void showGameSuspended(GameSuspendedEvent event) {
        // state is updated
        this.currentState = TuiState.SUSPENDED;
        // message is printed in yellow
        printOrLog("\033[33mGame suspended. Waiting for players...\033[0m");
    }

    @Override
    public void showGameResumed(GameResumedEvent event) {
        // state is updated
        this.currentState = TuiState.IN_GAME;
        printOrLog("Game resumed.");
    }

    @Override
    public void notifyDisconnection(String message) {
        // message is printed in red
        printOrLog("\033[31mConnection lost: " + message + "\033[0m");
        // termination is requested
        this.isRunning = false;
        // wait flag is cleared and thread is notified
        synchronized (this) {
            isWaitingResponse = false;
            notifyAll();
        }
    }
}
