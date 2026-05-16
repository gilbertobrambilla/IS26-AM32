package it.polimi.ingsw.am32.controller.commands.game;

import it.polimi.ingsw.am32.controller.GameController;

import java.io.Serializable;

/**
 * Interface representing a command executed in the game.
 */
public interface GameCommand extends Serializable {

    /**
     * The command is executed on the game controller.
     *
     * @param controller the game controller processing the request
     * @param playerNickname the nickname of the player issuing the command
     */
    void execute(GameController controller, String playerNickname);
}
