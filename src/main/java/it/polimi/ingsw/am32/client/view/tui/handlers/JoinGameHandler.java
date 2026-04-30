package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * The handler for the join game command is implemented.
 */
public class JoinGameHandler implements InputHandler {

    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // argument length is validated
        if (args.length != 2) {
            throw new IllegalArgumentException("invalid arguments. usage: join <id>");
        }

        try {
            // game id is parsed
            int gameId = Integer.parseInt(args[1]);
            // game join is requested
            controller.requestJoinGame(gameId);
        } catch (NumberFormatException e) {
            // exception is thrown for invalid number format
            throw new IllegalArgumentException("argument must be a valid number");
        }
    }
}
