package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * The handler for the join game command is implemented.
 */
public class JoinGameHandler implements InputHandler {

    /**
     * A join game command is executed.
     *
     * @param controller the client controller
     * @param args the raw input tokens
     * @throws IllegalArgumentException if the provided arguments are invalid
     */
    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // argument length is validated
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid arguments for command. Expected usage: join <id>.");
        }

        try {
            // game id is parsed
            int gameId = Integer.parseInt(args[1]);
            // game join is requested
            controller.requestJoinGame(gameId);
        } catch (NumberFormatException e) {
            // exception is thrown for invalid number format
            throw new IllegalArgumentException("Argument must be a valid number.");
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
        return "join <id>";
    }

    /**
     * The command description is returned.
     *
     * @return the command description
     */
    @Override
    public String getDescription() {
        // description string is returned
        return "joins an existing game";
    }
}
