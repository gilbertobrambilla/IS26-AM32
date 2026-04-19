package it.polimi.ingsw.am32.controller.commands.game;

import it.polimi.ingsw.am32.controller.GameController;

import java.io.Serializable;

public interface GameCommand extends Serializable {
    void execute(GameController controller, String playerNickname);
}
