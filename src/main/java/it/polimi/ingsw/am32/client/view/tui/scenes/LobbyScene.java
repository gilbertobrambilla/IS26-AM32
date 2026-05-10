package it.polimi.ingsw.am32.client.view.tui.scenes;

import it.polimi.ingsw.am32.client.view.tui.components.global.LegendPanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.LogPanel;
import it.polimi.ingsw.am32.client.view.tui.components.lobby.LobbyPanel;
import org.jline.terminal.Terminal;

import java.util.List;

/**
 * Scene responsible for rendering the lobby room layout.
 */
public class LobbyScene implements Scene {
    private final LogPanel logPanel;
    private final LegendPanel legendPanel;
    private final LobbyPanel lobbyPanel;

    /**
     * The lobby scene is instantiated.
     *
     * @param logPanel the shared log panel instance.
     * @param legendPanel the shared legend panel instance.
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
     * @param gameId the identifier of the game.
     * @param currentPlayersCount the current player count.
     * @param targetPlayers the target player count.
     * @param currentPlayers the list of connected player nicknames.
     * @param myNickname the local player nickname.
     */
    public void setLobbyData(int gameId, int currentPlayersCount, int targetPlayers, List<String> currentPlayers, String myNickname) {
        // data is forwarded to the component
        lobbyPanel.updateData(gameId, currentPlayersCount, targetPlayers, currentPlayers, myNickname);
    }

    @Override
    public void draw(Terminal terminal, int width, int height) {
        // fixed layout bounds are defined
        int inputHeight = 2;
        int logHeight = 8;
        int sideWidth = Math.min(45, width / 3);
        int mainWidth = width - sideWidth;
        int mainHeight = height - logHeight - inputHeight;

        // lobby panel is drawn
        lobbyPanel.draw(terminal, 1, 1, width, mainHeight);

        // bottom section is split between log and legend
        logPanel.draw(terminal, 1, height - logHeight - inputHeight + 1, mainWidth, logHeight);
        legendPanel.draw(terminal, mainWidth + 1, height - logHeight - inputHeight + 1, sideWidth, logHeight);
    }
}
