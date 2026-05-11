package it.polimi.ingsw.am32.client.view.tui.components.ingame;

import it.polimi.ingsw.am32.client.view.tui.components.global.AbstractTuiPanel;
import org.jline.terminal.Terminal;

/**
 * Panel responsible for displaying generic game information.
 */
public class HeaderPanel extends AbstractTuiPanel {
    private String currentPhase = "WAITING";
    private int currentRound = 1;
    private int currentEra = 1;

    /**
     * The game state data for the header is updated.
     *
     * @param phase the current game phase.
     * @param round the current round.
     * @param era the current era.
     */
    public void updateData(String phase, int round, int era) {
        // internal state is updated
        this.currentPhase = phase;
        this.currentRound = round;
        this.currentEra = era;
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // bounding box is drawn
        drawBox(terminal, startX, startY, width, height, "Game Status");

        // inner area is cleared
        clearContent(terminal, startX, startY, width, height);

        // content string is formatted
        String content = String.format("ERA: %d | ROUND: %d | PHASE: %s", currentEra, currentRound, currentPhase);

        // cursor is moved to the center of the panel
        int textX = startX + (width - content.length()) / 2;
        moveCursor(terminal, textX, startY + 1);

        // content is printed
        terminal.writer().print(content);
    }
}
