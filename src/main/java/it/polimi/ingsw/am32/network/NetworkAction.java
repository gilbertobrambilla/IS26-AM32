package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.network.exceptions.NetworkException;

@FunctionalInterface
public interface NetworkAction {
    void execute() throws NetworkException;
}