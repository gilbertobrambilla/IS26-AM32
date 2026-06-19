package it.polimi.ingsw.am32.client.view.tui.components.lobby;

import it.polimi.ingsw.am32.client.view.tui.components.global.AbstractTuiPanel;
import org.jline.terminal.Terminal;

import java.util.List;

/**
 * This panel displays lobby information and connected players.
 */
public class LobbyPanel extends AbstractTuiPanel {
    private int gameId;
    private int currentPlayersCount;
    private int targetPlayers;
    private List<String> currentPlayers;
    private String myNickname;

    /**
     * The lobby details and player list are updated.
     *
     * @param gameId the identifier of the game
     * @param currentPlayersCount the current player count
     * @param targetPlayers the target player count
     * @param currentPlayers the list of connected player nicknames
     * @param myNickname the local player nickname
     */
    public void updateData(int gameId, int currentPlayersCount, int targetPlayers, List<String> currentPlayers, String myNickname) {
        // internal state variables are updated
        this.gameId = gameId;
        this.currentPlayersCount = currentPlayersCount;
        this.targetPlayers = targetPlayers;
        this.currentPlayers = currentPlayers;
        this.myNickname = myNickname;
    }

    /**
     * The lobby panel is drawn.
     *
     * @param terminal the terminal instance
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
     * @param width the panel width
     * @param height the panel height
     */
    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // purely typographic layout is used without outer box
        clearContent(terminal, startX, startY, width, height);

        // margins are defined to match the menu scene
        int paddingX = 3;
        int paddingY = 2;
        int baseLeft = startX + paddingX;
        int currentY = startY + paddingY;

        // title and game id are printed
        moveCursor(terminal, baseLeft, currentY);
        terminal.writer().print("LOBBY ROOM - Game ID: " + gameId);

        // uniform vertical spacing is applied
        currentY += 2;

        // player count is printed
        moveCursor(terminal, baseLeft, currentY);
        terminal.writer().print("Current players: " + currentPlayersCount + " / " + targetPlayers);

        // spacing is applied before the list
        currentY++;

        // player list is printed if available
        if (currentPlayers != null) {
            for (String player : currentPlayers) {
                // local player suffix is evaluated
                String suffix = player.equals(myNickname) ? " (you)" : "";

                // list item is printed with indentation
                moveCursor(terminal, baseLeft + 2, currentY);
                terminal.writer().print("- " + player + suffix);

                // vertical coordinate is incremented
                currentY++;
            }
        }

        // spacing is applied after the list
        currentY++;

        // status message is printed
        moveCursor(terminal, baseLeft, currentY);
        terminal.writer().print("Waiting for players to join...");
    }
}
