package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the execution of the take action command.
 */
public class TakeHandler implements InputHandler {

    private enum TargetList {
        NONE,
        CHARACTERS,
        BUILDINGS
    }

    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // lists are initialized
        List<String> characters = new ArrayList<>();
        List<String> buildings = new ArrayList<>();

        // target state is initialized
        TargetList currentTarget = TargetList.NONE;

        // arguments are iterated
        for (int i = 1; i < args.length; i++) {
            // current token is extracted
            String token = args[i];

            if ("-c".equals(token)) {
                // target is set to characters
                currentTarget = TargetList.CHARACTERS;
            } else if ("-b".equals(token)) {
                // target is set to buildings
                currentTarget = TargetList.BUILDINGS;
            } else {
                // token is evaluated based on target
                if (currentTarget == TargetList.CHARACTERS) {
                    // character is added with required prefix
                    characters.add("char_" + token);
                } else if (currentTarget == TargetList.BUILDINGS) {
                    // building is added with required prefix
                    buildings.add("build_" + token);
                } else {
                    // exception is thrown for invalid syntax
                    throw new IllegalArgumentException("invalid syntax. usage: take [-c chars...] [-b builds...]");
                }
            }
        }

        // action resolution is requested
        controller.requestResolveAction(characters, buildings);
    }
}
