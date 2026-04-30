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
        this.players = new HashMap<>();
        this.offerTrack = new ArrayList<>();
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
     */
    public void updateTotemPlacement(String nickname, String tileId, String nextPlayer) {
        for (LightOfferTile tile : offerTrack) {
            if (tile.getId().equals(tileId)) {
                tile.setOccupyingPlayer(nickname);
                break;
            }
        }
        this.currentPlayer = nextPlayer;
    }

    /**
     * Updates the state when an action is resolved.
     */
    public void updateActionResolution(String nickname, List<String> characters, List<String> buildings, List<PlayerStatsChangedDTO> stats, String nextPlayer) {
        LightPlayer p = players.get(nickname);
        if (p != null) {
            characters.forEach(p::addCharacterCard);
            buildings.forEach(p::addBuildingCard);
        }
        updateStats(stats);
        this.currentPlayer = nextPlayer;
    }

    /**
     * Updates the state when player statistics change.
     */
    public void updateStats(List<PlayerStatsChangedDTO> statsList) {
        for (PlayerStatsChangedDTO s : statsList) {
            LightPlayer p = players.get(s.getPlayer());
            if (p != null) p.updateStats(s.getFood(), s.getPrestige());
        }
    }

    /**
     * Updates the state during phase changes.
     */
    public void updatePhaseChange(GamePhase nextPhase, String nextPlayer) {
        this.currentPhase = nextPhase;
        this.currentPlayer = nextPlayer;
    }

    /**
     * Updates the connection status of a player.
     */
    public void updatePlayerConnection(String nickname, boolean isConnected) {
        LightPlayer p = players.get(nickname);
        if (p != null) p.setConnected(isConnected);
    }

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
        return players;
    }

    public List<LightOfferTile> getOfferTrack() {
        return offerTrack;
    }

    public List<String> getUpperTribeRow() {
        return upperTribeRow;
    }

    public List<String> getLowerTribeRow() {
        return lowerTribeRow;
    }

    public List<String> getUpperBuildingRow() {
        return upperBuildingRow;
    }

    public List<String> getLowerBuildingRow() {
        return lowerBuildingRow;
    }

    public int getTribeDeckSize() {
        return tribeDeckSize;
    }

    public int getTribeDeckTopCardEra() {
        return tribeDeckTopCardEra;
    }
}
