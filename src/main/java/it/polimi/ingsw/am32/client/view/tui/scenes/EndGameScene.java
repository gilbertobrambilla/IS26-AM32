package it.polimi.ingsw.am32.client.view.tui.scenes;

import it.polimi.ingsw.am32.dto.messages.game.models.LeaderboardEntryDTO;
import org.jline.terminal.Terminal;

import java.util.List;

/**
 * This scene renders final game results.
 */
public class EndGameScene implements Scene {
    private List<String> winners;
    private List<LeaderboardEntryDTO> globalLeaderboard;
    private int globalPosition;
    private boolean dbAvailable;
    private boolean isLeaderboardProcessed;

    /**
     * The winners are updated.
     *
     * @param winners the list of winning players
     */
    public void setWinners(List<String> winners) {
        this.winners = winners;
    }

    /**
     * The global leaderboard data is updated.
     *
     * @param globalLeaderboard the list of global leaderboard entries
     * @param globalPosition the calculated global position of the player
     * @param dbAvailable the flag indicating if the database is available
     */
    public void setGlobalLeaderboard(List<LeaderboardEntryDTO> globalLeaderboard, int globalPosition, boolean dbAvailable) {
        this.globalLeaderboard = globalLeaderboard;
        this.globalPosition = globalPosition;
        this.dbAvailable = dbAvailable;
        // flag is updated to indicate the event was received
        this.isLeaderboardProcessed = true;
    }

    /**
     * The final results scene is drawn.
     *
     * @param terminal the terminal instance
     * @param width the terminal width
     * @param height the terminal height
     */
    @Override
    public void draw(Terminal terminal, int width, int height) {
        // box boundaries and dynamic height are calculated
        int boxWidth = 66;
        int boxHeight = 8;
        int displayLimit = 10;

        if (!isLeaderboardProcessed) {
            // height is adjusted for waiting phase
            boxHeight += 2;
        } else if (!dbAvailable) {
            // height is adjusted for database failure message
            boxHeight += 2;
        } else if (globalLeaderboard != null) {
            // height is adjusted to fit dynamic leaderboard entries up to the limit
            int visibleEntries = Math.min(globalLeaderboard.size(), displayLimit);
            boxHeight += 4 + visibleEntries;

            // extra height is added for the overflow message if needed
            if (globalLeaderboard.size() > displayLimit) {
                boxHeight++;
            }
        }

        // extra height is added for the quit message
        boxHeight += 2;

        int startX = (width - boxWidth) / 2;
        int startY = (height - boxHeight) / 2;

        // top border is drawn
        terminal.writer().print("\033[" + startY + ";" + startX + "H");
        terminal.writer().print("┌" + "─".repeat(boxWidth - 2) + "┐");

        // title is drawn
        terminal.writer().print("\033[" + (startY + 2) + ";" + (startX + (boxWidth - 9) / 2) + "H");
        terminal.writer().print("GAME OVER");

        // winners are drawn
        int currentY = startY + 4;
        if (winners != null && !winners.isEmpty()) {
            String winnersText = "Winners: " + String.join(", ", winners);
            terminal.writer().print("\033[" + currentY + ";" + (startX + (boxWidth - winnersText.length()) / 2) + "H");
            terminal.writer().print(winnersText);
        }
        currentY += 2;

        // leaderboard section is rendered based on availability
        if (!isLeaderboardProcessed) {
            // waiting feedback is drawn
            String waitText = "Processing global leaderboard...";
            terminal.writer().print("\033[" + currentY + ";" + (startX + (boxWidth - waitText.length()) / 2) + "H");
            terminal.writer().print(waitText);
            currentY += 2;
        } else if (!dbAvailable) {
            // failure message is drawn
            String failText = "Global leaderboard is currently unavailable.";
            terminal.writer().print("\033[" + currentY + ";" + (startX + (boxWidth - failText.length()) / 2) + "H");
            terminal.writer().print(failText);
            currentY += 2;
        } else if (globalLeaderboard != null) {
            // leaderboard title is drawn
            String title = "GLOBAL LEADERBOARD";
            terminal.writer().print("\033[" + currentY + ";" + (startX + (boxWidth - title.length()) / 2) + "H");
            terminal.writer().print(title);
            currentY += 2;

            // leaderboard headers are drawn with updated column widths
            String header = String.format("%-4s | %-15s | %-8s | %-21s", "Pos", "Player", "Score", "Date");
            terminal.writer().print("\033[" + currentY + ";" + (startX + (boxWidth - header.length()) / 2) + "H");
            terminal.writer().print(header);
            currentY++;

            // leaderboard entries are drawn iteratively up to the limit
            int visibleEntries = Math.min(globalLeaderboard.size(), displayLimit);
            for (int i = 0; i < visibleEntries; i++) {
                LeaderboardEntryDTO entry = globalLeaderboard.get(i);

                // date string is retrieved safely
                String dateStr = entry.getDate() != null ? entry.getDate() : "";

                // date string is explicitly truncated if it exceeds maximum formatting width
                if (dateStr.length() > 21) {
                    dateStr = dateStr.substring(0, 21);
                }

                // row is formatted matching the header column widths
                String row = String.format("%-4d | %-15s | %-8d | %-21s", entry.getPosition(), entry.getPlayer(), entry.getScore(), dateStr);
                terminal.writer().print("\033[" + currentY + ";" + (startX + (boxWidth - row.length()) / 2) + "H");
                terminal.writer().print(row);
                currentY++;
            }

            // overflow message is drawn if list exceeds limit
            if (globalLeaderboard.size() > displayLimit) {
                int otherPlayers = globalLeaderboard.size() - displayLimit;
                String overflowText = "... and " + otherPlayers + " other players worldwide!";
                terminal.writer().print("\033[" + currentY + ";" + (startX + (boxWidth - overflowText.length()) / 2) + "H");
                terminal.writer().print(overflowText);
                currentY++;
            }
            currentY++;

            // global rank is drawn
            String rankText = "Your global rank: " + (globalPosition > 0 ? globalPosition : "N/A");
            terminal.writer().print("\033[" + currentY + ";" + (startX + (boxWidth - rankText.length()) / 2) + "H");
            terminal.writer().print(rankText);
            currentY += 2;
        }

        // quit message is drawn
        String quitText = "Game Over. Type 'quit' to close the application.";
        terminal.writer().print("\033[" + currentY + ";" + (startX + (boxWidth - quitText.length()) / 2) + "H");
        terminal.writer().print(quitText);

        // bottom border is drawn
        terminal.writer().print("\033[" + (startY + boxHeight - 1) + ";" + startX + "H");
        terminal.writer().print("└" + "─".repeat(boxWidth - 2) + "┘");
    }
}
