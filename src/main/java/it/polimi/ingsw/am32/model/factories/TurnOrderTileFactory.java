package it.polimi.ingsw.am32.model.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.dto.setup.TurnOrderTileDTO;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TurnOrderTileFactory implements TileFactory<TurnOrderTileDTO, TurnOrderTile> {
    private final ObjectMapper objectMapper;

    public TurnOrderTileFactory() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<TurnOrderTile> createTiles(List<TurnOrderTileDTO> dtoList) {
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        List<TurnOrderTile> tiles = new ArrayList<>();

        for (TurnOrderTileDTO dto : dtoList) {
            List<Integer> foodModifiers;
            List<Integer> prestigeModifiers;

            try {
                Integer[] foodArray = objectMapper.readValue(dto.getFoodModifiers(), Integer[].class);
                Integer[] prestigeArray = objectMapper.readValue(dto.getPrestigeModifiers(), Integer[].class);

                foodModifiers = List.of(foodArray);
                prestigeModifiers = List.of(prestigeArray);

            } catch (Exception e) {
                throw new RuntimeException("error parsing json for tile " + dto.getId(), e);
            }

            TurnOrderTile newTile = new TurnOrderTile(
                dto.getId(),
                dto.getRequiredPlayers(),
                foodModifiers,
                prestigeModifiers
            );

            tiles.add(newTile);
        }

        return tiles;
    }

    public TurnOrderTile createTile(TurnOrderTileDTO dto) {
        Objects.requireNonNull(dto, "dtoList cannot be null");

        List<Integer> foodModifiers;
        List<Integer> prestigeModifiers;

        try {
            Integer[] foodArray = objectMapper.readValue(dto.getFoodModifiers(), Integer[].class);
            Integer[] prestigeArray = objectMapper.readValue(dto.getPrestigeModifiers(), Integer[].class);

            foodModifiers = List.of(foodArray);
            prestigeModifiers = List.of(prestigeArray);

        } catch (Exception e) {
            throw new RuntimeException("error parsing json for tile " + dto.getId(), e);
        }

        TurnOrderTile newTile = new TurnOrderTile(
                dto.getId(),
                dto.getRequiredPlayers(),
                foodModifiers,
                prestigeModifiers
        );

        return newTile;
    }
}
