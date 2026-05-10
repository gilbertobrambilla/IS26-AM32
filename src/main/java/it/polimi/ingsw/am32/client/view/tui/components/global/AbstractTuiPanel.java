package it.polimi.ingsw.am32.client.view.tui.components.global;

import it.polimi.ingsw.am32.client.view.tui.components.TuiComponent;
import org.jline.terminal.Terminal;

/**
 * Base abstract class for tui panels providing common drawing utilities.
 */
public abstract class AbstractTuiPanel implements TuiComponent {

    /**
     * The cursor is moved to the specified coordinates using ansi escape codes.
     *
     * @param terminal the terminal instance.
     * @param x the x coordinate.
     * @param y the y coordinate.
     */
    protected void moveCursor(Terminal terminal, int x, int y) {
        // ansi escape sequence is sent to the terminal
        terminal.writer().print("\033[" + y + ";" + x + "H");
    }

    /**
     * A bordered box with an optional title is drawn.
     *
     * @param terminal the terminal instance.
     * @param startX the starting x coordinate.
     * @param startY the starting y coordinate.
     * @param width the width of the box.
     * @param height the height of the box.
     * @param title the title to be displayed on the top border.
     */
    protected void drawBox(Terminal terminal, int startX, int startY, int width, int height, String title) {
        // top border is drawn using unicode characters
        moveCursor(terminal, startX, startY);
        terminal.writer().print("╔");
        for (int i = 0; i < width - 2; i++) {
            terminal.writer().print("═");
        }
        terminal.writer().print("╗");

        // title is printed if present
        if (title != null && !title.isEmpty()) {
            moveCursor(terminal, startX + 2, startY);
            terminal.writer().print(" " + title + " ");
        }

        // side borders are drawn using unicode characters
        for (int i = 1; i < height - 1; i++) {
            moveCursor(terminal, startX, startY + i);
            terminal.writer().print("║");
            moveCursor(terminal, startX + width - 1, startY + i);
            terminal.writer().print("║");
        }

        // bottom border is drawn using unicode characters
        moveCursor(terminal, startX, startY + height - 1);
        terminal.writer().print("╚");
        for (int i = 0; i < width - 2; i++) {
            terminal.writer().print("═");
        }
        terminal.writer().print("╝");
    }

    /**
     * Text is drawn with automatic line wrapping to fit within the maximum width.
     *
     * @param terminal the terminal instance.
     * @param text the text to be printed.
     * @param startX the starting x coordinate.
     * @param startY the starting y coordinate.
     * @param maxWidth the maximum allowed width before wrapping.
     * @return the number of vertical lines used.
     */
    protected int drawWrappedText(Terminal terminal, String text, int startX, int startY, int maxWidth) {
        // safe minimum width is enforced
        if (maxWidth <= 0) return 1;

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

                // line is reset and count is incremented
                currentLine = new StringBuilder();
                lineCount++;
            }

            // space is added if line is not empty
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            // word is appended
            currentLine.append(word);
        }

        // remaining text is printed
        if (currentLine.length() > 0) {
            moveCursor(terminal, startX, startY + lineCount);
            terminal.writer().print(currentLine.toString());
            lineCount++;
        }

        // total lines used is returned
        return Math.max(1, lineCount);
    }

    /**
     * The area inside the box is cleared.
     *
     * @param terminal the terminal instance.
     * @param startX the starting x coordinate.
     * @param startY the starting y coordinate.
     * @param width the width of the box.
     * @param height the height of the box.
     */
    protected void clearContent(Terminal terminal, int startX, int startY, int width, int height) {
        // empty string is prepared
        String emptyLine = " ".repeat(Math.max(0, width - 2));

        // inner lines are overwritten with spaces
        for (int i = 1; i < height - 1; i++) {
            moveCursor(terminal, startX + 1, startY + i);
            terminal.writer().print(emptyLine);
        }
    }
}
