package it.polimi.ingsw.am32.client.view.tui.components.ingame;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.tui.components.global.AbstractTuiPanel;
import it.polimi.ingsw.am32.client.view.tui.utils.CardFormatter;
import it.polimi.ingsw.am32.client.view.tui.utils.TuiColorPalette;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import org.jline.terminal.Terminal;

import java.util.HashMap;
import java.util.Map;

/**
 * Panel responsible for displaying player statistics and inventory.
 */
public class SidePanel extends AbstractTuiPanel {
    private LightGameState state;
    private String focusedPlayer;

    /**
     * Side panel is instantiated.
     */
    public SidePanel() {
        // instance remains empty as formatter is now a singleton
    }

    /**
     * Game state data is updated.
     *
     * @param state the current game state
     */
    public void updateData(LightGameState state) {
        // state is updated
        this.state = state;
    }

    /**
     * Panel focus is set to a specific player or cleared.
     *
     * @param nickname the player nickname or null to return to compressed view
     */
    public void setFocus(String nickname) {
        // focus is updated
        this.focusedPlayer = nickname;
    }

    /**
     * Ansi color code is retrieved based on totem color.
     *
     * @param color the enum representing the totem color
     * @return the ansi color escape sequence
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
            drawExpandedView(terminal, startX, startY, width);
        }
    }

    /**
     * Compressed view summarizing all players is drawn with local player identification.
     *
     * @param terminal the terminal instance
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
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

            // local player identification is appended
            String youSuffix = player.getNickname().equals(state.getMyNickname()) ? " (you)" : "";

            // color codes are prepared from central palette
            String colorCode = TuiColorPalette.getTotemColor(player.getTotemColor());
            String resetCode = "\033[0m";

            // basic info is drawn including identification suffix
            moveCursor(terminal, startX + 2, startY + rowOffset++);
            terminal.writer().print(activeIndicator + colorCode + player.getNickname() + youSuffix + resetCode + connectionTag);

            // vitals are drawn with prestige prioritized over food
            moveCursor(terminal, startX + 4, startY + rowOffset++);
            terminal.writer().print(player.getPrestige() + " 🏆   " + player.getFood() + " 🍖");

            // card counters are drawn using full names
            moveCursor(terminal, startX + 4, startY + rowOffset++);
            terminal.writer().print(player.getCharacterCards().size() + " characters   " + player.getBuildingCards().size() + " buildings");

            // spacing is added
            rowOffset++;
        }
    }

    /**
     * Expanded view showing detailed inventory for a specific player is drawn with identification.
     *
     * @param terminal the terminal instance
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
     * @param maxWidth the maximum width
     */
    private void drawExpandedView(Terminal terminal, int startX, int startY, int maxWidth) {
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

        // connection tag is formatted
        String connectionTag = player.isConnected() ? "" : " (disconnected)";

        // active player indicator is evaluated
        String activeIndicator = player.getNickname().equals(currentPlayer) ? "> " : "  ";

        // local player identification is appended
        String youSuffix = player.getNickname().equals(state.getMyNickname()) ? " (you)" : "";

        // formatting codes are prepared
        String colorCode = TuiColorPalette.getTotemColor(player.getTotemColor());
        String resetCode = "\033[0m";
        String boldCode = "\033[1m";

        // header info is drawn including identification suffix
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print(activeIndicator + boldCode + colorCode + player.getNickname() + youSuffix + resetCode + connectionTag);

        // vitals are drawn with prestige prioritized over food
        moveCursor(terminal, startX + 4, startY + rowOffset++);
        terminal.writer().print(player.getPrestige() + " 🏆   " + player.getFood() + " 🍖");
        rowOffset++;

        // characters section is drawn
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print(boldCode + "characters" + resetCode);

        // character occurrences are mapped using translated types
        AssetManager am = AssetManager.getInstance();
        Map<String, Integer> charCounts = new HashMap<>();
        for (String cardId : player.getCharacterCards()) {
            CharacterCardDTO dto = am.getCharacterCard(cardId);
            if (dto != null) {
                String type = GameDictionary.getInstance().translate(dto.getCharacterType());
                charCounts.put(type, charCounts.getOrDefault(type, 0) + 1);
            }
        }

        // grouped character counts are printed
        for (Map.Entry<String, Integer> entry : charCounts.entrySet()) {
            moveCursor(terminal, startX + 4, startY + rowOffset++);
            terminal.writer().print(entry.getValue() + "x " + entry.getKey());
        }
        rowOffset++;

        // buildings section is drawn
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print(boldCode + "buildings" + resetCode);

        // building occurrences are mapped by their formatted effect
        Map<String, Integer> bldgCounts = new HashMap<>();
        for (String cardId : player.getBuildingCards()) {
            BuildingCardDTO dto = am.getBuildingCard(cardId);
            if (dto != null) {
                String effect = CardFormatter.getInstance().formatBuilding(dto);
                bldgCounts.put(effect, bldgCounts.getOrDefault(effect, 0) + 1);
            }
        }

        // grouped building effects are printed
        for (Map.Entry<String, Integer> entry : bldgCounts.entrySet()) {
            String line = entry.getValue() + "x " + entry.getKey();
            rowOffset += drawWrappedText(terminal, line, startX + 4, startY + rowOffset, maxWidth - 6);
        }
    }
}
