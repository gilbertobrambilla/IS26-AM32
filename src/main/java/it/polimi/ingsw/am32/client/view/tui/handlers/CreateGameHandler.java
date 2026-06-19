package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * The handler for the create game command is implemented.
 */
public class CreateGameHandler implements InputHandler {

    /**
     * A game creation command is executed.
     *
     * @param controller the client controller
     * @param args the raw input tokens
     * @throws IllegalArgumentException if the provided arguments are invalid
     */
    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // argument length is validated
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid arguments for command. Expected usage: create <players>");
        }

        try {
            // number of players is parsed
            int players = Integer.parseInt(args[1]);
            // game creation is requested
            controller.requestCreateGame(players);
        } catch (NumberFormatException e) {
            // exception is thrown for invalid number format
            throw new IllegalArgumentException("Argument must be a valid number");
        }
    }

    /**
     * The command syntax is returned.
     *
     * @return the command syntax
     */
    @Override
    public String getSyntax() {
        // syntax string is returned
        return "create <players>";
    }

    /**
     * The command description is returned.
     *
     * @return the command description
     */
    @Override
    public String getDescription() {
        // description string is returned
        return "creates a new game";
    }
}
