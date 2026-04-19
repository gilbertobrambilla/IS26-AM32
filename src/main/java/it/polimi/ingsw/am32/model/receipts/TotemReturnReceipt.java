package it.polimi.ingsw.am32.model.receipts;

public class TotemReturnReceipt {
    private final String playerNickname;
    private final int turnOrderPosition;
    private final PlayerStatsChangeReceipt statsChange;
    private final String nextPlayerNickname;

    public TotemReturnReceipt(String playerNickname, int turnOrderPosition, PlayerStatsChangeReceipt statsChange, String nextPlayerNickname) {
        this.playerNickname = playerNickname;
        this.turnOrderPosition = turnOrderPosition;
        this.statsChange = statsChange;
        this.nextPlayerNickname = nextPlayerNickname;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public int getTurnOrderPosition() {
        return turnOrderPosition;
    }

    public PlayerStatsChangeReceipt getStatsChange() {
        return statsChange;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }
}
