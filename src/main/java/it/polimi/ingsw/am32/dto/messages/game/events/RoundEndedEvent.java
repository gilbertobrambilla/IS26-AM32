package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.CardDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

public class RoundEndedEvent implements MessageDTO {
    private final int round;
    private final int era;
    private final List<CardDTO> upperTribeRow;
    private final List<CardDTO> lowerTribeRow;
    private final List<CardDTO> upperBuildingRow;
    private final List<CardDTO> lowerBuildingRow;
    private final String nextPlayer;

    public RoundEndedEvent(int round, int era, List<CardDTO> upperTribeRow, List<CardDTO> lowerTribeRow, List<CardDTO> upperBuildingRow, List<CardDTO> lowerBuildingRow, String nextPlayer) {
        this.round = round;
        this.era = era;
        this.upperTribeRow = List.copyOf(upperTribeRow);
        this.lowerTribeRow = List.copyOf(lowerTribeRow);
        this.upperBuildingRow = List.copyOf(upperBuildingRow);
        this.lowerBuildingRow = List.copyOf(lowerBuildingRow);
        this.nextPlayer = nextPlayer;
    }

    public int getRound() {
        return round;
    }

    public int getEra() {
        return era;
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

    public String getNextPlayer() {
        return nextPlayer;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
