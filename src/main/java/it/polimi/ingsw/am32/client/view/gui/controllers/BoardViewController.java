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
import javafx.scene.Node;
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

/**
 * The central game board controller is represented.
 */
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

    /**
     * Dependency injection is handled through the constructor.
     *
     * @param clientController the main client controller
     */
    public BoardViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    /**
     * Board containers are initialized.
     */
    @FXML
    public void initialize() {
        clearContainers();
    }

    /**
     * The board, offer track, and turn order are rebuilt from the light state.
     *
     * @param state the light game state to render
     */
    public void wipeAndRebuild(LightGameState state) {
        if (state == null) return;
        renderBoardRows(state);
        renderOfferTiles(state);
        renderTurnOrder(state);
        updateConfirmButtonState(state);
    }

    /**
     * Board containers, caches, selections, and confirm controls are reset.
     */
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

    /**
     * Pending card selections are cleared when the current phase does not allow them.
     *
     * @param currentPhase the current game phase
     */
    public void resetPhaseSelection(GamePhase currentPhase) {
        if (currentPhase == GamePhase.RESOLVE_ACTIONS || currentPhase == GamePhase.EXTRA_ACQUISITION) return;

        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) {
            confirmActionButton.setVisible(false);
            confirmActionButton.setManaged(false);
            confirmActionButton.setDisable(true);
        }
        availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));
    }

    /**
     * Upper and lower board card rows are rendered.
     *
     * @param state the light game state to render
     */
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

    /**
     * The current turn order tile is rendered.
     *
     * @param state the light game state to render
     */
    private void renderTurnOrder(LightGameState state) {
        LightTurnOrderTile turnOrderTile = state.getTurnOrderTile();
        if (turnOrderTile == null) return;

        stopActivePulse();

        StackPane compositeTile = buildTurnOrderTile(turnOrderTile, state);

        turnOrderContainer.getChildren().setAll(compositeTile);
    }

    /**
     * A composite turn order tile with a background and slots is built.
     *
     * @param tile the light turn order tile model
     * @param state the light game state
     * @return the composite tile node
     */
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

    /**
     * The background image view for a turn order tile is built.
     *
     * @param tileId the turn order tile identifier
     * @return the background image view, or null if unavailable
     */
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

    /**
     * The totem slots displayed over the turn order tile are built.
     *
     * @param tile the light turn order tile model
     * @param state the light game state
     * @return the slot container
     */
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

    /**
     * Offer tiles and the deck preview are rendered.
     *
     * @param state the light game state to render
     */
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

        // node list is used to host both offer views and the deck view
        List<Node> orderedViews = tiles.stream()
                .map(t -> offerViewCache.get(t.getId()))
                .collect(Collectors.toList());


        StackPane deckView = buildDeckView(state);
        if (deckView != null) {
            orderedViews.add(deckView);
        }

        offersContainer.getChildren().setAll(orderedViews);
    }

    /**
     * The tribe deck preview is built.
     *
     * @param state the light game state
     * @return the deck preview node, or null if no deck is visible
     */
    private StackPane buildDeckView(LightGameState state) {
        int deckSize = state.getTribeDeckSize();
        int topEra = state.getTribeDeckTopCardEra();

        if (deckSize <= 0 || topEra <= 0) return null;

        String imagePath = "/assets/images/card_backs/back_tribe_" + topEra + ".png";

        javafx.scene.image.Image deckImage = ImageCache.getInstance().getImage(imagePath);
        if (deckImage == null) return null;

        ImageView deckImageView = new ImageView(deckImage);
        deckImageView.setFitHeight(120.0);
        deckImageView.setPreserveRatio(true);

        Tooltip tooltip = new Tooltip("Tribe Deck\nCards remaining: " + deckSize + "\nNext Era: " + topEra);
        tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Tooltip.install(deckImageView, tooltip);

        StackPane container = new StackPane(deckImageView);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(0, 0, 0, 40.0));

        return container;
    }

    /**
     * Card click selection is handled according to the current game phase.
     *
     * @param selectedCard the selected card view
     */
    private void handleCardSelection(CardView selectedCard) {
        LightGameState state = clientController.getLightGameState();

        if (!isMyTurnAndPhase(state, GamePhase.RESOLVE_ACTIONS, GamePhase.EXTRA_ACQUISITION)) {
            AnimationUtils.shakeNode(selectedCard);
            GuiLogger.info("[BoardViewController] card selection rejected: not active player or wrong phase");
            return;
        }

        String cardId = selectedCard.getCardId();
        if (cardId == null || cardId.startsWith("event")) return;

        if (state.getCurrentPhase() == GamePhase.EXTRA_ACQUISITION) {
            handleCardSelectionForExtraAcquisition(cardId, selectedCard, state);
        } else {
            handleCardSelectionForResolveActions(cardId, selectedCard, state);
        }

        updateConfirmButtonState(state);
    }

    /**
     * Card selection during extra acquisition is handled.
     *
     * @param cardId the selected card identifier
     * @param selectedCard the selected card view
     * @param state the light game state
     */
    private void handleCardSelectionForExtraAcquisition(String cardId, CardView selectedCard, LightGameState state) {
        if (selectedCharacterIds.contains(cardId) || selectedBuildingIds.contains(cardId)) {
            selectedCharacterIds.remove(cardId);
            selectedBuildingIds.remove(cardId);
            selectedCard.setHighlight(false);
        } else {
            if (!state.isExtraAcquisitionValid(state.getMyNickname(), cardId)) {
                AnimationUtils.shakeNode(selectedCard);
                GuiLogger.info("[BoardViewController] Card selection rejected: invalid for extra acquisition.");
                return;
            }
            selectedCharacterIds.clear();
            selectedBuildingIds.clear();
            availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));

            List<String> targetList = cardId.startsWith("building") ? selectedBuildingIds : selectedCharacterIds;
            targetList.add(cardId);
            selectedCard.setHighlight(true);
        }
    }

    /**
     * Card selection during action resolution is handled.
     *
     * @param cardId the selected card identifier
     * @param selectedCard the selected card view
     * @param state the light game state
     */
    private void handleCardSelectionForResolveActions(String cardId, CardView selectedCard, LightGameState state) {
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
    }

    /**
     * The current selection is confirmed from the board button.
     */
    @FXML
    private void handleConfirmAction() {
        LightGameState state = clientController.getLightGameState();
        if (!isMyTurnAndPhase(state, GamePhase.RESOLVE_ACTIONS, GamePhase.EXTRA_ACQUISITION)) {
            if (confirmActionButton != null) AnimationUtils.shakeNode(confirmActionButton);
            return;
        }

        if (state.getCurrentPhase() == GamePhase.EXTRA_ACQUISITION) {
            handleConfirmForExtraAcquisition();
        } else {
            handleConfirmForResolveActions(state);
        }
    }

    /**
     * Extra acquisition selection is submitted.
     */
    private void handleConfirmForExtraAcquisition() {
        String selectedCard = null;
        if (!selectedCharacterIds.isEmpty()) {
            selectedCard = selectedCharacterIds.get(0);
        } else if (!selectedBuildingIds.isEmpty()) {
            selectedCard = selectedBuildingIds.get(0);
        }
        clientController.requestExtraAcquisition(selectedCard);
        clearSelectionsAndHideConfirmButton();
    }

    /**
     * Action resolution selections are validated and submitted.
     *
     * @param state the light game state
     */
    private void handleConfirmForResolveActions(LightGameState state) {
        OfferRequirements requirements = getCurrentOfferRequirements(state);
        if (requirements == null || !isSelectionComplete(state, requirements)) {
            if (confirmActionButton != null) AnimationUtils.shakeNode(confirmActionButton);
            return;
        }

        clientController.requestResolveAction(
                new ArrayList<>(selectedCharacterIds),
                new ArrayList<>(selectedBuildingIds)
        );
        clearSelectionsAndHideConfirmButton();
    }

    /**
     * Card selections are cleared and the confirmation button is hidden.
     */
    private void clearSelectionsAndHideConfirmButton() {
        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) {
            confirmActionButton.setVisible(false);
            confirmActionButton.setManaged(false);
            confirmActionButton.setDisable(true);
        }
        availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));
    }

    /**
     * Totem placement on an offer tile is requested.
     *
     * @param selectedTile the selected offer tile view
     */
    private void handleTotemPlacement(OfferView selectedTile) {
        if (!isMyTurnAndPhase(clientController.getLightGameState(), GamePhase.PLACE_TOTEMS)) {
            AnimationUtils.shakeNode(selectedTile);
            GuiLogger.info("[BoardViewController] totem placement rejected: not active player or wrong phase");
            return;
        }

        clientController.requestPlaceTotem(selectedTile.getTileData().getId());
    }

    /**
     * Local turn ownership and phase compatibility are checked.
     *
     * @param state the light game state
     * @param expectedPhases the accepted game phases
     * @return true if the local player can act in one of the expected phases
     */
    private boolean isMyTurnAndPhase(LightGameState state, GamePhase... expectedPhases) {
        if (state == null || state.getCurrentPlayer() == null) return false;
        if (!state.getMyNickname().equals(state.getCurrentPlayer())) return false;
        for (GamePhase phase : expectedPhases) {
            if (state.getCurrentPhase() == phase) return true;
        }
        return false;
    }

    /**
     * Confirm button visibility, text, and disabled state are refreshed.
     *
     * @param state the light game state
     */
    private void updateConfirmButtonState(LightGameState state) {
        if (confirmActionButton == null) return;

        boolean visible = isMyTurnAndPhase(state, GamePhase.RESOLVE_ACTIONS, GamePhase.EXTRA_ACQUISITION);
        confirmActionButton.setVisible(visible);
        confirmActionButton.setManaged(visible);

        if (!visible) {
            confirmActionButton.setDisable(true);
            return;
        }

        if (state.getCurrentPhase() == GamePhase.EXTRA_ACQUISITION) {
            updateButtonForExtraAcquisition();
        } else {
            updateButtonForResolveActions(state);
        }
    }

    /**
     * Confirm button state for extra acquisition is refreshed.
     */
    private void updateButtonForExtraAcquisition() {
        confirmActionButton.setDisable(false);
        if (selectedCharacterIds.isEmpty() && selectedBuildingIds.isEmpty()) {
            confirmActionButton.setText("Skip Extra Acquisition");
        } else {
            confirmActionButton.setText("Confirm Extra Acquisition");
        }
    }

    /**
     * Confirm button state for action resolution is refreshed.
     *
     * @param state the light game state
     */
    private void updateButtonForResolveActions(LightGameState state) {
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

    /**
     * Whether the given card can be selected for the current offer is checked.
     *
     * @param cardId the selected card identifier
     * @param state the light game state
     * @param requirements the current offer requirements
     * @return true if the card can be selected, false otherwise
     */
    private boolean canSelectCard(String cardId, LightGameState state, OfferRequirements requirements) {
        if (isUpperCard(cardId, state)) {
            return countSelectedUpperCards(state) < requirements.upperCards();
        }
        if (isLowerCard(cardId, state)) {
            return countSelectedLowerCards(state) < requirements.lowerCards();
        }
        return false;
    }

    /**
     * Whether the current selection satisfies or exhausts the offer requirements is checked.
     *
     * @param state the light game state
     * @param requirements the current offer requirements
     * @return true if the selection can be submitted, false otherwise
     */
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

    /**
     * The offer requirements for the local player's occupied tile are resolved.
     *
     * @param state the light game state
     * @return the current offer requirements, or null if unavailable
     */
    private OfferRequirements getCurrentOfferRequirements(LightGameState state) {
        if (state == null) return null;

        return state.getOfferTrack().stream()
                .filter(tile -> state.getMyNickname().equals(tile.getOccupyingPlayer()))
                .findFirst()
                .map(tile -> AssetManager.getInstance().getOfferTile(tile.getId()))
                .map(this::toRequirements)
                .orElse(null);
    }

    /**
     * Offer tile DTO values are converted to local requirements.
     *
     * @param tile the offer tile dto
     * @return the local offer requirements, or null if unavailable
     */
    private OfferRequirements toRequirements(OfferTileDTO tile) {
        if (tile == null) return null;
        return new OfferRequirements(tile.getDrawsFromUpperRow(), tile.getDrawsFromLowerRow());
    }

    /**
     * Selected cards belonging to the upper row are counted.
     *
     * @param state the light game state
     * @return the number of selected upper-row cards
     */
    private int countSelectedUpperCards(LightGameState state) {
        return (int) selectedCardIds().stream()
                .filter(cardId -> isUpperCard(cardId, state))
                .count();
    }

    /**
     * Selected cards belonging to the lower row are counted.
     *
     * @param state the light game state
     * @return the number of selected lower-row cards
     */
    private int countSelectedLowerCards(LightGameState state) {
        return (int) selectedCardIds().stream()
                .filter(cardId -> isLowerCard(cardId, state))
                .count();
    }

    /**
     * All selected card identifiers are collected.
     *
     * @return the selected card identifiers
     */
    private List<String> selectedCardIds() {
        List<String> ids = new ArrayList<>(selectedCharacterIds);
        ids.addAll(selectedBuildingIds);
        return ids;
    }

    /**
     * Whether a card belongs to the upper row is checked.
     *
     * @param cardId the card identifier
     * @param state the light game state
     * @return true if the card belongs to the upper row, false otherwise
     */
    private boolean isUpperCard(String cardId, LightGameState state) {
        return state.getUpperTribeRow().contains(cardId) || state.getUpperBuildingRow().contains(cardId);
    }

    /**
     * Whether a card belongs to the lower row is checked.
     *
     * @param cardId the card identifier
     * @param state the light game state
     * @return true if the card belongs to the lower row, false otherwise
     */
    private boolean isLowerCard(String cardId, LightGameState state) {
        return state.getLowerTribeRow().contains(cardId) || state.getLowerBuildingRow().contains(cardId);
    }

    /**
     * Two row halves are combined while null values are tolerated.
     *
     * @param half1 the first row half
     * @param half2 the second row half
     * @return the combined card identifiers
     */
    private static List<String> collectRowIds(List<String> half1, List<String> half2) {
        List<String> result = new ArrayList<>();
        if (half1 != null) result.addAll(half1);
        if (half2 != null) result.addAll(half2);
        return result;
    }

    /**
     * A bold tooltip is built.
     *
     * @param text the tooltip text
     * @return the tooltip instance
     */
    private static Tooltip buildTooltip(String text) {
        Tooltip t = new Tooltip(text);
        t.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return t;
    }

    /**
     * The active turn-order pulse animation is stopped.
     */
    private void stopActivePulse() {
        if (activePulse != null) {
            activePulse.stop();
            activePulse = null;
        }
    }

    /**
     * Offer tile card draw requirements are represented.
     *
     * @param upperCards the number of required upper-row cards
     * @param lowerCards the number of required lower-row cards
     */
    private record OfferRequirements(int upperCards, int lowerCards) {
        /**
         * The total number of required cards is returned.
         *
         * @return the total required cards
         */
        int totalCards() {
            return upperCards + lowerCards;
        }
    }
}
