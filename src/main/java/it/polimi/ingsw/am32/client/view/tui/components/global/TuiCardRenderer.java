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
     * @param terminal the terminal instance
     * @param x the starting x coordinate
     * @param y the starting y coordinate
     * @param width the width of the card
     * @param height the height of the card
     * @param idLabel the short identifier
     * @param typeLabel the category of the card
     * @param costLabel the cost associated with the card
     * @param prestigeLabel the prestige points associated with the card
     * @param colorCode the ansi color code for borders and highlights
     * @param effect the descriptive effect text
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

        // type label is printed
        if (typeLabel != null && !typeLabel.isEmpty()) {
            if (idLabel == null || idLabel.isEmpty()) {
                // centered without forced minimum margins
                int padding = Math.max(0, (width - 2 - typeLabel.length()) / 2);
                moveCursor(terminal, x + 1 + padding, y + 1);
                terminal.writer().print(colorCode + typeLabel + reset);
            } else {
                // left aligned attached to the border with truncation
                moveCursor(terminal, x + 1, y + 1);
                String safeType = typeLabel.length() > width - 6 ? typeLabel.substring(0, width - 6) : typeLabel;
                terminal.writer().print(colorCode + safeType + reset);
            }
        }

        // id label is printed top right
        if (idLabel != null && !idLabel.isEmpty()) {
            String formattedId = "[" + idLabel + "]";
            moveCursor(terminal, x + width - 1 - formattedId.length(), y + 1);
            terminal.writer().print(colorCode + formattedId + reset);
        }

        // flags for cost and prestige presence are evaluated
        boolean hasCost = costLabel != null && !costLabel.isEmpty();
        boolean hasPrestige = prestigeLabel != null && !prestigeLabel.isEmpty();

        if (hasCost) {
            // cost is printed attached to the left border on the second row
            moveCursor(terminal, x + 1, y + 2);
            terminal.writer().print(costLabel);

            if (hasPrestige) {
                // prestige is printed attached to the right border on the second row
                moveCursor(terminal, x + width - 1 - prestigeLabel.length(), y + 2);
                terminal.writer().print(prestigeLabel);
            }

            // effect text is wrapped and printed starting from the third row
            if (effect != null && !effect.isEmpty()) {
                drawCardTextWrapped(terminal, effect, x + 1, y + 3, width - 2, height - 4);
            }
        } else {
            // effect text is wrapped and printed starting from the second row
            if (effect != null && !effect.isEmpty()) {
                drawCardTextWrapped(terminal, effect, x + 1, y + 2, width - 2, height - 3);
            }

            if (hasPrestige) {
                // prestige is printed attached to the left border on the third row
                moveCursor(terminal, x + 1, y + 3);
                terminal.writer().print(prestigeLabel);
            }
        }
    }

    /**
     * An offer tile box is drawn on the terminal.
     *
     * @param terminal the terminal instance
     * @param x the starting x coordinate
     * @param y the starting y coordinate
     * @param width the width of the tile
     * @param height the height of the tile
     * @param idLabel the letter identifier of the tile
     * @param totemColorAnsi the ansi color code of the occupying totem
     * @param stats the text summarizing the tile effects
     */
    public void drawOfferTileBox(Terminal terminal, int x, int y, int width, int height, String idLabel, String totemColorAnsi, String stats) {
        // default border color is set statically
        String borderColor = "\033[38;5;240m";
        String reset = TuiColorPalette.getReset();

        // borders are drawn
        moveCursor(terminal, x, y);
        terminal.writer().print(borderColor + "┌");
        for (int i = 0; i < width - 2; i++) terminal.writer().print("─");
        terminal.writer().print("┐" + reset);

        for (int i = 1; i < height - 1; i++) {
            moveCursor(terminal, x, y + i);
            terminal.writer().print(borderColor + "│" + reset);
            moveCursor(terminal, x + width - 1, y + i);
            terminal.writer().print(borderColor + "│" + reset);
        }

        moveCursor(terminal, x, y + height - 1);
        terminal.writer().print(borderColor + "└");
        for (int i = 0; i < width - 2; i++) terminal.writer().print("─");
        terminal.writer().print("┘" + reset);

        // totem slot is centered on the first inner row
        int slotX = x + Math.max(1, (width - 3) / 2);
        moveCursor(terminal, slotX, y + 1);
        if (totemColorAnsi != null && !totemColorAnsi.isEmpty()) {
            terminal.writer().print("[" + totemColorAnsi + "■" + reset + "]");
        } else {
            terminal.writer().print("[ ]");
        }

        // stats are perfectly centered on the third inner row
        if (stats != null && !stats.isEmpty()) {
            int statsX = x + Math.max(1, (width - stats.length()) / 2);
            moveCursor(terminal, statsX, y + 3);
            terminal.writer().print(stats);
        }

        // id letter is printed in the bottom right corner
        if (idLabel != null && !idLabel.isEmpty()) {
            moveCursor(terminal, x + width - 2 - idLabel.length(), y + height - 2);
            terminal.writer().print(idLabel);
        }
    }

    /**
     * Text is drawn with multi-level wrapping supporting forced newlines via \n.
     *
     * @param terminal the terminal instance
     * @param text the text to wrap
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
     * @param maxWidth the maximum width before wrapping
     * @param maxLines the maximum vertical lines allowed
     */
    private void drawCardTextWrapped(Terminal terminal, String text, int startX, int startY, int maxWidth, int maxLines) {
        // text is split into paragraphs using the newline character
        String[] paragraphs = text.split("\n", -1);
        int lineCount = 0;

        for (String paragraph : paragraphs) {
            // vertical limit check is performed
            if (lineCount >= maxLines) break;

            // words within the paragraph are processed for automatic wrapping
            String[] words = paragraph.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                // automatic wrap is evaluated
                if (currentLine.length() + word.length() + 1 > maxWidth) {
                    // current line is printed
                    moveCursor(terminal, startX, startY + lineCount);
                    terminal.writer().print(currentLine.toString());

                    // index is incremented and reset
                    lineCount++;
                    currentLine = new StringBuilder();

                    // inner vertical check is performed
                    if (lineCount >= maxLines) break;
                }

                // space is added between words
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            }

            // check if vertical limit was reached during word processing
            if (lineCount >= maxLines) break;

            // remaining paragraph content is printed
            if (currentLine.length() > 0) {
                moveCursor(terminal, startX, startY + lineCount);
                terminal.writer().print(currentLine.toString());
            }

            // vertical index is always incremented at the end of a paragraph (\n)
            lineCount++;
        }
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // default draw method is left blank
    }
}
