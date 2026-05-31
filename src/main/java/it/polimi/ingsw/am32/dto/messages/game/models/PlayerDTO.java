package it.polimi.ingsw.am32.dto.messages.game.models;

import it.polimi.ingsw.am32.model.enums.TotemColor;

import java.io.Serializable;
import java.util.List;

/**
 * A player data transfer object is represented.
 */
public class PlayerDTO implements Serializable {
    private final String nickname;
    private final TotemColor totem;
    private final int food;
    private final int prestige;
    private final boolean isConnected;
    private final List<CardDTO> characterCards;
    private final List<CardDTO> buildingCards;

    /**
     * A player data transfer object is initialized with visible player data.
     *
     * @param nickname the player nickname
     * @param totem the player totem color
     * @param food the current food amount
     * @param prestige the current prestige amount
     * @param isConnected true if the player is connected
     * @param characterCards the owned character cards
     * @param buildingCards the owned building cards
     */
    public PlayerDTO(String nickname,
                     TotemColor totem,
                     int food,
                     int prestige,
                     boolean isConnected,
                     List<CardDTO> characterCards,
                     List<CardDTO> buildingCards) {
        this.nickname = nickname;
        this.totem = totem;
        this.food = food;
        this.prestige = prestige;
        this.isConnected = isConnected;
        this.characterCards = List.copyOf(characterCards);
        this.buildingCards = List.copyOf(buildingCards);
    }

    /**
     * The player nickname is returned.
     *
     * @return the player nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * The player totem color is returned.
     *
     * @return the player totem color
     */
    public TotemColor getTotem() {
        return totem;
    }

    /**
     * The current food amount is returned.
     *
     * @return the current food amount
     */
    public int getFood() {
        return food;
    }

    /**
     * The current prestige amount is returned.
     *
     * @return the current prestige amount
     */
    public int getPrestige() {
        return prestige;
    }

    /**
     * The connection flag is returned.
     *
     * @return true if the player is connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * The owned character cards are returned.
     *
     * @return the owned character cards
     */
    public List<CardDTO> getCharacterCards() {
        return characterCards;
    }

    /**
     * The owned building cards are returned.
     *
     * @return the owned building cards
     */
    public List<CardDTO> getBuildingCards() {
        return buildingCards;
    }
}
