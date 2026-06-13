package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;
import java.util.List;

import it.polimi.ingsw.am32.model.enums.GamePhase;

/**
 * A full game state data transfer object is represented.
 */
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

    /**
     * A full game state data transfer object is initialized with the visible state.
     *
     * @param gameId the game identifier
     * @param players the visible players
     * @param upperTribeRow the upper tribe row
     * @param lowerTribeRow the lower tribe row
     * @param upperBuildingRow the upper building row
     * @param lowerBuildingRow the lower building row
     * @param offerTrack the offer track
     * @param turnOrder the turn order tile
     * @param tribeDeckSize the tribe deck size
     * @param tribeDeckTopCardEra the era of the top tribe deck card
     * @param currentRound the current round
     * @param currentEra the current era
     * @param currentPhase the current game phase
     * @param currentPlayer the current player nickname
     */
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

    /**
     * The game identifier is returned.
     *
     * @return the game identifier
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * The visible players are returned.
     *
     * @return the visible players
     */
    public List<PlayerDTO> getPlayers() {
        return players;
    }

    /**
     * The upper tribe row is returned.
     *
     * @return the upper tribe row
     */
    public List<CardDTO> getUpperTribeRow() {
        return upperTribeRow;
    }

    /**
     * The lower tribe row is returned.
     *
     * @return the lower tribe row
     */
    public List<CardDTO> getLowerTribeRow() {
        return lowerTribeRow;
    }

    /**
     * The upper building row is returned.
     *
     * @return the upper building row
     */
    public List<CardDTO> getUpperBuildingRow() {
        return upperBuildingRow;
    }

    /**
     * The lower building row is returned.
     *
     * @return the lower building row
     */
    public List<CardDTO> getLowerBuildingRow() {
        return lowerBuildingRow;
    }

    /**
     * The offer track is returned.
     *
     * @return the offer track
     */
    public List<OfferTileDTO> getOfferTrack() {
        return offerTrack;
    }

    /**
     * The turn order tile is returned.
     *
     * @return the turn order tile
     */
    public TurnOrderDTO getTurnOrder() {
        return turnOrder;
    }

    /**
     * The tribe deck size is returned.
     *
     * @return the tribe deck size
     */
    public int getTribeDeckSize() {
        return tribeDeckSize;
    }

    /**
     * The era of the top tribe deck card is returned.
     *
     * @return the top tribe deck card era
     */
    public int getTribeDeckTopCardEra() {
        return tribeDeckTopCardEra;
    }

    /**
     * The current round is returned.
     *
     * @return the current round
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * The current era is returned.
     *
     * @return the current era
     */
    public int getCurrentEra() {
        return currentEra;
    }

    /**
     * The current game phase is returned.
     *
     * @return the current game phase
     */
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * The current player nickname is returned.
     *
     * @return the current player nickname
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }
}
