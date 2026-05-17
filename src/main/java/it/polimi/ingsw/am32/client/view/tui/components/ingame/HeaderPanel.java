package it.polimi.ingsw.am32.client.view.tui.components.ingame;

import it.polimi.ingsw.am32.client.view.tui.components.global.AbstractTuiPanel;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import org.jline.terminal.Terminal;

/**
 * Panel responsible for displaying generic game information.
 */
public class HeaderPanel extends AbstractTuiPanel {
    private String currentPhase = "WAITING";
    private int currentRound = 1;
    private int currentEra = 1;
    private int deckSize = 0;
    private int topEra = 0;

    /**
     * The game state data for the header is updated.
     *
     * @param phase the current game phase
     * @param round the current round
     * @param era the current era
     * @param deckSize the remaining cards in the deck
     * @param topEra the era of the top card
     */
    public void updateData(String phase, int round, int era, int deckSize, int topEra) {
        // raw phase is translated using the singleton formatter
        this.currentPhase = GameDictionary.getInstance().translate(phase);
        // internal state is updated
        this.currentRound = round;
        this.currentEra = era;
        this.deckSize = deckSize;
        this.topEra = topEra;
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // bounding box is drawn
        drawBox(terminal, startX, startY, width, height, "Game Status");

        // inner area is cleared
        clearContent(terminal, startX, startY, width, height);

        // content string is formatted using the translated phase
        String content = String.format(
                "Era: %d | Round: %d | Phase: %s | Deck: %d (Top Card Era: %d)",
                currentEra, currentRound, currentPhase, deckSize, topEra
        );

        // cursor is moved to the center of the panel
        int textX = startX + (width - content.length()) / 2;
        moveCursor(terminal, textX, startY + 1);

        // content is printed
        terminal.writer().print(content);
    }
}
