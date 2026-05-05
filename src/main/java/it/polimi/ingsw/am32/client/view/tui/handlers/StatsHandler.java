package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * Handles the visual focus on player statistics and inventory.
 */
public class StatsHandler implements InputHandler {

    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // target nickname is determined
        String targetNickname;

        if (args.length == 1 || "me".equalsIgnoreCase(args[1])) {
            // local player nickname is used
            targetNickname = controller.getMyNickname();
        } else if ("clear".equalsIgnoreCase(args[1])) {
            // focus is cleared
            controller.requestPlayerFocus(null);
            return;
        } else {
            // provided nickname is used
            targetNickname = args[1];
        }

        // existence of player is validated locally
        if (!controller.getLightGameState().getPlayers().containsKey(targetNickname)) {
            // exception is thrown if player is not found
            throw new IllegalArgumentException("player not found in the current game");
        }

        // visual focus is requested
        controller.requestPlayerFocus(targetNickname);
    }
}
