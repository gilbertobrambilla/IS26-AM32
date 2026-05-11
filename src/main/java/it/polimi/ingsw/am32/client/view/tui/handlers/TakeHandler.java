package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;

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

        // raw tokens are tracked for error reporting
        List<String> characterTokens = new ArrayList<>();
        List<String> buildingTokens = new ArrayList<>();

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
                    characters.add("character_" + token);
                    characterTokens.add(token);
                } else if (currentTarget == TargetList.BUILDINGS) {
                    // building is added with required prefix
                    buildings.add("building_" + token);
                    buildingTokens.add(token);
                } else {
                    // exception is thrown for missing flag
                    throw new IllegalArgumentException("Cards must follow either a '-c' or '-b' flag.");
                }
            }
        }

        // empty payload is prevented
        if (characters.isEmpty() && buildings.isEmpty()) {
            throw new IllegalArgumentException("At least one character or building card must be specified.");
        }

        // game state is retrieved
        LightGameState state = controller.getLightGameState();

        // state existence is verified
        if (state == null) {
            throw new IllegalArgumentException("Game state is not currently available.");
        }

        // turn is validated locally
        if (!state.getMyNickname().equals(state.getCurrentPlayer())) {
            throw new IllegalArgumentException("It is not your turn.");
        }

        // characters are validated against the board
        for (int i = 0; i < characters.size(); i++) {
            String charId = characters.get(i);
            boolean isAvailable = state.getUpperTribeRow().contains(charId) || state.getLowerTribeRow().contains(charId);

            // exception is thrown for unavailable character
            if (!isAvailable) {
                throw new IllegalArgumentException("Character card '" + characterTokens.get(i) + "' is not available on the board.");
            }
        }

        // buildings are validated against the board
        for (int i = 0; i < buildings.size(); i++) {
            String buildId = buildings.get(i);
            boolean isAvailable = state.getUpperBuildingRow().contains(buildId) || state.getLowerBuildingRow().contains(buildId);

            // exception is thrown for unavailable building
            if (!isAvailable) {
                throw new IllegalArgumentException("Building card '" + buildingTokens.get(i) + "' is not available on the board.");
            }
        }

        // action resolution is requested
        controller.requestResolveAction(characters, buildings);
    }

    @Override
    public String getSyntax() {
        // syntax string is returned
        return "take [-c|-b] <id>";
    }

    @Override
    public String getDescription() {
        // description string is returned
        return "acquires cards";
    }
}
