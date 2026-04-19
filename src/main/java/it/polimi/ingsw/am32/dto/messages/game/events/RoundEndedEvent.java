package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.game.models.CardDTO;

import java.util.List;

public class RoundEndedEvent {
    private final int newRoundNumber;
    private final int newEraNumber;

    // absolute state of the board rows after the round ends
    private final List<CardDTO> upperTribeRow;
    private final List<CardDTO> lowerTribeRow;
    private final List<CardDTO> upperBuildingRow;
    private final List<CardDTO> lowerBuildingRow;

    private final String nextPlayerNickname;

    public RoundEndedEvent(int newRoundNumber,
                           int newEraNumber,
                           List<CardDTO> upperTribeRow,
                           List<CardDTO> lowerTribeRow,
                           List<CardDTO> upperBuildingRow,
                           List<CardDTO> lowerBuildingRow,
                           String nextPlayerNickname) {
        this.newRoundNumber = newRoundNumber;
        this.newEraNumber = newEraNumber;
        this.upperTribeRow = upperTribeRow;
        this.lowerTribeRow = lowerTribeRow;
        this.upperBuildingRow = upperBuildingRow;
        this.lowerBuildingRow = lowerBuildingRow;
        this.nextPlayerNickname = nextPlayerNickname;
    }

    public int getNewRoundNumber() {
        return newRoundNumber;
    }

    public int getNewEraNumber() {
        return newEraNumber;
    }

    public List<CardDTO> getUpperTribeRow() {
        return upperTribeRow;
    }

    public List<CardDTO> getLowerTribeRow() {
        return lowerTribeRow;
    }

    public List<CardDTO> getUpperBuildingRow() {
        return upperBuildingRow;
    }

    public List<CardDTO> getLowerBuildingRow() {
        return lowerBuildingRow;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }
}
