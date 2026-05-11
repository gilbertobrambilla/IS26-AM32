package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.model.Player;

import java.util.Objects;

/**
 * Represents the active session of a player linking the model entity to its network view.
 */
public class PlayerSession {
    private final Player player;
    private GameView view;

    /**
     * Constructs a new player session.
     *
     * @param player the player model entity.
     * @param view the view associated with the player.
     */
    public PlayerSession(Player player, GameView view) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(view, "view cannot be null");

        this.player = player;
        this.view = view;
    }

    /**
     * Retrieves the player model entity.
     *
     * @return the player entity.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Retrieves the view associated with the player.
     *
     * @return the player view.
     */
    public GameView getView() {
        return this.view;
    }

    /**
     * Sets a new view for the player session.
     *
     * @param view the new view to be set.
     */
    public void setView(GameView view) {
        Objects.requireNonNull(view, "view cannot be null");
        // view is updated for the session
        this.view = view;
    }
}
