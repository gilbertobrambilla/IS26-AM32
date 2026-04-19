package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.model.Player;

import java.util.Objects;

public class PlayerSession {
    private final Player player;
    private GameView view;

    public PlayerSession(Player player, GameView view) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(view, "view cannot be null");

        this.player = player;
        this.view = view;
    }

    public Player getPlayer() {
        return this.player;
    }

    public GameView getView() {
        return this.view;
    }

    public void setView(GameView view) {
        Objects.requireNonNull(view, "view cannot be null");

        this.view = view;
    }
}
