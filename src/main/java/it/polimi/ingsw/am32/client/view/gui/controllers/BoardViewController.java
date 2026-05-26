package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.model.LightTurnOrderTile;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.client.view.gui.components.OfferView;
import it.polimi.ingsw.am32.client.view.gui.components.TotemView;
import it.polimi.ingsw.am32.client.view.gui.utils.AnimationUtils;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import it.polimi.ingsw.am32.dto.setup.OfferTileDTO;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardViewController {

    private static final double TURN_TILE_HEIGHT    = 120.0;
    private static final double TURN_SLOT_SIZE      = 20.0;
    private static final double TURN_SLOT_MIN_GAP   = 2.0;

    private final ClientController clientController;

    @FXML private HBox   upperRowCardsContainer;
    @FXML private HBox   lowerRowCardsContainer;
    @FXML private VBox   turnOrderContainer;
    @FXML private HBox   offersContainer;
    @FXML private Button confirmActionButton;

    private final List<String>          selectedCharacterIds   = new ArrayList<>();
    private final List<String>          selectedBuildingIds    = new ArrayList<>();
    private final Map<String, CardView> availableCardViewCache = new HashMap<>();
    private final Map<String, OfferView> offerViewCache        = new HashMap<>();

    private ScaleTransition activePulse;

    public BoardViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        clearContainers();
    }

    public void wipeAndRebuild(LightGameState state) {
        if (state == null) return;
        renderBoardRows(state);
        renderOfferTiles(state);
        renderTurnOrder(state);
        updateConfirmButtonState(state);
    }

    public void clearContainers() {
        upperRowCardsContainer.getChildren().clear();
        lowerRowCardsContainer.getChildren().clear();
        turnOrderContainer.getChildren().clear();
        offersContainer.getChildren().clear();

        stopActivePulse();
        availableCardViewCache.clear();
        offerViewCache.clear();
        selectedCharacterIds.clear();
        selectedBuildingIds.clear();

        if (confirmActionButton != null) {
            confirmActionButton.setVisible(false);
            confirmActionButton.setManaged(false);
            confirmActionButton.setDisable(true);
        }
    }

    public void resetPhaseSelection(GamePhase currentPhase) {
        if (currentPhase == GamePhase.RESOLVE_ACTIONS) return;

        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) {
            confirmActionButton.setVisible(false);
            confirmActionButton.setManaged(false);
            confirmActionButton.setDisable(true);
        }
        availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));
    }

    private void renderBoardRows(LightGameState state) {
        List<String> upperIds = collectRowIds(state.getUpperTribeRow(), state.getUpperBuildingRow());
        List<String> lowerIds = collectRowIds(state.getLowerTribeRow(), state.getLowerBuildingRow());

        List<String> allIds = new ArrayList<>(upperIds);
        allIds.addAll(lowerIds);

        availableCardViewCache.keySet().removeIf(id -> !allIds.contains(id));

        for (String cardId : allIds) {
            if (!availableCardViewCache.containsKey(cardId)) {
                CardView cardView = new CardView(cardId, 80.0, 120.0);
                cardView.setFluid(true);
                cardView.setOnMouseClicked(e -> handleCardSelection(cardView));
                availableCardViewCache.put(cardId, cardView);
            }
        }

        upperRowCardsContainer.getChildren().setAll(
                upperIds.stream().map(availableCardViewCache::get).collect(Collectors.toList())
        );
        lowerRowCardsContainer.getChildren().setAll(
                lowerIds.stream().map(availableCardViewCache::get).collect(Collectors.toList())
        );
    }

    private void renderTurnOrder(LightGameState state) {
        LightTurnOrderTile turnOrderTile = state.getTurnOrderTile();
        if (turnOrderTile == null) return;

        stopActivePulse();

        StackPane compositeTile = buildTurnOrderTile(turnOrderTile, state);

        turnOrderContainer.getChildren().setAll(compositeTile);
    }

    private StackPane buildTurnOrderTile(LightTurnOrderTile tile, LightGameState state) {
        StackPane composite = new StackPane();
        composite.setAlignment(Pos.TOP_CENTER);

        ImageView background = buildTurnOrderBackground(tile.getTileId());
        if (background != null) {
            composite.getChildren().add(background);
        }

        composite.getChildren().add(buildSlotsBox(tile, state));
        return composite;
    }

    private ImageView buildTurnOrderBackground(String tileId) {
        var dto = AssetManager.getInstance().getTurnOrderTile(tileId);
        if (dto == null || dto.getAssetPath() == null) return null;

        var image = ImageCache.getInstance().getImage(dto.getAssetPath());
        if (image == null) return null;

        ImageView view = new ImageView(image);
        view.setFitHeight(TURN_TILE_HEIGHT);
        view.setPreserveRatio(true);
        return view;
    }

    private VBox buildSlotsBox(LightTurnOrderTile tile, LightGameState state) {
        List<String> players   = tile.getPlayers();
        String activePlayer    = state.getCurrentPlayer();

        int totalPlayers = state.getPlayers().size();

        double topPad;
        double reservedSpace;

        switch (totalPlayers) {
            case 2:
                topPad = 25.0;
                reservedSpace = 80.0;
                break;
            case 3:
                topPad = 20.0;
                reservedSpace = 75.0;
                break;
            case 4:
                topPad = 15.0;
                reservedSpace = 70.0;
                break;
            case 5:
                topPad = 5.0;
                reservedSpace = 80.0;
                break;
            default:
                topPad = 20.0;
                reservedSpace = 80.0;
                break;
        }

        double available = TURN_TILE_HEIGHT - reservedSpace;
        double spacing   = (available - (totalPlayers * TURN_SLOT_SIZE)) / (totalPlayers + 1);

        VBox slotsBox = new VBox(Math.max(spacing, TURN_SLOT_MIN_GAP));
        slotsBox.setAlignment(Pos.TOP_CENTER);
        slotsBox.setPadding(new Insets(topPad, 0, 0, 0));


        int missingPlayers = totalPlayers - players.size();
        boolean isPlacementPhase = (state.getCurrentPhase() == GamePhase.PLACE_TOTEMS);


        if (isPlacementPhase) {
            for (int i = 0; i < missingPlayers; i++) {
                javafx.scene.layout.Region topGhost = new javafx.scene.layout.Region();
                topGhost.setPrefSize(30.0, TURN_SLOT_SIZE);
                topGhost.setMaxSize(30.0, TURN_SLOT_SIZE);
                slotsBox.getChildren().add(topGhost);
            }
        }

        for (String nickname : players) {
            LightPlayer player = null;
            if (nickname != null && !nickname.isEmpty()) {
                player = state.getPlayers().get(nickname);
            }

            if (player == null) {

                javafx.scene.layout.Region emptySpace = new javafx.scene.layout.Region();
                emptySpace.setPrefSize(30.0, TURN_SLOT_SIZE);
                emptySpace.setMaxSize(30.0, TURN_SLOT_SIZE);
                slotsBox.getChildren().add(emptySpace);
            } else {
                TotemView totem = new TotemView(player.getTotemColor(), nickname, true);
                javafx.scene.control.Tooltip.install(totem, buildTooltip(nickname));

                if (nickname.equals(activePlayer)) {
                    totem.getStyleClass().add("turn-slot-active");
                    activePulse = AnimationUtils.playPulse(totem);
                }

                slotsBox.getChildren().add(totem);
            }
        }


        if (!isPlacementPhase) {
            for (int i = 0; i < missingPlayers; i++) {
                javafx.scene.layout.Region bottomGhost = new javafx.scene.layout.Region();
                bottomGhost.setPrefSize(30.0, TURN_SLOT_SIZE);
                bottomGhost.setMaxSize(30.0, TURN_SLOT_SIZE);
                slotsBox.getChildren().add(bottomGhost);
            }
        }

        return slotsBox;
    }

    private void renderOfferTiles(LightGameState state) {
        List<LightOfferTile> tiles = state.getOfferTrack();
        Set<String> currentTileIds = tiles.stream()
                .map(LightOfferTile::getId)
                .collect(Collectors.toSet());

        offerViewCache.keySet().removeIf(id -> !currentTileIds.contains(id));

        for (LightOfferTile tile : tiles) {
            String tileId         = tile.getId();
            String occupyingPlayer = tile.getOccupyingPlayer();

            var color = (occupyingPlayer != null && state.getPlayers().containsKey(occupyingPlayer))
                    ? state.getPlayers().get(occupyingPlayer).getTotemColor()
                    : null;

            if (offerViewCache.containsKey(tileId)) {
                offerViewCache.get(tileId).updateTile(tile, color);
            } else {
                OfferView offerView = new OfferView(tile, color);
                offerView.setOnMouseClicked(event -> handleTotemPlacement(offerView));
                offerViewCache.put(tileId, offerView);
            }
        }

        List<OfferView> orderedViews = tiles.stream()
                .map(t -> offerViewCache.get(t.getId()))
                .collect(Collectors.toList());

        offersContainer.getChildren().setAll(orderedViews);
    }

    private void handleCardSelection(CardView selectedCard) {
        LightGameState state = clientController.getLightGameState();

        if (!isMyTurnAndPhase(state, GamePhase.RESOLVE_ACTIONS)) {
            AnimationUtils.shakeNode(selectedCard);
            GuiLogger.info("[BoardViewController] card selection rejected: not active player or wrong phase");
            return;
        }

        String cardId = selectedCard.getCardId();
        if (cardId == null || cardId.startsWith("event")) return;

        OfferRequirements requirements = getCurrentOfferRequirements(state);
        if (requirements == null) {
            AnimationUtils.shakeNode(selectedCard);
            GuiLogger.warning("[BoardViewController] card selection rejected: no offer tile found for local player");
            return;
        }

        List<String> targetList = cardId.startsWith("building") ? selectedBuildingIds : selectedCharacterIds;
        LightPlayer player = state.getPlayers().get(state.getMyNickname());

        if (targetList.contains(cardId)) {
            targetList.remove(cardId);
            selectedCard.setHighlight(false);
        } else {
            if (!canSelectCard(cardId, state, requirements)) {
                AnimationUtils.shakeNode(selectedCard);
                GuiLogger.info("[BoardViewController] Card selection rejected: limit reached.");
                return;
            }
            if (!player.canAffordAdditionalBuilding(selectedBuildingIds, cardId)) {
                AnimationUtils.shakeNode(selectedCard);
                GuiLogger.info("[BoardViewController] Card selection rejected: not enough food.");
                return;
            }

            targetList.add(cardId);
            selectedCard.setHighlight(true);
        }

        updateConfirmButtonState(state);
    }

    @FXML
    private void handleConfirmAction() {
        LightGameState state = clientController.getLightGameState();
        if (!isMyTurnAndPhase(state, GamePhase.RESOLVE_ACTIONS)) {
            if (confirmActionButton != null) AnimationUtils.shakeNode(confirmActionButton);
            return;
        }

        OfferRequirements requirements = getCurrentOfferRequirements(state);
        if (requirements == null || !isSelectionComplete(state, requirements)) {
            if (confirmActionButton != null) AnimationUtils.shakeNode(confirmActionButton);
            return;
        }

        clientController.requestResolveAction(
                new ArrayList<>(selectedCharacterIds),
                new ArrayList<>(selectedBuildingIds)
        );

        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) {
            confirmActionButton.setVisible(false);
            confirmActionButton.setManaged(false);
            confirmActionButton.setDisable(true);
        }
        availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));
    }

    private void handleTotemPlacement(OfferView selectedTile) {
        if (!isMyTurnAndPhase(clientController.getLightGameState(), GamePhase.PLACE_TOTEMS)) {
            AnimationUtils.shakeNode(selectedTile);
            GuiLogger.info("[BoardViewController] totem placement rejected: not active player or wrong phase");
            return;
        }

        clientController.requestPlaceTotem(selectedTile.getTileData().getId());
    }

    private boolean isMyTurnAndPhase(LightGameState state, GamePhase expectedPhase) {
        if (state == null || state.getCurrentPlayer() == null) return false;
        return state.getMyNickname().equals(state.getCurrentPlayer())
                && state.getCurrentPhase() == expectedPhase;
    }

    private void updateConfirmButtonState(LightGameState state) {
        if (confirmActionButton == null) return;

        boolean visible = isMyTurnAndPhase(state, it.polimi.ingsw.am32.model.enums.GamePhase.RESOLVE_ACTIONS);
        confirmActionButton.setVisible(visible);
        confirmActionButton.setManaged(visible);

        if (!visible) {
            confirmActionButton.setDisable(true);
            return;
        }

        OfferRequirements requirements = getCurrentOfferRequirements(state);
        boolean complete = requirements != null && isSelectionComplete(state, requirements);

        if (!complete) {
            confirmActionButton.setText("Confirm Selection");
            confirmActionButton.setDisable(true);
        } else {
            int selectedUpper = countSelectedUpperCards(state);
            int selectedLower = countSelectedLowerCards(state);

            if (requirements.totalCards() == 0) {
                confirmActionButton.setText("Resolve Action");
            } else if (selectedUpper < requirements.upperCards() || selectedLower < requirements.lowerCards()) {
                confirmActionButton.setText("Force Confirm (Resources Depleted)");
            } else {
                confirmActionButton.setText("Confirm Selection");
            }
            confirmActionButton.setDisable(false);
        }
    }

    private boolean canSelectCard(String cardId, LightGameState state, OfferRequirements requirements) {
        if (isUpperCard(cardId, state)) {
            return countSelectedUpperCards(state) < requirements.upperCards();
        }
        if (isLowerCard(cardId, state)) {
            return countSelectedLowerCards(state) < requirements.lowerCards();
        }
        return false;
    }

    private boolean isSelectionComplete(LightGameState state, OfferRequirements requirements) {
        List<String> selUpper = selectedCardIds().stream().filter(id -> isUpperCard(id, state)).toList();
        List<String> selLower = selectedCardIds().stream().filter(id -> isLowerCard(id, state)).toList();

        return state.isSelectionCompleteOrStuck(
                state.getMyNickname(),
                requirements.upperCards(),
                requirements.lowerCards(),
                selUpper,
                selLower,
                selectedBuildingIds
        );
    }

    private OfferRequirements getCurrentOfferRequirements(LightGameState state) {
        if (state == null) return null;

        return state.getOfferTrack().stream()
                .filter(tile -> state.getMyNickname().equals(tile.getOccupyingPlayer()))
                .findFirst()
                .map(tile -> AssetManager.getInstance().getOfferTile(tile.getId()))
                .map(this::toRequirements)
                .orElse(null);
    }

    private OfferRequirements toRequirements(OfferTileDTO tile) {
        if (tile == null) return null;
        return new OfferRequirements(tile.getDrawsFromUpperRow(), tile.getDrawsFromLowerRow());
    }

    private int countSelectedUpperCards(LightGameState state) {
        return (int) selectedCardIds().stream()
                .filter(cardId -> isUpperCard(cardId, state))
                .count();
    }

    private int countSelectedLowerCards(LightGameState state) {
        return (int) selectedCardIds().stream()
                .filter(cardId -> isLowerCard(cardId, state))
                .count();
    }

    private List<String> selectedCardIds() {
        List<String> ids = new ArrayList<>(selectedCharacterIds);
        ids.addAll(selectedBuildingIds);
        return ids;
    }

    private boolean isUpperCard(String cardId, LightGameState state) {
        return state.getUpperTribeRow().contains(cardId) || state.getUpperBuildingRow().contains(cardId);
    }

    private boolean isLowerCard(String cardId, LightGameState state) {
        return state.getLowerTribeRow().contains(cardId) || state.getLowerBuildingRow().contains(cardId);
    }

    private static List<String> collectRowIds(List<String> half1, List<String> half2) {
        List<String> result = new ArrayList<>();
        if (half1 != null) result.addAll(half1);
        if (half2 != null) result.addAll(half2);
        return result;
    }

    private static Tooltip buildTooltip(String text) {
        Tooltip t = new Tooltip(text);
        t.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return t;
    }

    private void stopActivePulse() {
        if (activePulse != null) {
            activePulse.stop();
            activePulse = null;
        }
    }

    private record OfferRequirements(int upperCards, int lowerCards) {
        int totalCards() {
            return upperCards + lowerCards;
        }
    }
}