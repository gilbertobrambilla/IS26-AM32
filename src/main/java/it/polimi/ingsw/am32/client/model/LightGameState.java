package it.polimi.ingsw.am32.client.model;

import it.polimi.ingsw.am32.model.enums.GamePhase;
import it.polimi.ingsw.am32.dto.messages.game.models.*;
import java.util.*;
import java.util.stream.Collectors;

public class LightGameState {
    private final String myNickname;
    private int gameId;
    private String currentPlayer;
    private GamePhase currentPhase;
    private int currentRound;
    private int currentEra;
    private LightTurnOrderTile turnOrderTile;
    private final Map<String, LightPlayer> players;
    private List<LightOfferTile> offerTrack;
    private List<String> upperTribeRow;
    private List<String> lowerTribeRow;
    private List<String> upperBuildingRow;
    private List<String> lowerBuildingRow;
    private int tribeDeckSize;
    private int tribeDeckTopCardEra;

    /**
     * Constructs a new LightGameState associated with the user's nickname.
     * @param myNickname The nickname of the local player.
     */
    public LightGameState(String myNickname) {
        this.myNickname = myNickname;
        // players map is initialized
        this.players = new HashMap<>();
        // offer track is initialized
        this.offerTrack = new ArrayList<>();
        // current player is initialized to a safe placeholder
        this.currentPlayer = "WAITING";
        // primitive values are initialized
        this.currentRound = 0;
        this.currentEra = 0;
        this.tribeDeckSize = 0;
        this.tribeDeckTopCardEra = 0;
        // card rows are initialized to empty lists to prevent ui crashes
        this.upperTribeRow = new ArrayList<>();
        this.lowerTribeRow = new ArrayList<>();
        this.upperBuildingRow = new ArrayList<>();
        this.lowerBuildingRow = new ArrayList<>();
    }

    /**
     * Full state initialization from GameStateDTO.
     * @param dto The data transfer object containing the full game state.
     */
    public void initializeFromDTO(GameStateDTO dto) {
        this.gameId = dto.getGameId();
        this.currentPhase = dto.getCurrentPhase();
        this.currentPlayer = dto.getCurrentPlayer();
        this.currentRound = dto.getCurrentRound();
        this.currentEra = dto.getCurrentEra();
        this.tribeDeckSize = dto.getTribeDeckSize();
        this.tribeDeckTopCardEra = dto.getTribeDeckTopCardEra();

        // turn order tile is initialized from the nested dto object
        TurnOrderDTO turnOrderDTO = dto.getTurnOrder();
        this.turnOrderTile = new LightTurnOrderTile(turnOrderDTO.getId(), turnOrderDTO.getPlayers());

        // rows are mapped from CardDTO to String IDs
        this.upperTribeRow = dto.getUpperTribeRow().stream().map(CardDTO::getId).collect(Collectors.toList());
        this.lowerTribeRow = dto.getLowerTribeRow().stream().map(CardDTO::getId).collect(Collectors.toList());
        this.upperBuildingRow = dto.getUpperBuildingRow().stream().map(CardDTO::getId).collect(Collectors.toList());
        this.lowerBuildingRow = dto.getLowerBuildingRow().stream().map(CardDTO::getId).collect(Collectors.toList());

        // players are mapped to light players
        this.players.clear();
        for (PlayerDTO p : dto.getPlayers()) {
            List<String> chars = p.getCharacterCards().stream().map(CardDTO::getId).collect(Collectors.toList());
            List<String> builds = p.getBuildingCards().stream().map(CardDTO::getId).collect(Collectors.toList());
            this.players.put(p.getNickname(), new LightPlayer(p.getNickname(), p.getTotem(), p.getFood(), p.getPrestige(), p.isConnected(), chars, builds));
        }

        // offer track is mapped
        this.offerTrack = dto.getOfferTrack().stream().map(t -> new LightOfferTile(t.getId(), t.getPlayer())).collect(Collectors.toList());
    }

