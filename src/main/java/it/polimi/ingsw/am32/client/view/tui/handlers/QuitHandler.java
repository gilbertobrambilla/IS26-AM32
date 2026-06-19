package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * The application termination is handled.
 */
public class QuitHandler implements InputHandler {

    /**
     * The quit command is executed.
     *
     * @param controller the client controller
     * @param args the raw input tokens
     * @throws IllegalArgumentException if the provided arguments are invalid
     */
    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // notification is printed
        System.out.println("disconnecting and closing application...");
        // app termination is requested via controller
        controller.requestExit();
    }

    /**
     * The command syntax is returned.
     *
     * @return the command syntax
     */
    @Override
    public String getSyntax() {
        // syntax string is returned
        return "quit";
    }

    /**
     * The command description is returned.
     *
     * @return the command description
     */
    @Override
    public String getDescription() {
        // description string is returned
        return "exits the game";
    }
}
