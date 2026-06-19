package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * The visual focus on player statistics and inventory is handled.
 */
public class StatsHandler implements InputHandler {

    /**
     * A player statistics focus command is executed.
     *
     * @param controller the client controller
     * @param args the raw input tokens
     * @throws IllegalArgumentException if the provided arguments are invalid
     */
    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // target nickname is determined
        String targetNickname;

        // focus is cleared if no arguments are provided
        if (args.length == 1) {
            controller.requestPlayerFocus(null);
            return;
        } else if ("me".equalsIgnoreCase(args[1])) {
            // local player nickname is used
            targetNickname = controller.getMyNickname();
        } else {
            // provided nickname is used
            targetNickname = args[1];
        }

        // game state existence is verified locally
        if (controller.getLightGameState() == null) {
            throw new IllegalArgumentException("Game state is not currently available.");
        }

        // existence of player is validated locally
        if (!controller.getLightGameState().getPlayers().containsKey(targetNickname)) {
            // exception is thrown if player is not found
            throw new IllegalArgumentException("Player '" + targetNickname + "' not found in the current game.");
        }

        // visual focus is requested
        controller.requestPlayerFocus(targetNickname);
    }

    /**
     * The command syntax is returned.
     *
     * @return the command syntax
     */
    @Override
    public String getSyntax() {
        // syntax string is returned
        return "stats [me|player]";
    }

    /**
     * The command description is returned.
     *
     * @return the command description
     */
    @Override
    public String getDescription() {
        // description string is returned
        return "shows player stats";
    }
}
