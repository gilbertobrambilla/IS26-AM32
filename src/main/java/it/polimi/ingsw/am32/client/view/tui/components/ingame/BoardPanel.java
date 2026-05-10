package it.polimi.ingsw.am32.client.view.tui.components.ingame;

import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.view.tui.components.global.AbstractTuiPanel;
import org.jline.terminal.Terminal;

/**
 * Panel responsible for displaying the central game board.
 */
public class BoardPanel extends AbstractTuiPanel {
    private LightGameState state;

    /**
     * The game state data is updated.
     *
     * @param state the current game state.
     */
    public void updateData(LightGameState state) {
        // state is updated
        this.state = state;
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // bounding box is drawn
        drawBox(terminal, startX, startY, width, height, "Game Board");

        // content area is cleared
        clearContent(terminal, startX, startY, width, height);

        if (state == null) {
            return;
        }

        // initial row offset is defined
        int rowOffset = 2;

        // max printable width is calculated
        int maxTextWidth = width - 4;

        // deck info is drawn
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print("tribe deck size: " + state.getTribeDeckSize());
        rowOffset++;

        // upper tribe row is drawn
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print("upper tribe row:");
        // text is wrapped and lines used are added to offset
        rowOffset += drawWrappedText(terminal, String.join(", ", state.getUpperTribeRow()), startX + 4, startY + rowOffset, maxTextWidth);
        rowOffset++;

        // lower tribe row is drawn
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print("lower tribe row:");
        // text is wrapped and lines used are added to offset
        rowOffset += drawWrappedText(terminal, String.join(", ", state.getLowerTribeRow()), startX + 4, startY + rowOffset, maxTextWidth);
        rowOffset++;

        // upper building row is drawn
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print("upper building row:");
        // text is wrapped and lines used are added to offset
        rowOffset += drawWrappedText(terminal, String.join(", ", state.getUpperBuildingRow()), startX + 4, startY + rowOffset, maxTextWidth);
        rowOffset++;

        // lower building row is drawn
        moveCursor(terminal, startX + 2, startY + rowOffset++);
        terminal.writer().print("lower building row:");
        // text is wrapped and lines used are added to offset
        rowOffset += drawWrappedText(terminal, String.join(", ", state.getLowerBuildingRow()), startX + 4, startY + rowOffset, maxTextWidth);
        rowOffset += 2;

        // offer track is drawn
        if (rowOffset < height - 2) {
            moveCursor(terminal, startX + 2, startY + rowOffset++);
            terminal.writer().print("offer track:");
            for (LightOfferTile tile : state.getOfferTrack()) {
                // drawing is stopped if space runs out
                if (rowOffset >= height - 1) {
                    break;
                }

                moveCursor(terminal, startX + 4, startY + rowOffset++);
                // occupant status is resolved
                String occupant = tile.getOccupyingPlayer() != null ? tile.getOccupyingPlayer() : "empty";
                terminal.writer().print("tile " + tile.getId() + ": " + occupant);
            }
        }
    }
}
