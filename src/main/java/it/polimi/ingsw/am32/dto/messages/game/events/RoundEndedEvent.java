package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.CardDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

/**
 * A round-ended event is represented.
 */
public class RoundEndedEvent implements MessageDTO {
    private final int newRound;
    private final int newEra;
    private final List<CardDTO> upperTribeRow;
    private final List<CardDTO> lowerTribeRow;
    private final List<CardDTO> upperBuildingRow;
    private final List<CardDTO> lowerBuildingRow;
    private final int tribeDeckSize;
    private final int tribeDeckTopCardEra;

    /**
     * A round-ended event is initialized with the next round board state.
     *
     * @param newRound the new round number
     * @param newEra the new era number
     * @param upperTribeRow the updated upper tribe row
     * @param lowerTribeRow the updated lower tribe row
     * @param upperBuildingRow the updated upper building row
     * @param lowerBuildingRow the updated lower building row
     * @param tribeDeckSize the updated tribe deck size
     * @param tribeDeckTopCardEra the era of the top tribe deck card
     */
    public RoundEndedEvent(int newRound, int newEra, List<CardDTO> upperTribeRow, List<CardDTO> lowerTribeRow, List<CardDTO> upperBuildingRow, List<CardDTO> lowerBuildingRow, int tribeDeckSize, int tribeDeckTopCardEra) {
        this.newRound = newRound;
        this.newEra = newEra;
        this.upperTribeRow = List.copyOf(upperTribeRow);
        this.lowerTribeRow = List.copyOf(lowerTribeRow);
        this.upperBuildingRow = List.copyOf(upperBuildingRow);
        this.lowerBuildingRow = List.copyOf(lowerBuildingRow);
        this.tribeDeckSize = tribeDeckSize;
        this.tribeDeckTopCardEra = tribeDeckTopCardEra;
    }

    /**
     * The new round number is returned.
     *
     * @return the new round number
     */
    public int getNewRound() {
        return newRound;
    }

    /**
     * The new era number is returned.
     *
     * @return the new era number
     */
    public int getNewEra() {
        return newEra;
    }

    /**
     * The updated upper tribe row is returned.
     *
     * @return the upper tribe row
     */
    public List<CardDTO> getUpperTribeRow() {
        return upperTribeRow;
    }

    /**
     * The updated lower tribe row is returned.
     *
     * @return the lower tribe row
     */
    public List<CardDTO> getLowerTribeRow() {
        return lowerTribeRow;
    }

    /**
     * The updated upper building row is returned.
     *
     * @return the upper building row
     */
    public List<CardDTO> getUpperBuildingRow() {
        return upperBuildingRow;
    }

    /**
     * The updated lower building row is returned.
     *
     * @return the lower building row
     */
    public List<CardDTO> getLowerBuildingRow() {
        return lowerBuildingRow;
    }

    /**
     * The updated tribe deck size is returned.
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
     * The visitor is accepted to dispatch the event.
     *
     * @param visitor the client message visitor
     */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
