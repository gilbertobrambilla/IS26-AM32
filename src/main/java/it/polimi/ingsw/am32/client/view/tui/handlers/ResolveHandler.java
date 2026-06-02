package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.dto.setup.OfferTileDTO;
import it.polimi.ingsw.am32.model.enums.GamePhase;

import java.util.ArrayList;
import java.util.List;

/**
 * Action resolution command execution is handled based on offer tile limits.
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

        // current game phase is extracted
        GamePhase currentPhase = state.getCurrentPhase();

        if (currentPhase == GamePhase.EXTRA_ACQUISITION) {
            // skip is requested if no arguments are provided
            if (args.length == 1) {
                controller.requestExtraAcquisition(null);
                return;
            }

            // argument length is validated for single card acquisition
            if (args.length != 3) {
                throw new IllegalArgumentException("invalid arguments. Use: resolve [-c|-b] <id> or just 'resolve' to pass");
            }

            String flag = args[1];
            String token = args[2];
            String cardId;

            // flag is evaluated to construct full card id
            if ("-c".equalsIgnoreCase(flag)) {
                cardId = "character_" + token;
            } else if ("-b".equalsIgnoreCase(flag)) {
                cardId = "building_" + token;
            } else {
                throw new IllegalArgumentException("unknown flag '" + flag + "' provided");
            }

            // validity of the extra acquisition is verified
            if (!state.isExtraAcquisitionValid(state.getMyNickname(), cardId)) {
                throw new IllegalArgumentException("card '" + token + "' is not available in the upper row or you cannot afford it");
            }

            // extra acquisition is requested
            controller.requestExtraAcquisition(cardId);

        } else if (currentPhase == GamePhase.RESOLVE_ACTIONS) {

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

            // food-only tiles (0 card draws) are resolved immediately with no arguments
            if (totalAllowed == 0 && args.length == 1) {
                controller.requestResolveAction(new ArrayList<>(), new ArrayList<>());
                return;
            }

            // exception is thrown if user attempts to skip standard action illegally
            if (args.length == 1) {
                throw new IllegalArgumentException("cannot skip standard action. Provide cards using [-c|-b] <id>");
            }

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
                it.polimi.ingsw.am32.log.LogManager.getInstance().log(it.polimi.ingsw.am32.log.LogLevel.INFO, "[*] Forced confirmation: your resources are depleted or no affordable cards remain.");
            }

            // standard action resolution is requested
            controller.requestResolveAction(characters, buildings);

        } else {
            // exception is thrown if resolve is called in unsupported phases
            throw new IllegalArgumentException("action not allowed in current phase");
        }
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
