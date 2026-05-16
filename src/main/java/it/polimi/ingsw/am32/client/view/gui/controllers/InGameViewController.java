package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.model.LightTurnOrderTile;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.client.view.gui.components.OpponentView;
import it.polimi.ingsw.am32.client.view.gui.components.OfferView;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

public class InGameViewController implements ViewController {

    private final ClientController clientController;

    // --- FXML UI Components ---
    @FXML private Label eraLabel;
    @FXML private Label roundLabel;
    @FXML private Label phaseLabel;

    @FXML private VBox opponentsContainer;
    @FXML private HBox offersContainer;

    @FXML private HBox upperRowCardsContainer;
    @FXML private HBox lowerRowCardsContainer;

    @FXML private HBox myCardsContainer;

    @FXML private Label notificationLabel;
    @FXML private Label myFoodLabel;
    @FXML private Label myPrestigeLabel;

    @FXML private VBox turnOrderContainer;
    @FXML private Button confirmActionButton;

    // --- Local Selection State ---
    private final List<String> selectedCharacterIds = new ArrayList<>();
    private final List<String> selectedBuildingIds = new ArrayList<>();

    // --- UI Components Cache  ---
    private final Map<String, OpponentView> opponentViewsCache = new HashMap<>();
    private final Map<String, OfferView> offerViewCache = new HashMap<>();
    private final Map<String, CardView> availableCardViewCache = new HashMap<>();
    private final Map<String, CardView> myCardsCache = new HashMap<>();

    private static final double CASCADE_SPACING = -150.0;
    private static final String DECK_INFO_NODE_ID = "deck-info";

