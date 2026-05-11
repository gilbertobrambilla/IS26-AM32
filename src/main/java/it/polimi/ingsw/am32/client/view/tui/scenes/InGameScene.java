package it.polimi.ingsw.am32.client.view.tui.scenes;

import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.view.tui.components.ingame.BoardPanel;
import it.polimi.ingsw.am32.client.view.tui.components.ingame.HeaderPanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.LogPanel;
import it.polimi.ingsw.am32.client.view.tui.components.ingame.SidePanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.LegendPanel;
import org.jline.terminal.Terminal;

/**
 * Scene responsible for rendering the active game layout.
 */
public class InGameScene implements Scene {
    private final HeaderPanel headerPanel;
    private final BoardPanel boardPanel;
    private final SidePanel sidePanel;
    private final LogPanel logPanel;
    private final LegendPanel legendPanel;

    /**
     * The in game scene is instantiated.
     *
     * @param headerPanel the header panel instance.
     * @param boardPanel the board panel instance.
     * @param sidePanel the side panel instance.
     * @param logPanel the log panel instance.
     * @param legendPanel the legend panel instance.
     */
    public InGameScene(HeaderPanel headerPanel, BoardPanel boardPanel, SidePanel sidePanel, LogPanel logPanel, LegendPanel legendPanel) {
        this.headerPanel = headerPanel;
        this.boardPanel = boardPanel;
        this.sidePanel = sidePanel;
        this.logPanel = logPanel;
        this.legendPanel = legendPanel;
    }

    /**
     * The game state is injected into the child panels.
     *
     * @param state the current light game state.
     */
    public void updateData(LightGameState state) {
        // game state components are extracted and forwarded
        if (state != null) {
            String phase = state.getCurrentPhase() != null ? state.getCurrentPhase().name() : "WAITING";
            headerPanel.updateData(phase, state.getCurrentRound(), state.getCurrentEra());
            boardPanel.updateData(state);
            sidePanel.updateData(state);
        }
    }

    @Override
    public void draw(Terminal terminal, int width, int height) {
        // fixed layout bounds are defined
        int headerHeight = 3;
        int inputHeight = 2;
        int sideWidth = Math.min(45, width / 3);
        int boardWidth = width - sideWidth;

        // log panel height is increased to 8 to accommodate the help command and borders
        int logHeight = 8;

        // main height is calculated dynamically
        int mainHeight = height - headerHeight - logHeight - inputHeight;

        // main panels are drawn
        headerPanel.draw(terminal, 1, 1, width, headerHeight);
        boardPanel.draw(terminal, 1, headerHeight + 1, boardWidth, mainHeight);
        sidePanel.draw(terminal, boardWidth + 1, headerHeight + 1, sideWidth, mainHeight);

        // bottom section is split between log and legend
        logPanel.draw(terminal, 1, height - logHeight - inputHeight + 1, boardWidth, logHeight);
        legendPanel.draw(terminal, boardWidth + 1, height - logHeight - inputHeight + 1, sideWidth, logHeight);
    }
}
