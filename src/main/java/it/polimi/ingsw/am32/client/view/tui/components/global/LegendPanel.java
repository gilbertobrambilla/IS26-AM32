package it.polimi.ingsw.am32.client.view.tui.components.global;

import it.polimi.ingsw.am32.client.view.tui.handlers.InputHandler;
import org.jline.terminal.Terminal;

import java.util.Collection;

/**
 * The dynamic list of available commands is displayed by this panel.
 */
public class LegendPanel extends AbstractTuiPanel {
    private Collection<InputHandler> commands;

    /**
     * The available commands are updated.
     *
     * @param commands the collection of active input handlers
     */
    public void updateCommands(Collection<InputHandler> commands) {
        // commands collection is updated
        this.commands = commands;
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // bounding box is drawn
        drawBox(terminal, startX, startY, width, height, "Quick Guide");

        // content area is cleared
        clearContent(terminal, startX, startY, width, height);

        // initial row offset is defined
        int rowOffset = 1;

        if (commands == null) {
            return;
        }

        // active commands are iterated
        for (InputHandler handler : commands) {
            // bounds are checked
            if (rowOffset >= height - 1) {
                break;
            }

            // cursor is moved
            moveCursor(terminal, startX + 2, startY + rowOffset++);

            // string components are prepared
            String syntax = "- " + handler.getSyntax();
            String desc = " (" + handler.getDescription() + ")";

            // formatting logic is applied
            String line = syntax + desc;
            if (line.length() > width - 4) {
                line = line.substring(0, width - 7) + "...";
            }

            // line is printed
            terminal.writer().print(line);
        }
    }
}
