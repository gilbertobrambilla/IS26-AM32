package it.polimi.ingsw.am32.controller.commands.game;

import it.polimi.ingsw.am32.controller.GameController;

/**
 * Command used to process an extra acquisition action in the game.
 */
public class ExtraAcquisitionCommand implements GameCommand {
    private final String cardId;

    /**
     * A new extra acquisition command is constructed with the selected card.
     *
     * @param cardId the id of the selected card, or null to skip the action.
     */
    public ExtraAcquisitionCommand(String cardId) {
        this.cardId = cardId;
    }

    /**
     * The extra acquisition request is executed on the game controller.
     *
     * @param controller the game controller processing the request
     * @param playerNickname the nickname of the player issuing the command
     */
    @Override
    public void execute(GameController controller, String playerNickname) {
        controller.processExtraAcquisition(playerNickname, this.cardId);
    }
}
