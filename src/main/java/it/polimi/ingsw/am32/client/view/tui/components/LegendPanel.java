package it.polimi.ingsw.am32.client.view.tui.components;

import org.jline.terminal.Terminal;

/**
 * Panel responsible for displaying the list of available commands.
 */
public class LegendPanel extends AbstractTuiPanel {

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // bounding box is drawn
        drawBox(terminal, startX, startY, width, height, "Quick Guide");

        // content area is cleared
        clearContent(terminal, startX, startY, width, height);

        // initial row offset is defined
        int rowOffset = 1;

        // commands are printed
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print("- place <id>");

        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print("- take [-c|-b] <id>");

        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print("- stats [me|<nickname>|clear]");

        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print("- quit");
    }
}
