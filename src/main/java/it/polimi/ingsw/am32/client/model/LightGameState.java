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
     * A new light game state associated with the user's nickname is constructed.
     *
     * @param myNickname the nickname of the local player
     */
    public LightGameState(String myNickname) {
        this.myNickname = myNickname;
        this.players = new HashMap<>();
        this.offerTrack = new ArrayList<>();
        // current player is initialized to a safe placeholder
        this.currentPlayer = "WAITING";
        // primitive values are initialized
        this.currentRound = 0;
        this.currentEra = 0;
        this.tribeDeckSize = 0;
        this.tribeDeckTopCardEra = 0;
        this.upperTribeRow = new ArrayList<>();
        this.lowerTribeRow = new ArrayList<>();
        this.upperBuildingRow = new ArrayList<>();
        this.lowerBuildingRow = new ArrayList<>();
    }

    /**
     * The full state is initialized from the game state dto.
     *
     * @param dto the data transfer object containing the full game state
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

        // rows are mapped from card dto to string ids
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
     * The state is updated when a totem is placed.
     *
     * @param nickname the nickname of the player
     * @param tileId the id of the offer tile
     * @param nextPlayer the nickname of the next player
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
     * The state is updated when an action is resolved.
     *
     * @param nickname the nickname of the player
     * @param characters the list of character card ids
     * @param buildings the list of building card ids
     * @param statsChange the statistics change
     */
    public void updateActionResolution(String nickname, List<String> characters, List<String> buildings, PlayerStatsChangedDTO statsChange) {
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
        if (statsChange != null) {
            updateStats(Collections.singletonList(statsChange));
        }
    }

    /**
     * The state is updated with the results of an extra acquisition.
     *
     * @param nickname the nickname of the player
     * @param acquiredCardId the id of the acquired card, or null if skipped
     * @param statsChange the statistics change
     */
    public void updateExtraAcquisitionResolution(String nickname, String acquiredCardId, PlayerStatsChangedDTO statsChange) {
        if (acquiredCardId != null) {
            LightPlayer p = players.get(nickname);
            if (p != null) {
                if (acquiredCardId.startsWith("building_")) {
                    p.addBuildingCard(acquiredCardId);
                    if (this.upperBuildingRow != null) this.upperBuildingRow.remove(acquiredCardId);
                } else {
                    p.addCharacterCard(acquiredCardId);
                    if (this.upperTribeRow != null) this.upperTribeRow.remove(acquiredCardId);
                }
            }
        }

        // stats are updated
        if (statsChange != null) {
            updateStats(Collections.singletonList(statsChange));
        }
    }

    /**
     * The state is updated when player statistics change.
     *
     * @param statsList the list of stat changes
     */
    public void updateStats(List<PlayerStatsChangedDTO> statsList) {
        for (PlayerStatsChangedDTO s : statsList) {
            LightPlayer p = players.get(s.getPlayer());
            if (p != null) {
                // stats are updated for specific player
                p.updateStats(
                        p.getFood() + s.getFoodChange(),
                        p.getPrestige() + s.getPrestigeChange()
                );
            }
        }
    }

    /**
     * The state is updated during phase changes.
     *
     * @param nextPhase the new game phase
     * @param nextPlayer the nickname of the next player
     */
    public void updatePhaseChange(GamePhase nextPhase, String nextPlayer) {
        // current phase is updated
        this.currentPhase = nextPhase;
        // current player is updated
        this.currentPlayer = nextPlayer;
    }

    /**
     * The connection status of a player is updated.
     *
     * @param nickname the nickname of the player
     * @param isConnected the boolean connection status
     */
    public void updatePlayerConnection(String nickname, boolean isConnected) {
        LightPlayer p = players.get(nickname);
        if (p != null) {
            // connection status is updated
            p.setConnected(isConnected);
        }
    }

    /**
     * The player's current selection is checked against mandatory character requirements.
     * Character cards are mandatory up to the offer tile row limits, while building cards remain optional.
     *
     * @param nickname the nickname of the player performing the action
     * @param reqUpper the maximum number of cards allowed from the upper row
     * @param reqLower the maximum number of cards allowed from the lower row
     * @param selUpper the list of card IDs currently selected from the upper row
     * @param selLower the list of card IDs currently selected from the lower row
     * @return true if all mandatory character cards are selected without exceeding row limits
     */
    public boolean isMandatorySelectionComplete(
            String nickname, int reqUpper, int reqLower,
            List<String> selUpper, List<String> selLower) {

        if (!this.players.containsKey(nickname)) return false;

        int selectedUpper = selUpper.size();
        int selectedLower = selLower.size();

        // cannot select more than the tile allows
        if (selectedUpper > reqUpper || selectedLower > reqLower) {
            return false;
        }

        // mapToInt combined with sum natively returns an int, avoiding long-to-int down casting
        int availableUpperCharacters = this.upperTribeRow.stream()
                .mapToInt(id -> id.startsWith("character_") ? 1 : 0)
                .sum();

        int availableLowerCharacters = this.lowerTribeRow.stream()
                .mapToInt(id -> id.startsWith("character_") ? 1 : 0)
                .sum();

        // the player must take all available character cards up to the tile limit
        int mandatoryUpper = Math.min(reqUpper, availableUpperCharacters);
        int mandatoryLower = Math.min(reqLower, availableLowerCharacters);

        return selectedUpper >= mandatoryUpper && selectedLower >= mandatoryLower;
    }

    /**
     * The specified card is checked for an extra acquisition action.
     *
     * @param nickname the nickname of the player
     * @param cardId the id of the selected card
     * @return true if the card is in the upper row and affordable, false otherwise
     */
    public boolean isExtraAcquisitionValid(String nickname, String cardId) {
        LightPlayer player = this.players.get(nickname);
        if (player == null) return false;

        if (cardId.startsWith("character_")) {
            // character card existence in the upper row is verified
            return this.upperTribeRow.contains(cardId);
        } else if (cardId.startsWith("building_")) {
            // building card existence in the upper row is verified
            if (!this.upperBuildingRow.contains(cardId)) {
                return false;
            }
            // building card affordability is verified
            return player.canAffordAdditionalBuilding(new ArrayList<>(), cardId);
        }

        return false;
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
     * The state is updated when an action finishes and the totem returns.
     */
    public void returnTotem(String nickname, int slotIndex) {
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

    public void updateRound(int newRound, int newEra, List<String> upperTribe, List<String> lowerTribe, List<String> upperBuilding, List<String> lowerBuilding, int tribeDeckSize, int tribeDeckTopCardEra) {
        this.currentRound = newRound;
        this.currentEra = newEra;
        this.upperTribeRow = new ArrayList<>(upperTribe);
        this.lowerTribeRow = new ArrayList<>(lowerTribe);
        this.upperBuildingRow = new ArrayList<>(upperBuilding);
        this.lowerBuildingRow = new ArrayList<>(lowerBuilding);
        this.tribeDeckSize = tribeDeckSize;
        this.tribeDeckTopCardEra = tribeDeckTopCardEra;
    }
}
