package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * The application termination is handled.
 */
public class QuitHandler implements InputHandler {

    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // notification is printed
        System.out.println("disconnecting and closing application...");
        // app termination is requested via controller
        controller.requestExit();
    }

    @Override
    public String getSyntax() {
        // syntax string is returned
        return "quit";
    }

    @Override
    public String getDescription() {
        // description string is returned
        return "exits the game";
    }
}
