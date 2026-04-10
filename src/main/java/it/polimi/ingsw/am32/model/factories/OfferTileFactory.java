package it.polimi.ingsw.am32.model.factories;

import it.polimi.ingsw.am32.model.dto.setup.OfferTileDTO;
import it.polimi.ingsw.am32.model.tiles.Action;
import it.polimi.ingsw.am32.model.tiles.OfferTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OfferTileFactory implements TileFactory<OfferTileDTO, OfferTile> {

    @Override
    public List<OfferTile> createTiles(List<OfferTileDTO> dtoList) {
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        List<OfferTile> tiles = new ArrayList<>();

        for (OfferTileDTO dto : dtoList) {

            Action action = new Action(
                dto.getActDrawsUpper(),
                dto.getActDrawsLower(),
                dto.getActFoodBonus()
            );

            OfferTile newTile = new OfferTile(
                dto.getId(),
                dto.getMinPlayers(),
                action
            );

            tiles.add(newTile);
        }

        return tiles;
    }
}
