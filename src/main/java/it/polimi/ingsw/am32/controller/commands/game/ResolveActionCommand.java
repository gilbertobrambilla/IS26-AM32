package it.polimi.ingsw.am32.controller.commands.game;

import it.polimi.ingsw.am32.controller.GameController;

import java.util.List;

public class ResolveActionCommand implements GameCommand {
    private final List<String> characters;
    private final List<String> buildings;

    public ResolveActionCommand(List<String> characters, List<String> buildings) {
        this.characters = characters;
        this.buildings = buildings;
    }

    @Override
    public void execute(GameController controller, String playerNickname) {
        controller.processResolveAction(playerNickname, characters, buildings);
    }
}
