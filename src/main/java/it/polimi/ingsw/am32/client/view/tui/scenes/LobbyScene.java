package it.polimi.ingsw.am32.client.view.tui.scenes;

import it.polimi.ingsw.am32.client.view.tui.components.global.LegendPanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.LogPanel;
import it.polimi.ingsw.am32.client.view.tui.components.lobby.LobbyPanel;
import org.jline.terminal.Terminal;

import java.util.List;

/**
 * This scene renders the lobby room layout.
 */
public class LobbyScene implements Scene {
    private final LogPanel logPanel;
    private final LegendPanel legendPanel;
    private final LobbyPanel lobbyPanel;

    /**
     * The lobby scene is instantiated.
     *
     * @param logPanel the shared log panel instance
     * @param legendPanel the shared legend panel instance
     */
    public LobbyScene(LogPanel logPanel, LegendPanel legendPanel) {
        this.logPanel = logPanel;
        this.legendPanel = legendPanel;
        // main panel is instantiated
        this.lobbyPanel = new LobbyPanel();
    }

    /**
     * The lobby data is passed to the internal panel.
     *
     * @param gameId the identifier of the game
     * @param currentPlayersCount the current player count
     * @param targetPlayers the target player count
     * @param currentPlayers the list of connected player nicknames
     * @param myNickname the local player nickname
     */
    public void setLobbyData(int gameId, int currentPlayersCount, int targetPlayers, List<String> currentPlayers, String myNickname) {
        // data is forwarded to the component
        lobbyPanel.updateData(gameId, currentPlayersCount, targetPlayers, currentPlayers, myNickname);
    }

    /**
     * The lobby room scene is drawn.
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

        // lobby panel is drawn
        lobbyPanel.draw(terminal, 1, 1, width, mainHeight);

        // bottom section is split
        logPanel.draw(terminal, 1, height - logHeight - inputHeight + 1, logWidth, logHeight);
        legendPanel.draw(terminal, logWidth + 1, height - logHeight - inputHeight + 1, legendWidth, logHeight);
    }
}