    /**
     * Updates the state when a totem is placed.
     * @param nickname The nickname of the player.
     * @param tileId The id of the offer tile.
     * @param nextPlayer The nickname of the next player.
     */
    public void updateTotemPlacement(String nickname, String tileId, String nextPlayer) {
        for (LightOfferTile tile : offerTrack) {
            if (tile.getId().equals(tileId)) {
                // occupying player is updated
                tile.setOccupyingPlayer(nickname);
                break;
            }
        }

        List<String> order = new ArrayList<>(this.turnOrderTile.getPlayers());
        order.remove(nickname);
        this.turnOrderTile = new LightTurnOrderTile(this.turnOrderTile.getTileId(), order);

        // current player is updated
        this.currentPlayer = nextPlayer;
    }

    /**
     * Updates the state when an action is resolved.
     * @param nickname The nickname of the player.
     * @param characters The list of character card ids.
     * @param buildings The list of building card ids.
     * @param stats The list of stat changes.
     * @param nextPlayer The nickname of the next player.
     */
    public void updateActionResolution(String nickname, List<String> characters, List<String> buildings, List<PlayerStatsChangedDTO> stats, String nextPlayer) {
        LightPlayer p = players.get(nickname);
        if (p != null) {
            // character cards are added
            characters.forEach(p::addCharacterCard);
            // building cards are added
            buildings.forEach(p::addBuildingCard);
        }

        if (this.upperTribeRow != null) this.upperTribeRow.removeAll(characters);
        if (this.lowerTribeRow != null) this.lowerTribeRow.removeAll(characters);
        if (this.upperBuildingRow != null) this.upperBuildingRow.removeAll(buildings);
        if (this.lowerBuildingRow != null) this.lowerBuildingRow.removeAll(buildings);

        // stats are updated
        updateStats(stats);
        // current player is updated
        this.currentPlayer = nextPlayer;
    }

    /**
     * Updates the state when player statistics change.
     * @param statsList The list of stat changes.
     */
    public void updateStats(List<PlayerStatsChangedDTO> statsList) {
        for (PlayerStatsChangedDTO s : statsList) {
            LightPlayer p = players.get(s.getPlayer());
            if (p != null) {
                // stats are updated for specific player
                p.updateStats(s.getFood(), s.getPrestige());
            }
        }
    }

    /**
     * Updates the state during phase changes.
     * @param nextPhase The new game phase.
     * @param nextPlayer The nickname of the next player.
     */
    public void updatePhaseChange(GamePhase nextPhase, String nextPlayer) {
        // current phase is updated
        this.currentPhase = nextPhase;
        // current player is updated
        this.currentPlayer = nextPlayer;
    }

    /**
     * Updates the connection status of a player.
     * @param nickname The nickname of the player.
     * @param isConnected The boolean connection status.
     */
    public void updatePlayerConnection(String nickname, boolean isConnected) {
        LightPlayer p = players.get(nickname);
        if (p != null) {
            // connection status is updated
            p.setConnected(isConnected);
        }
    }

    /**
     * Determines if the player's current selection satisfies the offer tile requirements,
     * or if the player is legitimately stuck because there are no other affordable cards left.
     *
     * @param nickname The nickname of the player performing the action.
     * @param reqUpper The number of cards required from the upper row.
     * @param reqLower The number of cards required from the lower row.
     * @param selUpper The list of card IDs currently selected from the upper row.
     * @param selLower The list of card IDs currently selected from the lower row.
     * @param selBuildings The list of building card IDs currently selected across all rows.
     * @return true if the selection is complete or the player cannot afford any more required cards.
     */
    public boolean isSelectionCompleteOrStuck(
            String nickname, int reqUpper, int reqLower,
            List<String> selUpper, List<String> selLower, List<String> selBuildings) {

        LightPlayer player = this.players.get(nickname);
        if (player == null) return false;

        int selectedUpper = selUpper.size();
        int selectedLower = selLower.size();

        // exact match with the offer tile requirements
        if (selectedUpper == reqUpper && selectedLower == reqLower) {
            return true;
        }

        // invalid UI state: the user selected more cards than required
        if (selectedUpper > reqUpper || selectedLower > reqLower) {
            return false;
        }

        // check if the user missed an affordable card in the upper row
        if (selectedUpper < reqUpper && hasAffordableUnselectedCards(
                this.upperTribeRow, this.upperBuildingRow, selUpper, selBuildings, player)) {
            return false;
        }

        // check if the user missed an affordable card in the lower row
        if (selectedLower < reqLower && hasAffordableUnselectedCards(
                this.lowerTribeRow, this.lowerBuildingRow, selLower, selBuildings, player)) {
            return false;
        }

        // the user hasn't met the exact requirements, but they are legitimately stuck
        return true;
    }

