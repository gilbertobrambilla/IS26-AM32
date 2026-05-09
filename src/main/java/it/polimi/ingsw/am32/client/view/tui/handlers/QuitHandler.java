package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * Handles the application termination.
 */
public class QuitHandler implements InputHandler {

    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // notification is printed
        System.out.println("disconnecting and closing application...");
        // client is disconnected
        controller.shutdown();
        // system is terminated
        System.exit(0);
    }
}
