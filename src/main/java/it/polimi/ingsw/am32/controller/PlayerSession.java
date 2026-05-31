package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.model.Player;

import java.util.Objects;

/**
 * The active session of a player linking the model entity to its network view is represented.
 */
public class PlayerSession {
    private final Player player;
    private VirtualView view;

    /**
     * A new player session is constructed.
     *
     * @param player the player model entity
     * @param view the virtual view associated with the player
     */
    public PlayerSession(Player player, VirtualView view) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(view, "view cannot be null");

        this.player = player;
        this.view = view;
    }

    /**
     * The player model entity is retrieved.
     *
     * @return the player entity
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * The view associated with the player is retrieved.
     *
     * @return the player view
     */
    public VirtualView getView() {
        return this.view;
    }
}
