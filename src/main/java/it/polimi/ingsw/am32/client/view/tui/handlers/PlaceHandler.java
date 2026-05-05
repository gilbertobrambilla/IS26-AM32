package it.polimi.ingsw.am32.client.view.tui.handlers;

import it.polimi.ingsw.am32.client.controller.ClientController;

/**
 * The handler for the place command is implemented.
 */
public class PlaceHandler implements InputHandler {

    @Override
    public void execute(ClientController controller, String[] args) throws IllegalArgumentException {
        // argument length is validated
        if (args.length != 2) {
            throw new IllegalArgumentException("invalid arguments. usage: place <tile_letter>");
        }

        // tile letter is extracted
        String letter = args[1];

        // full tile id is constructed
        String tileId = "offer_tile_" + letter.toUpperCase();

        // totem placement is requested
        controller.requestPlaceTotem(tileId);
    }
}
