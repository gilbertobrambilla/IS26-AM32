package it.polimi.ingsw.am32.client.view.tui.scenes;

import org.jline.terminal.Terminal;
import java.util.List;

/**
 * Scene responsible for rendering the final game results.
 */
public class EndGameScene implements Scene {
    private List<String> winners;

    /**
     * The winners are updated.
     *
     * @param winners the list of winning players.
     */
    public void setWinners(List<String> winners) {
        this.winners = winners;
    }

    @Override
    public void draw(Terminal terminal, int width, int height) {
        // box boundaries are calculated
        int boxWidth = 50;
        int boxHeight = 10;
        int startX = (width - boxWidth) / 2;
        int startY = (height - boxHeight) / 2;

        // top border is drawn
        terminal.writer().print("\033[" + startY + ";" + startX + "H");
        terminal.writer().print("┌" + "─".repeat(boxWidth - 2) + "┐");

        // title is drawn
        terminal.writer().print("\033[" + (startY + 2) + ";" + (startX + (boxWidth - 11) / 2) + "H");
        terminal.writer().print("GAME OVER");

        // winners are drawn
        if (winners != null && !winners.isEmpty()) {
            String winnersText = "Winners: " + String.join(", ", winners);
            terminal.writer().print("\033[" + (startY + 4) + ";" + (startX + (boxWidth - winnersText.length()) / 2) + "H");
            terminal.writer().print(winnersText);
        }

        // bottom border is drawn
        terminal.writer().print("\033[" + (startY + boxHeight - 1) + ";" + startX + "H");
        terminal.writer().print("└" + "─".repeat(boxWidth - 2) + "┘");
    }
}
