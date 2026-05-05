package it.polimi.ingsw.am32.client.view.tui.components;

import org.jline.terminal.Terminal;
import java.util.LinkedList;
import java.util.List;

/**
 * Panel responsible for displaying the recent event history.
 */
public class LogPanel extends AbstractTuiPanel {
    private final List<String> messages;
    private final int maxMessages;

    /**
     * The log panel is instantiated.
     *
     * @param maxMessages the maximum number of messages to retain.
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
     * @param message the message to be displayed.
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

        // available rows are calculated
        int availableRows = height - 2;

        // messages are iterated and printed
        int rowIdx = 0;
        int startIndex = Math.max(0, messages.size() - availableRows);

        for (int i = startIndex; i < messages.size(); i++) {
            // cursor is moved to the current row
            moveCursor(terminal, startX + 2, startY + 1 + rowIdx);

            String msg = messages.get(i);

            // message is truncated if too long for the box
            if (msg.length() > width - 4) {
                msg = msg.substring(0, width - 7) + "...";
            }

            // message is printed
            terminal.writer().print(msg);

            // row index is incremented
            rowIdx++;
        }
    }
}
