package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.messages.game.models.*;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;
import it.polimi.ingsw.am32.model.tiles.OfferTile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class responsible for mapping model objects to data transfer objects.
 */
public class GameDTOMapper {

    /**
     * A comprehensive snapshot of the current game state is built.
     *
     * @param gameId the unique identifier for this game
     * @param gameState the game state model
     * @param activeClientsNicknames the list of currently connected players
     * @return the generated game state dto
     */
    public static GameStateDTO buildGameStateDTO(int gameId, GameState gameState, List<String> activeClientsNicknames) {
        // players are mapped to dto
        List<PlayerDTO> playersDTO = gameState.getPlayers().stream()
                .map(p -> buildPlayerDTO(p, activeClientsNicknames))
                .collect(Collectors.toList());

        // rows are retrieved from model and mapped to dto
        List<CardDTO> upperTribeRowDTO = buildCharacterCardDTOList(gameState.getUpperTribeRow());
        List<CardDTO> lowerTribeRowDTO = buildCharacterCardDTOList(gameState.getLowerTribeRow());
        List<CardDTO> upperBuildingRowDTO = buildBuildingCardDTOList(gameState.getUpperBuildingRow());
        List<CardDTO> lowerBuildingRowDTO = buildBuildingCardDTOList(gameState.getLowerBuildingRow());

        // offer track is mapped to dto
        List<OfferTileDTO> offerTrackDTO = gameState.getOfferTrack().stream()
                .map(GameDTOMapper::buildOfferTileDTO)
                .collect(Collectors.toList());

        // turn-order nicknames are extracted from model
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
     * A list of player stats change receipts is mapped to a list of dto.
     *
     * @param receipts the list of receipts from the model
     * @return the mapped list of dto
     */
    public static List<PlayerStatsChangedDTO> buildPlayerStatsChangedDTOList(List<PlayerStatsChangeReceipt> receipts) {
        if (receipts == null) return new ArrayList<>();
        // receipt list is mapped to dto
        return receipts.stream()
                .map(GameDTOMapper::buildPlayerStatsChangedDTO)
                .collect(Collectors.toList());
    }

    /**
     * A list of building card models is mapped to a list of card dto.
     *
     * @param buildings the list of building card models
     * @return the mapped list of card dto
     */
    public static List<CardDTO> buildBuildingCardDTOList(List<BuildingCard> buildings) {
        if (buildings == null) return new ArrayList<>();
        // building cards are processed as a stream and mapped
        return buildings.stream()
                .map(building -> new CardDTO(building.getId()))
                .collect(Collectors.toList());
    }

    /**
     * A list of card models is mapped to a list of card dto.
     *
     * @param cards the list of card models
     * @return the mapped list of card dto
     */
    public static List<CardDTO> buildCharacterCardDTOList(List<Card> cards) {
        if (cards == null) return new ArrayList<>();
        // cards are processed as a stream and mapped
        return cards.stream()
                .map(card -> new CardDTO(card.getId()))
                .collect(Collectors.toList());
    }

    /**
     * A single player stats changes receipt is mapped to its corresponding dto.
     *
     * @param receipt the receipt from the model
     * @return the mapped dto, or null if the receipt is null
     */
    public static PlayerStatsChangedDTO buildPlayerStatsChangedDTO(PlayerStatsChangeReceipt receipt) {
        if (receipt == null) return null;
        // stats-changes dto is built
        return new PlayerStatsChangedDTO(
                receipt.getPlayerNickname(),
                receipt.getFoodChange(),
                receipt.getPrestigeChange()
        );
    }

    /**
     * A player model is mapped to a player dto.
     *
     * @param player the player model
     * @param activeClientsNicknames the list of connected player nicknames
     * @return the mapped player dto
     */
    private static PlayerDTO buildPlayerDTO(Player player, List<String> activeClientsNicknames) {
        if (player == null) return null;
        // player cards are mapped to dto
        List<CardDTO> characterCards = buildCharacterCardDTOList(new ArrayList<>(player.getCharacterList()));
        List<CardDTO> buildingCards = buildBuildingCardDTOList(new ArrayList<>(player.getBuildingList()));
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
     * An offer tile model is mapped to an offer tile dto.
     *
     * @param tile the offer tile model
     * @return the mapped offer tile dto
     */
    private static OfferTileDTO buildOfferTileDTO(OfferTile tile) {
        if (tile == null) return null;
        // occupying player nickname is extracted if present
        String occupyingPlayer = tile.isFree() ? null : tile.getOccupyingPlayer().getNickname();
        // offer tile dto is built
        return new OfferTileDTO(tile.getId(), occupyingPlayer);
    }
}
