package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.network.exceptions.NetworkException;

/**
 * A network action that may fail is represented.
 */
@FunctionalInterface
public interface NetworkAction {
    /**
     * The network action is executed.
     *
     * @throws NetworkException if a network error occurs
     */
    void execute() throws NetworkException;
}
