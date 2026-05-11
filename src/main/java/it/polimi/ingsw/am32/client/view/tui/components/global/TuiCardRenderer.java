package it.polimi.ingsw.am32.client.view.tui.components.global;

import it.polimi.ingsw.am32.client.view.tui.utils.TuiColorPalette;
import org.jline.terminal.Terminal;

/**
 * Utility component for rendering formatted card rectangles and board tiles.
 */
public class TuiCardRenderer extends AbstractTuiPanel {

    /**
     * A structured card box is drawn on the terminal.
     *
     * @param terminal the terminal instance.
     * @param x the starting x coordinate.
     * @param y the starting y coordinate.
     * @param width the width of the card.
     * @param height the height of the card.
     * @param idLabel the short identifier printed in the top right.
     * @param typeLabel the category of the card.
     * @param costLabel the cost associated with the card.
     * @param prestigeLabel the prestige points associated with the card.
     * @param colorCode the ansi color code for borders and highlights.
     * @param effect the descriptive effect text.
     */
    public void drawCardBox(Terminal terminal, int x, int y, int width, int height, String idLabel, String typeLabel, String costLabel, String prestigeLabel, String colorCode, String effect) {
        // reset code is fetched
        String reset = TuiColorPalette.getReset();

        // top border is drawn
        moveCursor(terminal, x, y);
        terminal.writer().print(colorCode + "┌");
        for (int i = 0; i < width - 2; i++) terminal.writer().print("─");
        terminal.writer().print("┐" + reset);

        // side borders are drawn
        for (int i = 1; i < height - 1; i++) {
            moveCursor(terminal, x, y + i);
            terminal.writer().print(colorCode + "│" + reset);
            moveCursor(terminal, x + width - 1, y + i);
            terminal.writer().print(colorCode + "│" + reset);
        }

        // bottom border is drawn
        moveCursor(terminal, x, y + height - 1);
        terminal.writer().print(colorCode + "└");
        for (int i = 0; i < width - 2; i++) terminal.writer().print("─");
        terminal.writer().print("┘" + reset);

        // id label is printed in the top right corner
        if (idLabel != null && !idLabel.isEmpty()) {
            moveCursor(terminal, x + width - 2 - idLabel.length(), y);
            terminal.writer().print(colorCode + " " + idLabel + " " + reset);
        }

        // cost label is printed in the top left inner space
        if (costLabel != null && !costLabel.isEmpty()) {
            moveCursor(terminal, x + 2, y + 1);
            terminal.writer().print(costLabel);
        }

        // prestige label is printed in the top right inner space
        if (prestigeLabel != null && !prestigeLabel.isEmpty()) {
            moveCursor(terminal, x + width - 2 - prestigeLabel.length(), y + 1);
            terminal.writer().print(prestigeLabel);
        }

        // type label is printed with color highlight
        if (typeLabel != null && !typeLabel.isEmpty()) {
            moveCursor(terminal, x + 2, y + 2);
            String safeType = typeLabel.length() > width - 4 ? typeLabel.substring(0, width - 4) : typeLabel;
            terminal.writer().print(colorCode + safeType + reset);
        }

        // effect text is wrapped and printed
        if (effect != null && !effect.isEmpty()) {
            drawCardTextWrapped(terminal, effect, x + 2, y + 3, width - 4, height - 4);
        }
    }

    /**
     * An offer tile box is drawn on the terminal.
     *
     * @param terminal the terminal instance.
     * @param x the starting x coordinate.
     * @param y the starting y coordinate.
     * @param width the width of the tile.
     * @param height the height of the tile.
     * @param idLabel the letter identifier of the tile.
     * @param player the occupying player nickname.
     * @param stats the text summarizing the tile effects.
     */
    public void drawOfferTileBox(Terminal terminal, int x, int y, int width, int height, String idLabel, String player, String stats) {
        // default border color is set
        String borderColor = "\033[38;5;240m";
        String reset = TuiColorPalette.getReset();

        // border color is highlighted if occupied
        if (player != null && !player.isEmpty()) {
            borderColor = "\033[38;5;226m";
        }

        // top border is drawn
        moveCursor(terminal, x, y);
        terminal.writer().print(borderColor + "┌");
        for (int i = 0; i < width - 2; i++) terminal.writer().print("─");
        terminal.writer().print("┐" + reset);

        // side borders are drawn
        for (int i = 1; i < height - 1; i++) {
            moveCursor(terminal, x, y + i);
            terminal.writer().print(borderColor + "│" + reset);
            moveCursor(terminal, x + width - 1, y + i);
            terminal.writer().print(borderColor + "│" + reset);
        }

        // bottom border is drawn
        moveCursor(terminal, x, y + height - 1);
        terminal.writer().print(borderColor + "└");
        for (int i = 0; i < width - 2; i++) terminal.writer().print("─");
        terminal.writer().print("┘" + reset);

        // id letter is printed in the top left
        moveCursor(terminal, x + 2, y + 1);
        terminal.writer().print(idLabel);

        // occupying player is printed aligned to the right
        if (player != null && !player.isEmpty()) {
            String safePlayer = player.length() > width - 5 ? player.substring(0, width - 5) : player;
            moveCursor(terminal, x + width - 1 - safePlayer.length(), y + 1);
            terminal.writer().print(borderColor + safePlayer + reset);
        }

        // stats are printed in the bottom center
        if (stats != null && !stats.isEmpty()) {
            moveCursor(terminal, x + 2, y + 2);
            terminal.writer().print(stats);
        }
    }

    /**
     * Text is drawn with automatic line wrapping restricted to the card boundaries.
     *
     * @param terminal the terminal instance.
     * @param text the text to wrap.
     * @param startX the starting x coordinate.
     * @param startY the starting y coordinate.
     * @param maxWidth the maximum width before wrapping.
     * @param maxLines the maximum vertical lines allowed.
     */
    private void drawCardTextWrapped(Terminal terminal, String text, int startX, int startY, int maxWidth, int maxLines) {
        // words are split by space
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int lineCount = 0;

        for (String word : words) {
            // length is checked against max width
            if (currentLine.length() + word.length() + 1 > maxWidth) {
                // current line is printed
                moveCursor(terminal, startX, startY + lineCount);
                terminal.writer().print(currentLine.toString());

                // string builder is reset
                currentLine = new StringBuilder();
                lineCount++;

                // loop is broken if max lines are reached
                if (lineCount >= maxLines) return;
            }

            // space is added for subsequent words
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            // word is appended
            currentLine.append(word);
        }

        // remaining text is printed
        if (currentLine.length() > 0 && lineCount < maxLines) {
            moveCursor(terminal, startX, startY + lineCount);
            terminal.writer().print(currentLine.toString());
        }
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // default draw method is intentionally left blank
        // specific rendering methods are invoked manually instead
    }
}
