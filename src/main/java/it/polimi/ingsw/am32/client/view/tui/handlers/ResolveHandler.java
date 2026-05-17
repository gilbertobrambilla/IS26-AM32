package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.dto.setup.OfferTileDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the execution of the action resolution command based on the offer tile limits.
 */
public class ResolveHandler implements InputHandler {

    /**
     * The command is executed using the provided arguments.
     *
     * @param controller the client controller
     * @param args the raw input tokens
     * @throws IllegalArgumentException if the provided arguments are invalid
     */
    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        LightGameState state = controller.getLightGameState();

        // state existence is verified
        if (state == null) {
            throw new IllegalArgumentException("game state is not currently available");
        }

        // turn is validated locally
        if (!state.getMyNickname().equals(state.getCurrentPlayer())) {
            throw new IllegalArgumentException("it is not your turn");
        }

        // player offer tile is identified
        LightOfferTile playerTile = null;
        for (LightOfferTile tile : state.getOfferTrack()) {
            if (state.getMyNickname().equals(tile.getOccupyingPlayer())) {
                playerTile = tile;
                break;
            }
        }

        // tile occupation is verified
        if (playerTile == null) {
            throw new IllegalArgumentException("no totem placed on offer track");
        }

        OfferTileDTO tileDto = AssetManager.getInstance().getOfferTile(playerTile.getId());
        int allowedUpper = tileDto.getDrawsFromUpperRow();
        int allowedLower = tileDto.getDrawsFromLowerRow();
        int totalAllowed = allowedUpper + allowedLower;

        List<String> characters = new ArrayList<>();
        List<String> buildings = new ArrayList<>();
        int takenUpper = 0;
        int takenLower = 0;

        String currentFlag = null;

        // arguments are parsed sequentially
        for (int i = 1; i < args.length; i++) {
            String token = args[i];

            // flag is updated if present
            if (token.startsWith("-")) {
                currentFlag = token;
                continue;
            }

            // exception is thrown if token is provided before any flag
            if (currentFlag == null) {
                throw new IllegalArgumentException("card id provided before any flag");
            }

            boolean found = false;

            // character flag is handled
            if ("-c".equalsIgnoreCase(currentFlag)) {
                String charId = "character_" + token;

                // upper tribe row is checked
                if (state.getUpperTribeRow().contains(charId)) {
                    characters.add(charId);
                    takenUpper++;
                    found = true;
                }
                // lower tribe row is checked if not found in upper
                else if (state.getLowerTribeRow().contains(charId)) {
                    characters.add(charId);
                    takenLower++;
                    found = true;
                }
            }
            // building flag is handled
            else if ("-b".equalsIgnoreCase(currentFlag)) {
                String buildId = "building_" + token;

                // upper building row is checked
                if (state.getUpperBuildingRow().contains(buildId)) {
                    buildings.add(buildId);
                    takenUpper++;
                    found = true;
                }
                // lower building row is checked if not found in upper
                else if (state.getLowerBuildingRow().contains(buildId)) {
                    buildings.add(buildId);
                    takenLower++;
                    found = true;
                }
            }
            // unknown flag is handled
            else {
                throw new IllegalArgumentException("unknown flag '" + currentFlag + "' provided");
            }

            // card availability is verified
            if (!found) {
                throw new IllegalArgumentException("card '" + token + "' with flag '" + currentFlag + "' is not available on the board");
            }
        }

        int requestedCards = characters.size() + buildings.size();

        // requested amount is validated against tile limits
        if (totalAllowed == 0 && requestedCards > 0) {
            throw new IllegalArgumentException("your tile only provides food, no cards allowed");
        }

        if (requestedCards != totalAllowed) {
            throw new IllegalArgumentException("you must pick exactly " + totalAllowed + " cards");
        }

        // row limits are verified
        if (takenUpper > allowedUpper || takenLower > allowedLower) {
            throw new IllegalArgumentException("cards picked do not match your tile's row limits");
        }

        // action resolution is requested
        controller.requestResolveAction(characters, buildings);
    }

    @Override
    public String getSyntax() {
        // syntax string is returned
        return "resolve [-c|-b] <id>";
    }

    @Override
    public String getDescription() {
        // description string is returned
        return "resolves your turn";
    }
}
