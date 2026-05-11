package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightOfferTile;

/**
 * The handler for the place command is implemented.
 */
public class PlaceHandler implements InputHandler {

    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // argument length is validated
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid arguments for command. Expected usage: place <tile_letter>.");
        }

        // tile letter is extracted
        String letter = args[1].toUpperCase();

        // full tile id is constructed
        String tileId = "offer_tile_" + letter;

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

        // tile existence is verified locally
        boolean tileExists = false;
        for (LightOfferTile tile : state.getOfferTrack()) {
            if (tile.getId().equals(tileId)) {
                tileExists = true;
                break;
            }
        }

        // exception is thrown if tile is not found
        if (!tileExists) {
            throw new IllegalArgumentException("Tile letter '" + letter + "' is not available on the board.");
        }

        // totem placement is requested
        controller.requestPlaceTotem(tileId);
    }

    @Override
    public String getSyntax() {
        // syntax string is returned
        return "place <letter>";
    }

    @Override
    public String getDescription() {
        // description string is returned
        return "places a totem";
    }
}
