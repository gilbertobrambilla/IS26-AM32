package it.polimi.ingsw.am32.controller.commands.game;

import it.polimi.ingsw.am32.controller.GameController;

/**
 * A command used to process the placement of a totem in the game is represented.
 */
public class PlaceTotemCommand implements GameCommand {
    private final String tileId;

    /**
     * A new place totem command is constructed with the chosen tile id.
     *
     * @param tileId the id of the selected offer tile
     */
    public PlaceTotemCommand(String tileId) {
        this.tileId = tileId;
    }

    /**
     * The place totem request is executed on the game controller.
     *
     * @param controller the game controller processing the request
     * @param playerNickname the nickname of the player issuing the command
     */
    @Override
    public void execute(GameController controller, String playerNickname) {
        controller.processPlaceTotem(playerNickname, this.tileId);
    }
}
