package it.polimi.ingsw.am32.client.view.tui.components.global;

import org.jline.terminal.Terminal;
import java.util.LinkedList;
import java.util.List;

/**
 * This panel displays the recent event history.
 */
public class LogPanel extends AbstractTuiPanel {
    private final List<String> messages;
    private final int maxMessages;

    /**
     * The log panel is instantiated.
     *
     * @param maxMessages the maximum number of messages to retain
     */
    public LogPanel(int maxMessages) {
        // list is initialized
        this.messages = new LinkedList<>();
        // max threshold is set
        this.maxMessages = maxMessages;
    }

    /**
     * A new message is added to the log.
     *
     * @param message the message to be displayed
     */
    public void addMessage(String message) {
        // message is added
        messages.add(message);

        // old messages are removed if threshold is exceeded
        if (messages.size() > maxMessages) {
            messages.remove(0);
        }
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // bounding box is drawn
        drawBox(terminal, startX, startY, width, height, "Event Log");

        // inner area is cleared
        clearContent(terminal, startX, startY, width, height);

        // available space is calculated
        int availableRows = height - 2;
        int maxWidth = width - 4;

        if (maxWidth <= 0) return;

        // display lines list is initialized
        List<String> displayLines = new java.util.ArrayList<>();

        // messages are wrapped and converted into single lines
        for (String msg : messages) {
            String[] words = msg.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                // visible lengths are calculated ignoring ansi codes
                int currentVisibleLen = currentLine.toString().replaceAll("\033\\[[;\\d]*m", "").length();
                int wordVisibleLen = word.replaceAll("\033\\[[;\\d]*m", "").length();

                // line break is evaluated
                if (currentVisibleLen > 0 && currentVisibleLen + wordVisibleLen + 1 > maxWidth) {
                    displayLines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }

                // space is added if not empty
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }

            // remaining text is added
            if (currentLine.length() > 0) {
                displayLines.add(currentLine.toString());
            }
        }

        // starting index is calculated to show only the most recent lines
        int startIndex = Math.max(0, displayLines.size() - availableRows);
        int rowIdx = 0;

        // visible lines are iterated and printed
        for (int i = startIndex; i < displayLines.size(); i++) {
            // cursor is moved
            moveCursor(terminal, startX + 2, startY + 1 + rowIdx);

            // text is printed
            terminal.writer().print(displayLines.get(i));

            // row index is incremented
            rowIdx++;
        }
    }
}
