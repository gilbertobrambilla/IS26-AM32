package it.polimi.ingsw.am32.client.model;

import it.polimi.ingsw.am32.model.enums.TotemColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LightPlayer {
    private final String nickname;
    private final TotemColor totemColor;
    private int food;
    private int prestige;
    private boolean isConnected;
    private final List<String> characterCards;
    private final List<String> buildingCards;

    public LightPlayer(String nickname, TotemColor totemColor, int food, int prestige, boolean isConnected, List<String> characterCards, List<String> buildingCards) {
        this.nickname = nickname;
        this.totemColor = totemColor;
        this.food = food;
        this.prestige = prestige;
        this.isConnected = isConnected;
        this.characterCards = new ArrayList<>(characterCards);
        this.buildingCards = new ArrayList<>(buildingCards);
    }

    public String getNickname() {
        return nickname;
    }

    public TotemColor getTotemColor() {
        return totemColor;
    }

    public int getFood() {
        return food;
    }

    public int getPrestige() {
        return prestige;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public List<String> getCharacterCards() {
        return Collections.unmodifiableList(characterCards);
    }

    public List<String> getBuildingCards() {
        return Collections.unmodifiableList(buildingCards);
    }

    public void updateStats(int food, int prestige) {
        this.food = food;
        this.prestige = prestige;
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    public void addCharacterCard(String cardId) {
        if (cardId != null) this.characterCards.add(cardId);
    }

    public void addBuildingCard(String cardId) {
        if (cardId != null) this.buildingCards.add(cardId);
    }
}
