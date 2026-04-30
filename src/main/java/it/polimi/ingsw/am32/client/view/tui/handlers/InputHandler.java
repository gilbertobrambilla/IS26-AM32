package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * The contract for the cli input handlers is defined.
 */
public interface InputHandler {

    /**
     * The command is executed using the provided arguments.
     *
     * @param controller the client controller.
     * @param args the raw input tokens.
     * @throws IllegalArgumentException if the provided arguments are invalid.
     */
    void execute(ClientController controller, String[] args) throws IllegalArgumentException;
}
