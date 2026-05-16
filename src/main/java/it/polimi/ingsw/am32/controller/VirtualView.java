package it.polimi.ingsw.am32.controller;

/**
 * Contract for the virtual view adapter bridging the controller and the network.
 */
public interface VirtualView extends LobbyView, GameView {

    /**
     * The unique identifier associated with this connection is retrieved.
     *
     * @return the unique connection id
     */
    String getConnectionID();

    /**
     * The virtual view is destroyed, closing the underlying connection cleanly.
     */
    void destroy();
}