    /**
     * Helper method to check if there is at least one affordable card left in the specified rows.
     */
    private boolean hasAffordableUnselectedCards(
            List<String> tribeRow, List<String> buildingRow,
            List<String> selectedInRow, List<String> allSelectedBuildings, LightPlayer player) {

        // ensure we only count IDs that look like characters
        long freeCharacters = tribeRow.stream()
                .filter(id -> id.startsWith("character_"))
                .filter(id -> !selectedInRow.contains(id))
                .count();

        if (freeCharacters > 0) return true;

        // ensure we only count IDs that look like buildings
        long affordableBuildings = buildingRow.stream()
                .filter(id -> id.startsWith("building"))
                .filter(id -> !selectedInRow.contains(id))
                .filter(id -> player.canAffordAdditionalBuilding(allSelectedBuildings, id))
                .count();

        return affordableBuildings > 0;
    }

    // getters

    public String getMyNickname() {
        return myNickname;
    }

    public int getGameId() {
        return gameId;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getCurrentEra() {
        return currentEra;
    }

    public LightTurnOrderTile getTurnOrderTile() {
        return turnOrderTile;
    }

    public Map<String, LightPlayer> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public List<LightOfferTile> getOfferTrack() {
        if (offerTrack == null) return Collections.emptyList();
        return Collections.unmodifiableList(offerTrack);
    }

    public List<String> getUpperTribeRow() {
        if (upperTribeRow == null) return Collections.emptyList();
        return Collections.unmodifiableList(upperTribeRow);
    }

    public List<String> getLowerTribeRow() {
        if (lowerTribeRow == null) return Collections.emptyList();
        return Collections.unmodifiableList(lowerTribeRow);
    }

    public List<String> getUpperBuildingRow() {
        if (upperBuildingRow == null) return Collections.emptyList();
        return Collections.unmodifiableList(upperBuildingRow);
    }

    public List<String> getLowerBuildingRow() {
        if (lowerBuildingRow == null) return Collections.emptyList();
        return Collections.unmodifiableList(lowerBuildingRow);
    }

    public int getTribeDeckSize() {
        return tribeDeckSize;
    }

    public int getTribeDeckTopCardEra() {
        return tribeDeckTopCardEra;
    }

    /**
     * Updates the state when an action finishes and the totem returns.
     */
    public void returnTotem(String nickname, int slotIndex) {
        // Libera l'Offer Tile
        for (LightOfferTile tile : offerTrack) {
            if (nickname.equals(tile.getOccupyingPlayer())) {
                tile.setOccupyingPlayer(null);
                break;
            }
        }

        List<String> order = new ArrayList<>(this.turnOrderTile.getPlayers());
        order.remove(nickname);

        if (slotIndex >= order.size()) {
            order.add(nickname);
        } else {
            order.add(slotIndex, nickname);
        }

        this.turnOrderTile = new LightTurnOrderTile(this.turnOrderTile.getTileId(), order);
    }

    public void updateRound(int newRound, int newEra, List<String> upperTribe, List<String> lowerTribe, List<String> upperBuilding, List<String> lowerBuilding, String nextPlayer) {
        this.currentRound = newRound;
        this.currentEra = newEra;
        this.upperTribeRow = new ArrayList<>(upperTribe);
        this.lowerTribeRow = new ArrayList<>(lowerTribe);
        this.upperBuildingRow = new ArrayList<>(upperBuilding);
        this.lowerBuildingRow = new ArrayList<>(lowerBuilding);
        this.currentPlayer = nextPlayer;
    }
}
