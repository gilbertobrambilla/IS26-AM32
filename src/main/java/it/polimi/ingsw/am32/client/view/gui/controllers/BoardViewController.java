package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.model.LightTurnOrderTile;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.client.view.gui.components.OfferView;
import it.polimi.ingsw.am32.client.view.gui.utils.AnimationUtils;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

public class BoardViewController {

    private final ClientController clientController;

    @FXML private HBox upperRowCardsContainer;
    @FXML private HBox lowerRowCardsContainer;
    @FXML private VBox turnOrderContainer;
    @FXML private HBox offersContainer;
    @FXML private Button confirmActionButton;

    private final List<String> selectedCharacterIds = new ArrayList<>();
    private final List<String> selectedBuildingIds = new ArrayList<>();

    private final Map<String, CardView> availableCardViewCache = new HashMap<>();
    private final Map<String, OfferView> offerViewCache = new HashMap<>();

    public BoardViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        // internal caches and local selection lists are cleared
        clearContainers();
    }

    public void wipeAndRebuild(LightGameState state) {
        if (state == null) return;

        // dynamic rendering based on incoming state is performed
        renderBoardRows(state);
        renderOfferTiles(state.getOfferTrack());
        renderTurnOrder(state.getTurnOrderTile());
    }

    public void clearContainers() {
        upperRowCardsContainer.getChildren().clear();
        lowerRowCardsContainer.getChildren().clear();
        turnOrderContainer.getChildren().clear();
        offersContainer.getChildren().clear();

        availableCardViewCache.clear();
        offerViewCache.clear();

        selectedCharacterIds.clear();
        selectedBuildingIds.clear();

        if (confirmActionButton != null) {
            confirmActionButton.setVisible(false);
        }
    }

    public void resetPhaseSelection(GamePhase currentPhase) {
        if (currentPhase != GamePhase.RESOLVE_ACTIONS) {
            // selections are discarded if phase is changed by the server
            selectedCharacterIds.clear();
            selectedBuildingIds.clear();
            if (confirmActionButton != null) {
                confirmActionButton.setVisible(false);
            }
            availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));
        }
    }

    private void renderBoardRows(LightGameState state) {
        List<String> upperIds = new ArrayList<>();
        if (state.getUpperTribeRow() != null) upperIds.addAll(state.getUpperTribeRow());
        if (state.getUpperBuildingRow() != null) upperIds.addAll(state.getUpperBuildingRow());

        List<String> lowerIds = new ArrayList<>();
        if (state.getLowerTribeRow() != null) lowerIds.addAll(state.getLowerTribeRow());
        if (state.getLowerBuildingRow() != null) lowerIds.addAll(state.getLowerBuildingRow());

        List<String> allIds = new ArrayList<>(upperIds);
        allIds.addAll(lowerIds);

        // stale visual components are purged from cache
        availableCardViewCache.keySet().removeIf(id -> !allIds.contains(id));

        for (String cardId : allIds) {
            if (!availableCardViewCache.containsKey(cardId)) {
                CardView cardView = new CardView(cardId);
                // click listener is assigned
                cardView.setOnMouseClicked(e -> handleCardSelection(cardView));
                availableCardViewCache.put(cardId, cardView);
            }
        }

        // updated views are injected into containers
        upperRowCardsContainer.getChildren().setAll(
                upperIds.stream().map(availableCardViewCache::get).collect(Collectors.toList())
        );
        lowerRowCardsContainer.getChildren().setAll(
                lowerIds.stream().map(availableCardViewCache::get).collect(Collectors.toList())
        );
    }

    private void renderOfferTiles(List<LightOfferTile> tiles) {
        Set<String> currentTileIds = tiles.stream().map(LightOfferTile::getId).collect(Collectors.toSet());

        // stale tiles are removed
        offerViewCache.keySet().removeIf(id -> !currentTileIds.contains(id));

        for (LightOfferTile tile : tiles) {
            String tileId = tile.getId();
            if (offerViewCache.containsKey(tileId)) {
                // data payload is updated for existing views
                offerViewCache.get(tileId).updateTile(tile);
            } else {
                OfferView offerView = new OfferView(tile);
                // click listener is assigned
                offerView.setOnMouseClicked(event -> handleTotemPlacement(offerView));
                offerViewCache.put(tileId, offerView);
            }
        }

        List<OfferView> orderedViews = tiles.stream()
                .map(tile -> offerViewCache.get(tile.getId()))
                .collect(Collectors.toList());

        // offer track container is repopulated
        offersContainer.getChildren().setAll(orderedViews);
    }

    private void renderTurnOrder(LightTurnOrderTile turnOrderTile) {
        if (turnOrderTile == null) return;

        List<Node> newNodes = new ArrayList<>();

        Label titleLabel = new Label("TURN ORDER");
        titleLabel.getStyleClass().add("subtitle-text");
        newNodes.add(titleLabel);

        List<String> players = turnOrderTile.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Label playerLabel = new Label((i + 1) + ". " + players.get(i));
            if (players.get(i).equals(clientController.getLightGameState().getMyNickname())) {
                playerLabel.getStyleClass().add("turn-order-local-player");
            } else {
                playerLabel.getStyleClass().add("turn-order-opponent");
            }
            newNodes.add(playerLabel);
        }

        // turn order box is repopulated
        turnOrderContainer.getChildren().setAll(newNodes);
    }

    private void handleCardSelection(CardView selectedCard) {
        if (!isMyTurnAndPhase(GamePhase.RESOLVE_ACTIONS)) {
            AnimationUtils.shakeNode(selectedCard);
            // rejection log is generated
            System.out.println("[GUI] card selection rejected: not active player or wrong phase");
            return;
        }

        selectedCard.getCardId().ifPresent(cardId -> {
            if (cardId.startsWith("event")) {
                return;
            }

            List<String> targetList = cardId.startsWith("building") ? selectedBuildingIds : selectedCharacterIds;

            if (targetList.contains(cardId)) {
                // card is deselected
                targetList.remove(cardId);
                selectedCard.setHighlight(false);
            } else {
                // card is selected
                targetList.add(cardId);
                selectedCard.setHighlight(true);
            }

            boolean hasSelection = !selectedCharacterIds.isEmpty() || !selectedBuildingIds.isEmpty();
            if (confirmActionButton != null) {
                // confirm button visibility is toggled
                confirmActionButton.setVisible(hasSelection);
            }
        });
    }

    @FXML
    private void handleConfirmAction() {
        // network request is dispatched
        clientController.requestResolveAction(new ArrayList<>(selectedCharacterIds), new ArrayList<>(selectedBuildingIds));

        // local state is reset
        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) {
            confirmActionButton.setVisible(false);
        }
        availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));
    }

    private void handleTotemPlacement(OfferView selectedTile) {
        if (!isMyTurnAndPhase(GamePhase.PLACE_TOTEMS)) {
            // error animation is triggered on the specific tile
            AnimationUtils.shakeNode(selectedTile);
            System.out.println("[GUI] totem placement rejected: not active player or wrong phase");
            return;
        }

        String tileId = selectedTile.getTileData().getId();
        // network request is dispatched
        clientController.requestPlaceTotem(tileId);
    }

    private boolean isMyTurnAndPhase(GamePhase expectedPhase) {
        LightGameState state = clientController.getLightGameState();
        if (state == null || state.getCurrentPlayer() == null) return false;

        // turn validation is evaluated
        return state.getMyNickname().equals(state.getCurrentPlayer())
                && state.getCurrentPhase() == expectedPhase;
    }
}
