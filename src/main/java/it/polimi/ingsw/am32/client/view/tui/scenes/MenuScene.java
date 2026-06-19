package it.polimi.ingsw.am32.client.view.tui.scenes;

import it.polimi.ingsw.am32.client.view.tui.components.global.LegendPanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.LogPanel;
import it.polimi.ingsw.am32.client.view.tui.components.menu.MenuPanel;
import org.jline.terminal.Terminal;

import java.util.List;

/**
 * This scene renders the main menu layout and available games grid.
 */
public class MenuScene implements Scene {
    private final LogPanel logPanel;
    private final LegendPanel legendPanel;
    private final MenuPanel gamesPanel;

    /**
     * Dto used to store lobby information internally.
     */
    public static class MenuLobbyData {
        public final int id;
        public final int currentPlayers;
        public final int targetPlayers;

        /**
         * The lobby data is instantiated.
         *
         * @param id the game identifier
         * @param currentPlayers the current number of players
         * @param targetPlayers the target number of players
         */
        public MenuLobbyData(int id, int currentPlayers, int targetPlayers) {
            this.id = id;
            this.currentPlayers = currentPlayers;
            this.targetPlayers = targetPlayers;
        }
    }

    /**
     * The menu scene is instantiated.
     *
     * @param logPanel the shared log panel instance
     * @param legendPanel the shared legend panel instance
     */
    public MenuScene(LogPanel logPanel, LegendPanel legendPanel) {
        this.logPanel = logPanel;
        this.legendPanel = legendPanel;
        this.gamesPanel = new MenuPanel();
    }

    /**
     * The available games list is updated.
     *
     * @param lobbies the list of available lobbies
     */
    public void setAvailableGames(List<MenuLobbyData> lobbies) {
        // data is passed to the component
        gamesPanel.updateData(lobbies);
    }

    /**
     * The main menu scene is drawn.
     *
     * @param terminal the terminal instance
     * @param width the terminal width
     * @param height the terminal height
     */
    @Override
    public void draw(Terminal terminal, int width, int height) {
        // fixed layout bounds are defined
        int inputHeight = 2;
        int logHeight = 7;

        // bottom section widths are defined with restored and increased legend size
        int legendWidth = Math.min(49, width / 3 + 4);
        int logWidth = width - legendWidth;

        int mainHeight = height - logHeight - inputHeight;

        // main games panel is drawn
        gamesPanel.draw(terminal, 1, 1, width, mainHeight);

        // bottom section is split
        logPanel.draw(terminal, 1, height - logHeight - inputHeight + 1, logWidth, logHeight);
        legendPanel.draw(terminal, logWidth + 1, height - logHeight - inputHeight + 1, legendWidth, logHeight);
    }
}
