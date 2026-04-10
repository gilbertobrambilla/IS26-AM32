package it.polimi.ingsw.am32.model.dto;

import java.util.List;
import it.polimi.ingsw.am32.model.enums.GamePhase;

public class GameStateDTO {
    private final int gameId;
    private final int currentRound;
    private final GamePhase currentPhase;
    private final PlayerIdentificationDTO currentPlayer;
    private final List<PlayerDTO> players;
    private final List<CardDTO> upperRow;
    private final List<CardDTO> lowerRow;
    private final List<OfferTileDTO> offerTrack;
    private final List<PlayerIdentificationDTO> turnOrder;
    private final int tribeDeckSize;

    public GameStateDTO(int gameId,
                        int currentRound,
                        GamePhase currentPhase,
                        PlayerIdentificationDTO currentPlayer,
                        List<PlayerDTO> players,
                        List<CardDTO> upperRow,
                        List<CardDTO> lowerRow,
                        List<OfferTileDTO> offerTrack,
                        List<PlayerIdentificationDTO> turnOrder,
                        int tribeDeckSize) {

        this.gameId = gameId;
        this.currentRound = currentRound;
        this.currentPhase = currentPhase;
        this.currentPlayer = currentPlayer;
        this.players = List.copyOf(players);
        this.upperRow = List.copyOf(upperRow);
        this.lowerRow = List.copyOf(lowerRow);
        this.offerTrack = List.copyOf(offerTrack);
        this.turnOrder = List.copyOf(turnOrder);
        this.tribeDeckSize = tribeDeckSize;
    }

    public int getGameId() {
        return gameId;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public PlayerIdentificationDTO getCurrentPlayer() {
        return currentPlayer;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public List<CardDTO> getUpperRow() {
        return upperRow;
    }

    public List<CardDTO> getLowerRow() {
        return lowerRow;
    }

    public List<OfferTileDTO> getOfferTrack() {
        return offerTrack;
    }

    public List<PlayerIdentificationDTO> getTurnOrder() {
        return turnOrder;
    }

    public int getTribeDeckSize() {
        return tribeDeckSize;
    }
}
