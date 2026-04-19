package it.polimi.ingsw.am32.dto.messages.game.models;

import it.polimi.ingsw.am32.model.enums.TotemColor;

import java.util.List;

public class PlayerDTO {
    private final String nickname;
    private final TotemColor totem;
    private final int food;
    private final int prestige;
    private final boolean isConnected;
    private final List<CardDTO> characterCards;
    private final List<CardDTO> buildingCards;

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

    public String getNickname() {
        return this.nickname;
    }

    public TotemColor getTotem() {
        return this.totem;
    }

    public int getFood() {
        return this.food;
    }

    public int getPrestige() {
        return this.prestige;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public List<CardDTO> getCharacterCards() {
        return this.characterCards;
    }

    public List<CardDTO> getBuildingCards() {
        return this.buildingCards;
    }
}
