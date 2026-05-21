package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.dto.setup.OfferTileDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the execution of the action resolution command based on the offer tile limits.
 */
public class ResolveHandler implements InputHandler {

    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        LightGameState state = controller.getLightGameState();

        if (state == null) {
            throw new IllegalArgumentException("game state is not currently available");
        }

        if (!state.getMyNickname().equals(state.getCurrentPlayer())) {
            throw new IllegalArgumentException("it is not your turn");
        }

        LightOfferTile playerTile = null;
        for (LightOfferTile tile : state.getOfferTrack()) {
            if (state.getMyNickname().equals(tile.getOccupyingPlayer())) {
                playerTile = tile;
                break;
            }
        }

        if (playerTile == null) {
            throw new IllegalArgumentException("no totem placed on offer track");
        }

        OfferTileDTO tileDto = AssetManager.getInstance().getOfferTile(playerTile.getId());
        int allowedUpper = tileDto.getDrawsFromUpperRow();
        int allowedLower = tileDto.getDrawsFromLowerRow();
        int totalAllowed = allowedUpper + allowedLower;

        List<String> characters = new ArrayList<>();
        List<String> buildings = new ArrayList<>();
        List<String> selUpper = new ArrayList<>();
        List<String> selLower = new ArrayList<>();

        String currentFlag = null;
        LightPlayer player = state.getPlayers().get(state.getMyNickname());

        for (int i = 1; i < args.length; i++) {
            String token = args[i];

            if (token.startsWith("-")) {
                currentFlag = token;
                continue;
            }

            if (currentFlag == null) {
                throw new IllegalArgumentException("card id provided before any flag");
            }

            boolean found = false;

            if ("-c".equalsIgnoreCase(currentFlag)) {
                String cardId = "character_" + token;

                if (state.getUpperTribeRow().contains(cardId)) {
                    characters.add(cardId);
                    selUpper.add(cardId);
                    found = true;
                } else if (state.getLowerTribeRow().contains(cardId)) {
                    characters.add(cardId);
                    selLower.add(cardId);
                    found = true;
                }
            } else if ("-b".equalsIgnoreCase(currentFlag)) {
                String buildId = "building_" + token;

                if (!player.canAffordAdditionalBuilding(buildings, buildId)) {
                    throw new IllegalArgumentException("insufficient resources for building " + token);
                }

                if (state.getUpperBuildingRow().contains(buildId)) {
                    buildings.add(buildId);
                    selUpper.add(buildId);
                    found = true;
                } else if (state.getLowerBuildingRow().contains(buildId)) {
                    buildings.add(buildId);
                    selLower.add(buildId);
                    found = true;
                }
            } else {
                throw new IllegalArgumentException("unknown flag '" + currentFlag + "' provided");
            }

            if (!found) {
                throw new IllegalArgumentException("card '" + token + "' with flag '" + currentFlag + "' is not available on the board");
            }
        }

        int requestedCards = characters.size() + buildings.size();

        if (totalAllowed == 0 && requestedCards > 0) {
            throw new IllegalArgumentException("your tile only provides food, no cards allowed");
        }

        boolean isCompleteOrStuck = state.isSelectionCompleteOrStuck(
                state.getMyNickname(),
                allowedUpper,
                allowedLower,
                selUpper,
                selLower,
                buildings
        );

        if (!isCompleteOrStuck) {
            throw new IllegalArgumentException("incomplete action: you must select the exact number of cards required, or there are still affordable cards you can pick");
        }

        if (selUpper.size() < allowedUpper || selLower.size() < allowedLower) {
            System.out.println("\n[*] Forced confirmation: your resources are depleted or no affordable cards remain.");
        }

        controller.requestResolveAction(characters, buildings);
    }

    @Override
    public String getSyntax() {
        return "resolve [-c|-b] <id>";
    }

    @Override
    public String getDescription() {
        return "resolves your turn";
    }
}
