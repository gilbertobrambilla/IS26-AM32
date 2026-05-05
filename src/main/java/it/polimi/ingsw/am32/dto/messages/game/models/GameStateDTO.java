package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;
import java.util.List;

import it.polimi.ingsw.am32.model.enums.GamePhase;

public class GameStateDTO implements Serializable {
    private final int gameId;
    private final List<PlayerDTO> players;
    private final List<CardDTO> upperTribeRow;
    private final List<CardDTO> lowerTribeRow;
    private final List<CardDTO> upperBuildingRow;
    private final List<CardDTO> lowerBuildingRow;
    private final List<OfferTileDTO> offerTrack;
    private final TurnOrderDTO turnOrder;
    private final int tribeDeckSize;
    private final int tribeDeckTopCardEra;
    private final int currentRound;
    private final int currentEra;
    private final GamePhase currentPhase;
    private final String currentPlayer;

    public GameStateDTO(int gameId,
                        List<PlayerDTO> players,
                        List<CardDTO> upperTribeRow,
                        List<CardDTO> lowerTribeRow,
                        List<CardDTO> upperBuildingRow,
                        List<CardDTO> lowerBuildingRow,
                        List<OfferTileDTO> offerTrack,
                        TurnOrderDTO turnOrder,
                        int tribeDeckSize,
                        int tribeDeckTopCardEra,
                        int currentRound,
                        int currentEra,
                        GamePhase currentPhase,
                        String currentPlayer) {
        this.gameId = gameId;
        this.players = List.copyOf(players);
        this.upperTribeRow = List.copyOf(upperTribeRow);
        this.lowerTribeRow = List.copyOf(lowerTribeRow);
        this.upperBuildingRow = List.copyOf(upperBuildingRow);
        this.lowerBuildingRow = List.copyOf(lowerBuildingRow);
        this.offerTrack = List.copyOf(offerTrack);
        this.turnOrder = turnOrder;
        this.tribeDeckSize = tribeDeckSize;
        this.tribeDeckTopCardEra = tribeDeckTopCardEra;
        this.currentRound = currentRound;
        this.currentEra = currentEra;
        this.currentPhase = currentPhase;
        this.currentPlayer = currentPlayer;
    }

    public int getGameId() {
        return gameId;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
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

    public List<OfferTileDTO> getOfferTrack() {
        return offerTrack;
    }

    public TurnOrderDTO getTurnOrder() {
        return turnOrder;
    }

    public int getTribeDeckSize() {
        return tribeDeckSize;
    }

    public int getTribeDeckTopCardEra() {
        return tribeDeckTopCardEra;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getCurrentEra() {
        return currentEra;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }
}
