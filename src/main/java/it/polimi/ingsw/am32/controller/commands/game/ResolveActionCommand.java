package it.polimi.ingsw.am32.controller.commands.game;

import it.polimi.ingsw.am32.controller.GameController;

import java.util.List;

/**
 * Command used to process the resolution of an action in the game.
 */
public class ResolveActionCommand implements GameCommand {
    private final List<String> characters;
    private final List<String> buildings;

    /**
     * A new resolve action command is constructed with the selected cards.
     *
     * @param characters the list of selected character card ids
     * @param buildings the list of selected building card ids
     */
    public ResolveActionCommand(List<String> characters, List<String> buildings) {
        this.characters = characters;
        this.buildings = buildings;
    }

    /**
     * The resolve action request is executed on the game controller.
     *
     * @param controller the game controller processing the request
     * @param playerNickname the nickname of the player issuing the command
     */
    @Override
    public void execute(GameController controller, String playerNickname) {
        controller.processResolveAction(playerNickname, this.characters, this.buildings);
    }
}
