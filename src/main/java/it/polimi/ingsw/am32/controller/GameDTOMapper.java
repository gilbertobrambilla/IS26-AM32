package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.messages.game.models.*;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;
import it.polimi.ingsw.am32.model.receipts.TotemReturnReceipt;
import it.polimi.ingsw.am32.model.tiles.OfferTile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class responsible for mapping model objects to data transfer objects.
 */
public class GameDTOMapper {

    /**
     * Builds a comprehensive snapshot of the current game state.
     *
     * @param gameId the unique identifier for this game
     * @param gameState the game state model
     * @param activeClientsNicknames the list of currently connected players
     * @return the generated game state dto
     */
    public static GameStateDTO buildGameStateDTO(int gameId, GameState gameState, List<String> activeClientsNicknames) {
        // players are mapped to dtos
        List<PlayerDTO> playersDTO = gameState.getPlayers().stream()
                .map(p -> mapPlayer(p, activeClientsNicknames))
                .collect(Collectors.toList());

        // rows are retrieved from model and mapped to dtos
        List<CardDTO> upperTribeRowDTO = mapCharacterCardsList(gameState.getUpperTribeRow());
        List<CardDTO> lowerTribeRowDTO = mapCharacterCardsList(gameState.getLowerTribeRow());
        List<CardDTO> upperBuildingRowDTO = mapBuildingCardsList(gameState.getUpperBuildingRow());
        List<CardDTO> lowerBuildingRowDTO = mapBuildingCardsList(gameState.getLowerBuildingRow());

        // offer track is mapped to dtos
        List<OfferTileDTO> offerTrackDTO = gameState.getOfferTrack().stream()
                .map(GameDTOMapper::mapOfferTile)
                .collect(Collectors.toList());

        // turn order nicknames are extracted from model
        List<String> playerNicknames = gameState.getTurnOrder().stream()
                .map(Player::getNickname)
                .collect(Collectors.toList());

        // turn order tile id is retrieved from model
        String turnOrderTileId = gameState.getTurnOrderTileId();

        // turn order dto is instantiated
        TurnOrderDTO turnOrderDTO = new TurnOrderDTO(turnOrderTileId, playerNicknames);

        // game state dto is built and returned
        return new GameStateDTO(
                gameId,
                playersDTO,
                upperTribeRowDTO,
                lowerTribeRowDTO,
                upperBuildingRowDTO,
                lowerBuildingRowDTO,
                offerTrackDTO,
                turnOrderDTO,
                gameState.getTribeDeckSize(),
                gameState.getTribeDeckFirstCardEra(),
                gameState.getCurrentRound(),
                gameState.getCurrentEra(),
                gameState.getCurrentPhase(),
                gameState.getCurrentPlayer() != null ? gameState.getCurrentPlayer().getNickname() : null
        );
    }

    /**
     * Maps a list of player stats change receipts to a list of dtos.
     *
     * @param receipts the list of receipts from the model
     * @return the mapped list of dtos
     */
    public static List<PlayerStatsChangedDTO> mapStatsChangesList(List<PlayerStatsChangeReceipt> receipts) {
        if (receipts == null) return new ArrayList<>();
        // receipts list is mapped to dtos
        return receipts.stream()
                .map(GameDTOMapper::mapStatsChange)
                .collect(Collectors.toList());
    }

    /**
     * Maps a totem return receipt to its corresponding dto.
     *
     * @param receipt the receipt from the model
     * @return the mapped dto, or null if the receipt is null
     */
    public static TotemReturnedDTO mapTotemReturn(TotemReturnReceipt receipt) {
        if (receipt == null) return null;
        // totem return dto is instantiated
        return new TotemReturnedDTO(
                receipt.getPlayerNickname(),
                receipt.getTurnOrderPosition(),
                mapStatsChange(receipt.getStatsChange()),
                receipt.getNextPlayerNickname()
        );
    }

    /**
     * Maps a list of building card models to a list of card dtos.
     *
     * @param buildings the list of building card models
     * @return the mapped list of card dtos
     */
    public static List<CardDTO> mapBuildingCardsList(List<BuildingCard> buildings) {
        if (buildings == null) return new ArrayList<>();
        // building cards are processed as a stream and mapped
        return buildings.stream()
                .map(building -> new CardDTO(building.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Maps a list of card models to a list of card dtos.
     *
     * @param cards the list of card models
     * @return the mapped list of card dtos
     */
    public static List<CardDTO> mapCharacterCardsList(List<Card> cards) {
        if (cards == null) return new ArrayList<>();
        // cards are processed as a stream and mapped
        return cards.stream()
                .map(card -> new CardDTO(card.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Maps a single player stats change receipt to its corresponding dto.
     *
     * @param receipt the receipt from the model
     * @return the mapped dto, or null if the receipt is null
     */
    private static PlayerStatsChangedDTO mapStatsChange(PlayerStatsChangeReceipt receipt) {
        if (receipt == null) return null;
        // stats change dto is built
        return new PlayerStatsChangedDTO(
                receipt.getPlayerNickname(),
                receipt.getFoodChange(),
                receipt.getPrestigeChange(),
                receipt.getCurrentFood(),
                receipt.getCurrentPrestige(),
                receipt.getReason()
        );
    }

    /**
     * Maps a player model to a player dto.
     *
     * @param player the player model
     * @param activeClientsNicknames the list of connected player nicknames
     * @return the mapped player dto
     */
    private static PlayerDTO mapPlayer(Player player, List<String> activeClientsNicknames) {
        if (player == null) return null;
        // player cards are mapped to dtos
        List<CardDTO> characterCards = mapCharacterCardsList(new ArrayList<>(player.getCharacterList()));
        List<CardDTO> buildingCards = mapBuildingCardsList(new ArrayList<>(player.getBuildingList()));
        // player dto is constructed
        return new PlayerDTO(
                player.getNickname(),
                player.getTotem(),
                player.getFood(),
                player.getPrestige(),
                activeClientsNicknames.contains(player.getNickname()),
                characterCards,
                buildingCards
        );
    }

    /**
     * Maps an offer tile model to an offer tile dto.
     *
     * @param tile the offer tile model
     * @return the mapped offer tile dto
     */
    private static OfferTileDTO mapOfferTile(OfferTile tile) {
        if (tile == null) return null;
        // occupying player nickname is extracted if present
        String occupyingPlayer = tile.isFree() ? null : tile.getOccupyingPlayer().getNickname();
        // offer tile dto is built
        return new OfferTileDTO(tile.getId(), occupyingPlayer);
    }
}
