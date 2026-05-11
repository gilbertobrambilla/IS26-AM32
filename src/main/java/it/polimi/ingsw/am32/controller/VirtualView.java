package it.polimi.ingsw.am32.controller;

/**
 * Contract for the virtual view adapter bridging the controller and the network.
 */
public interface VirtualView extends LobbyView, GameView {

    /**
     * Destroys the virtual view closing the underlying connection cleanly.
     */
    void destroy();

    /**
     * Retrieves the unique identifier associated with this connection.
     *
     * @return the unique connection id.
     */
    String getConnectionID();
}
