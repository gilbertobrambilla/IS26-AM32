package it.polimi.ingsw.am32.client.view.tui.scenes;

import it.polimi.ingsw.am32.client.view.tui.components.global.LegendPanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.LogPanel;
import it.polimi.ingsw.am32.client.view.tui.components.menu.MenuPanel;
import org.jline.terminal.Terminal;

import java.util.List;

/**
 * Scene responsible for rendering the main menu layout and the available games grid.
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
         * @param id the game identifier.
         * @param currentPlayers the current number of players.
         * @param targetPlayers the target number of players.
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
     * @param logPanel the shared log panel instance.
     * @param legendPanel the shared legend panel instance.
     */
    public MenuScene(LogPanel logPanel, LegendPanel legendPanel) {
        this.logPanel = logPanel;
        this.legendPanel = legendPanel;
        this.gamesPanel = new MenuPanel();
    }

    /**
     * The available games list is updated.
     *
     * @param lobbies the list of available lobbies.
     */
    public void setAvailableGames(List<MenuLobbyData> lobbies) {
        // data is passed to the component
        gamesPanel.updateData(lobbies);
    }

    @Override
    public void draw(Terminal terminal, int width, int height) {
        // fixed layout bounds are defined
        int inputHeight = 2;
        int logHeight = 8;
        int sideWidth = Math.min(45, width / 3);
        int mainWidth = width - sideWidth;
        int mainHeight = height - logHeight - inputHeight;

        // main games panel is drawn
        gamesPanel.draw(terminal, 1, 1, width, mainHeight);

        // bottom section is split between log and legend
        logPanel.draw(terminal, 1, height - logHeight - inputHeight + 1, mainWidth, logHeight);
        legendPanel.draw(terminal, mainWidth + 1, height - logHeight - inputHeight + 1, sideWidth, logHeight);
    }
}
