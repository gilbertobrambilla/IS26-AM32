package it.polimi.ingsw.am32.client.view.tui.components.menu;

import it.polimi.ingsw.am32.client.view.tui.components.global.AbstractTuiPanel;
import it.polimi.ingsw.am32.client.view.tui.scenes.MenuScene.MenuLobbyData;
import org.jline.terminal.Terminal;

import java.util.List;

/**
 * This panel displays the grid of available lobbies.
 */
public class MenuPanel extends AbstractTuiPanel {
    private List<MenuLobbyData> availableLobbies;

    /**
     * The available lobbies data is updated.
     *
     * @param lobbies the list of available lobbies
     */
    public void updateData(List<MenuLobbyData> lobbies) {
        // internal list is updated
        this.availableLobbies = lobbies;
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // margins are defined for a premium look
        int paddingX = 3;
        int paddingY = 2;
        int baseLeft = startX + paddingX;
        int currentY = startY + paddingY;

        // empty state is checked
        if (availableLobbies == null || availableLobbies.isEmpty()) {
            // empty state message is drawn
            moveCursor(terminal, baseLeft, currentY);
            terminal.writer().print("No lobbies available at the moment. Create one!");
            return;
        }

        // introductory text is printed
        moveCursor(terminal, baseLeft, currentY);
        terminal.writer().print("Available lobbies you can join:");

        // variables for grid positioning are initialized
        int tileWidth = 24;
        int tileHeight = 4;
        int currentX = baseLeft;
        currentY += 2;
        int drawnCount = 0;

        // lobbies are iterated and drawn as tiles
        for (MenuLobbyData lobby : availableLobbies) {
            // vertical overflow is checked
            if (currentY + tileHeight > startY + height) {
                // remaining lobbies count is calculated
                int remaining = availableLobbies.size() - drawnCount;
                // singular or plural word is determined
                String lobbyWord = (remaining == 1) ? "lobby" : "lobbies";

                // overflow message is drawn
                moveCursor(terminal, baseLeft, currentY);
                terminal.writer().print("... and " + remaining + " more " + lobbyWord + ".");
                break;
            }

            // tile box is drawn
            drawBox(terminal, currentX, currentY, tileWidth, tileHeight, "ID: " + lobby.id);

            // player data is drawn inside the box
            moveCursor(terminal, currentX + 2, currentY + 1);
            terminal.writer().print("Players: " + lobby.currentPlayers + "/" + lobby.targetPlayers);

            // horizontal coordinate is advanced
            currentX += tileWidth + 2;
            drawnCount++;

            // horizontal wrap is checked
            if (currentX + tileWidth > startX + width - paddingX) {
                // coordinates are reset for a new row
                currentX = baseLeft;
                currentY += tileHeight + 1;
            }
        }
    }
}