    public InGameViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        clearAllContainers();
    }

    public void updateStatusBar(int era, int round, String phase) {
        eraLabel.setText(String.valueOf(era));
        roundLabel.setText(String.valueOf(round));
        phaseLabel.setText(phase.replace("_", " ").toUpperCase());
    }

    public void fullRender(LightGameState state) {
        updateStatusBar(state.getCurrentEra(), state.getCurrentRound(), state.getCurrentPhase().name());

        String myNick = state.getMyNickname();
        renderOpponents(state.getPlayers(), myNick);

        renderBoardRows(state);

        renderOfferTiles(state.getOfferTrack());
        renderMyBoard(state.getPlayers().get(myNick));
        renderTurnOrder(state.getTurnOrderTile());
        renderDeckInfo(state.getTribeDeckSize(), state.getTribeDeckTopCardEra());
    }

    @Override
    public void onGamePhaseUpdated(GamePhaseChangedEvent event) {
        System.out.println("🚨 [GUI DEBUG] Il Server mi ha detto di cambiare fase in: " + event.getPhase()); // <- AGGIUNGI QUESTA!
        LightGameState state = clientController.getLightGameState();

        if (event.getPhase() != GamePhase.RESOLVE_ACTIONS) {
            selectedCharacterIds.clear();
            selectedBuildingIds.clear();
            if (confirmActionButton != null) confirmActionButton.setVisible(false);
        }

        fullRender(state);
    }

    private void renderBoardRows(LightGameState state) {
        List<String> upperIds = new ArrayList<>();
        if(state.getUpperTribeRow() != null) upperIds.addAll(state.getUpperTribeRow());
        if(state.getUpperBuildingRow() != null) upperIds.addAll(state.getUpperBuildingRow());

        List<String> lowerIds = new ArrayList<>();
        if(state.getLowerTribeRow() != null) lowerIds.addAll(state.getLowerTribeRow());
        if(state.getLowerBuildingRow() != null) lowerIds.addAll(state.getLowerBuildingRow());

        List<String> allIds = new ArrayList<>(upperIds);
        allIds.addAll(lowerIds);

        availableCardViewCache.keySet().removeIf(id -> !allIds.contains(id));

        for (String cardId : allIds) {
            if (!availableCardViewCache.containsKey(cardId)) {
                CardView cardView = new CardView(cardId);
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

    private void renderMyBoard(LightPlayer localPlayer) {
        if (localPlayer == null) return;

        if (myFoodLabel != null) myFoodLabel.setText(String.valueOf(localPlayer.getFood()));
        if (myPrestigeLabel != null) myPrestigeLabel.setText(String.valueOf(localPlayer.getPrestige()));

        List<String> allMyCards = new ArrayList<>();
        allMyCards.addAll(localPlayer.getCharacterCards());
        allMyCards.addAll(localPlayer.getBuildingCards());
        Set<String> currentIds = new HashSet<>(allMyCards);

        myCardsCache.keySet().removeIf(id -> !currentIds.contains(id));

        for (String cardId : allMyCards) {
            if (!myCardsCache.containsKey(cardId)) {
                CardView cardView = new CardView(cardId);
                myCardsCache.put(cardId, cardView);
            }
        }

        myCardsContainer.getChildren().clear();

        Map<String, List<CardView>> groupedCards = new HashMap<>();
        for (String cardId : allMyCards) {
            String category = getCardCategory(cardId);
            groupedCards.computeIfAbsent(category, k -> new ArrayList<>()).add(myCardsCache.get(cardId));
        }

        for (Map.Entry<String, List<CardView>> entry : groupedCards.entrySet()) {
            VBox column = new VBox();
            column.setAlignment(Pos.TOP_CENTER);
            column.setSpacing(CASCADE_SPACING);
            column.getChildren().addAll(entry.getValue());

            myCardsContainer.getChildren().add(column);
        }
    }

    private String getCardCategory(String cardId) {
        if (cardId.startsWith("building")) {
            return "Building";
        }

        CharacterCardDTO dto = AssetManager.getInstance().getCharacterCard(cardId);
        if (dto == null) {
            System.err.println("[InGameViewController] Unknown card id: " + cardId);
            return "Generic";
        }

        return dto.getCharacterType();
    }

    private void renderOpponents(Map<String, LightPlayer> players, String localPlayerName) {
        opponentViewsCache.keySet().removeIf(nick -> !players.containsKey(nick));

        List<OpponentView> updatedViews = new ArrayList<>();

        players.forEach((nickname, player) -> {
            if (!nickname.equals(localPlayerName)) {
                OpponentView view = opponentViewsCache.computeIfAbsent(nickname, k -> new OpponentView(player));
                view.updateValues(player); // Aggiorna i dati
                updatedViews.add(view);
            }
        });

        opponentsContainer.getChildren().setAll(updatedViews);
    }

    private void renderOfferTiles(List<LightOfferTile> tiles) {
        Set<String> currentTileIds = tiles.stream().map(LightOfferTile::getId).collect(Collectors.toSet());
        offerViewCache.keySet().removeIf(id -> !currentTileIds.contains(id));
        for (LightOfferTile tile : tiles) {
            String tileId = tile.getId();
            if (offerViewCache.containsKey(tileId)) {
                offerViewCache.get(tileId).updateTile(tile);
            } else {
                OfferView offerView = new OfferView(tile);
                offerView.setOnMouseClicked(event -> handleTotemPlacement(offerView));
                offerViewCache.put(tileId, offerView);
            }
        }
        List<OfferView> orderedViews = tiles.stream().map(tile -> offerViewCache.get(tile.getId())).collect(Collectors.toList());
        offersContainer.getChildren().setAll(orderedViews);
    }

    private void handleCardSelection(CardView selectedCard) {
        if (!isMyTurnAndPhase(GamePhase.RESOLVE_ACTIONS)) {
            showNotification("⚠️ It is not your turn to take cards!");
            return;
        }

        selectedCard.getCardId().ifPresent(cardId -> {
            // block clicks on event cards (they resolve automatically)
            if (cardId.startsWith("event")) {
                showNotification("⚠️ Event cards resolve automatically!");
                return;
            }

            // sort correctly using "building" as the discriminant
            List<String> targetList;
            if (cardId.startsWith("building")) {
                targetList = selectedBuildingIds;
            } else {
                // Everything else (e.g., char_15, character_15) goes to characters
                targetList = selectedCharacterIds;
            }

            // multiple selection logic (without clearing previously selected cards!)
            if (targetList.contains(cardId)) {
                targetList.remove(cardId); // Deselect if clicked again
                selectedCard.setHighlight(false);
            } else {
                targetList.add(cardId);    // Add to the selected list
                selectedCard.setHighlight(true);
            }

            // show the confirm button if there is at least one card selected
            boolean hasSelection = !selectedCharacterIds.isEmpty() || !selectedBuildingIds.isEmpty();
            if (confirmActionButton != null) confirmActionButton.setVisible(hasSelection);
        });
    }

    @FXML
    private void handleConfirmAction() {
        clientController.requestResolveAction(new ArrayList<>(selectedCharacterIds), new ArrayList<>(selectedBuildingIds));
        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) confirmActionButton.setVisible(false);
        availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));
    }

    private void handleTotemPlacement(OfferView selectedTile) {
        if (!isMyTurnAndPhase(GamePhase.PLACE_TOTEMS)) return;

        String tileId = selectedTile.getTileData().getId();
        clientController.requestPlaceTotem(tileId);
    }

    private boolean isMyTurnAndPhase(GamePhase expectedPhase) {
        LightGameState state = clientController.getLightGameState();
        if (state == null || state.getCurrentPlayer() == null) return false;
        return state.getMyNickname().equals(state.getCurrentPlayer())
                && state.getCurrentPhase() == expectedPhase;
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

        turnOrderContainer.getChildren().setAll(newNodes);
    }

    private void renderDeckInfo(int deckSize, int topCardEra) {
        Label deckLabel = new Label("Tribe Deck: " + deckSize + " cards (Era " + topCardEra + ")");
        deckLabel.setId(DECK_INFO_NODE_ID);
        deckLabel.getStyleClass().add("subtitle-text");
    }

    private void clearAllContainers() {
        opponentsContainer.getChildren().clear();
        opponentViewsCache.clear();

        if (notificationLabel != null) notificationLabel.setVisible(false);
        if (myFoodLabel != null) myFoodLabel.setText("0");
        if (myPrestigeLabel != null) myPrestigeLabel.setText("0");

        offersContainer.getChildren().clear();
        offerViewCache.clear();
        myCardsContainer.getChildren().clear();
        myCardsCache.clear();
        upperRowCardsContainer.getChildren().clear(); // AGGIORNATO
        lowerRowCardsContainer.getChildren().clear(); // AGGIORNATO
        availableCardViewCache.clear();
        turnOrderContainer.getChildren().clear();

        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) confirmActionButton.setVisible(false);
    }

    @Override public void onGameStarted(GameStartedEvent event) { fullRender(clientController.getLightGameState()); }
    @Override public void onGameStateRestored(GameStateEvent event) { fullRender(clientController.getLightGameState()); }

    @Override
    public void onTotemPlaced(TotemPlacedEvent event) {
        LightGameState state = clientController.getLightGameState();
        renderOfferTiles(state.getOfferTrack());
        renderTurnOrder(state.getTurnOrderTile());
    }

    @Override
    public void onPlayerDisconnected(PlayerDisconnectedEvent event) {
        OpponentView view = opponentViewsCache.get(event.getPlayer());
        if (view != null) {
            view.setOpacity(0.4);
        }
        showNotification("⚠️ " + event.getPlayer() + " disconnected.");
    }

    @Override
    public void onPlayerReconnected(PlayerReconnectedEvent event) {
        OpponentView view = opponentViewsCache.get(event.getPlayer());
        if (view != null) {
            view.setOpacity(1.0);
        }
        showNotification("✅ " + event.getPlayer() + " reconnected.");;
    }
    public void showNotification(String message) {
        System.out.println("[GUI] " + message);

        if (notificationLabel != null) {
            notificationLabel.setText("📢 " + message);
            notificationLabel.setVisible(true); // Mostra il banner di errore

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
            pause.setOnFinished(e -> notificationLabel.setVisible(false)); // Nascondi dopo 5 secondi
            pause.play();
        }
    }

    @Override
    public void onActionResolved(ActionResolvedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onError(GameErrorEvent event) {
        showNotification("❌ Azione non valida: " + event.getErrorMessage());
    }

    @Override
    public void onEventResolved(EventResolvedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onExtraAcquisition(ExtraAcquisitionResolvedEvent event) {
        fullRender(clientController.getLightGameState());
    }

}
