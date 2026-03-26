package it.polimi.ingsw.am32.cards.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.cards.Card;
import it.polimi.ingsw.am32.enums.EventType;

public abstract class EventCard extends Card {
    private final boolean isFinal;

    public EventCard(String id, int era, boolean isFinal) {
        super(id, era);
        this.isFinal = isFinal;
    }

    @Override
    public boolean isEvent() {
        return true;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public abstract EventType getEventType();
    public abstract void resolveEvent(GameState gameState);
}
