package it.polimi.ingsw.am32.controller.commands.game;

import it.polimi.ingsw.am32.controller.GameController;

public class PlaceTotemCommand implements GameCommand {
    private final String tileId;

    public PlaceTotemCommand(String tileId) {
        this.tileId = tileId;
    }

    @Override
    public void execute(GameController controller, String playerNickname) {
        controller.processPlaceTotem(playerNickname, tileId);
    }
}
