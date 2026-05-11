package it.polimi.ingsw.am32.client.view.tui.components.ingame;

import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.tui.components.global.AbstractTuiPanel;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import org.jline.terminal.Terminal;

/**
 * Panel responsible for displaying player statistics and inventory.
 */
public class SidePanel extends AbstractTuiPanel {
    private LightGameState state;
    private String focusedPlayer;

    /**
     * The game state data is updated.
     *
     * @param state the current game state.
     */
    public void updateData(LightGameState state) {
        // state is updated
        this.state = state;
    }

    /**
     * The panel focus is set to a specific player or cleared.
     *
     * @param nickname the player nickname, or null to return to compressed view.
     */
    public void setFocus(String nickname) {
        // focus is updated
        this.focusedPlayer = nickname;
    }

    /**
     * Ansi color code is retrieved based on totem color.
     *
     * @param color the enum representing the totem color.
     * @return the ansi color escape sequence.
     */
    private String getAnsiColor(TotemColor color) {
        if (color == null) {
            return "\033[0m";
        }

        // ansi code is mapped
        switch (color.name().toUpperCase()) {
            case "RED": return "\033[31m";
            case "BLUE": return "\033[34m";
            case "GREEN": return "\033[32m";
            case "YELLOW": return "\033[33m";
            default: return "\033[0m";
        }
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // bounding box is drawn
        drawBox(terminal, startX, startY, width, height, "Players");
        // content area is cleared
        clearContent(terminal, startX, startY, width, height);

        if (state == null || state.getPlayers().isEmpty()) {
            return;
        }

        // view mode is determined based on focus
        if (focusedPlayer == null || focusedPlayer.isEmpty()) {
            // compressed view is rendered
            drawCompressedView(terminal, startX, startY);
        } else {
            // expanded view is rendered
            drawExpandedView(terminal, startX, startY);
        }
    }

    /**
     * The compressed view summarizing all players is drawn.
     *
     * @param terminal the terminal instance.
     * @param startX the starting x coordinate.
     * @param startY the starting y coordinate.
     */
    private void drawCompressedView(Terminal terminal, int startX, int startY) {
        // initial row offset is defined
        int rowOffset = 2;

        // current player is retrieved
        String currentPlayer = state.getCurrentPlayer();

        for (LightPlayer player : state.getPlayers().values()) {
            // connection tag is formatted as suffix
            String connectionTag = player.isConnected() ? "" : " (disconnected)";

            // active player indicator is evaluated
            String activeIndicator = player.getNickname().equals(currentPlayer) ? "> " : "  ";

            // color codes are prepared
            String colorCode = getAnsiColor(player.getTotemColor());
            String resetCode = "\033[0m";

            // basic info is drawn with connection tag appended after the color reset
            moveCursor(terminal, startX + 2, startY + rowOffset++);
            terminal.writer().print(activeIndicator + colorCode + player.getNickname() + resetCode + connectionTag);

            // vitals are drawn
            moveCursor(terminal, startX + 4, startY + rowOffset++);
            terminal.writer().print("food: " + player.getFood() + " | prestige: " + player.getPrestige());

            // card counters are drawn
            moveCursor(terminal, startX + 4, startY + rowOffset++);
            terminal.writer().print("cards: " + player.getCharacterCards().size() + " chars | " + player.getBuildingCards().size() + " bldgs");

            // spacing is added
            rowOffset++;
        }
    }

    /**
     * The expanded view showing detailed inventory for a specific player is drawn.
     *
     * @param terminal the terminal instance.
     * @param startX the starting x coordinate.
     * @param startY the starting y coordinate.
     */
    private void drawExpandedView(Terminal terminal, int startX, int startY) {
        // focused player is retrieved
        LightPlayer player = state.getPlayers().get(focusedPlayer);

        if (player == null) {
            // error is displayed
            moveCursor(terminal, startX + 2, startY + 2);
            terminal.writer().print("player not found.");
            return;
        }

        // initial row offset is defined
        int rowOffset = 2;

        // current player is retrieved
        String currentPlayer = state.getCurrentPlayer();

        // connection tag is formatted as suffix
        String connectionTag = player.isConnected() ? "" : " (disconnected)";

        // active player indicator is evaluated
        String activeIndicator = player.getNickname().equals(currentPlayer) ? "> " : "  ";

        // formatting codes are prepared
        String colorCode = getAnsiColor(player.getTotemColor());
        String resetCode = "\033[0m";
        String boldCode = "\033[1m";

        // header info is drawn with connection tag appended after the color reset
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print(activeIndicator + boldCode + colorCode + player.getNickname() + resetCode + connectionTag);

        // vitals are drawn
        moveCursor(terminal, startX + 4, startY + rowOffset++);
        terminal.writer().print("food: " + player.getFood() + " | prestige: " + player.getPrestige());
        rowOffset++;

        // characters section is drawn
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print(boldCode + "characters" + resetCode);

        // TODO: identical character cards are aggregated and translated via json dictionary
        for (String cardId : player.getCharacterCards()) {
            // indentation is added
            moveCursor(terminal, startX + 4, startY + rowOffset++);
            terminal.writer().print(cardId);
        }
        rowOffset++;

        // buildings section is drawn
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print(boldCode + "buildings" + resetCode);

        // TODO: identical building cards are aggregated and translated via json dictionary
        for (String cardId : player.getBuildingCards()) {
            // indentation is added
            moveCursor(terminal, startX + 4, startY + rowOffset++);
            terminal.writer().print(cardId);
        }
    }
}
