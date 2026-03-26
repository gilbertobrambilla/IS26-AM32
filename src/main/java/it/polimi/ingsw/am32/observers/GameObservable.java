package it.polimi.ingsw.am32.observers;

public interface GameObservable {
    void addObserver(GameObserver observer);
    void removeObserver(GameObserver observer);
    void notifyObservers(GameEvent event);
}
