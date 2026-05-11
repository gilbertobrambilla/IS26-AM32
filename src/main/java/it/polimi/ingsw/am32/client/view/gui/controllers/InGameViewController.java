package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.client.view.gui.components.OpponentView;
import it.polimi.ingsw.am32.client.view.gui.components.OfferView;
import it.polimi.ingsw.am32.client.view.gui.components.PlayerStatsView;
import it.polimi.ingsw.am32.dto.messages.game.events.GamePhaseChangedEvent;
import it.polimi.ingsw.am32.dto.messages.game.events.GameStartedEvent;
import it.polimi.ingsw.am32.dto.messages.game.events.GameStateEvent;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The main controller for the In-Game view.
 * Manages the synchronization between LightGameState and UI using a cache-based approach
 * to ensure high performance and zero flickering.
 */
public class InGameViewController implements ViewController {

    private final ClientController clientController;

    // --- FXML UI Components ---
    @FXML private Label eraLabel;
    @FXML private Label roundLabel;
    @FXML private Label phaseLabel;

    @FXML private VBox opponentsContainer;
    @FXML private HBox offersContainer;
    @FXML private HBox availableCardsContainer;
    @FXML private HBox myCardsContainer;
    @FXML private VBox myStatsContainer;
    @FXML private VBox turnOrderContainer;
    @FXML private VBox eventContainer;
    @FXML private Button confirmActionButton;

    // --- Local Selection State ---
    private final List<String> selectedCharacterIds = new ArrayList<>();
    private final List<String> selectedBuildingIds = new ArrayList<>();

    // --- UI Components Cache (Type-Safe) ---
    private final Map<String, OpponentView> opponentViewsCache = new HashMap<>();
    private final Map<String, OfferView> offerViewCache = new HashMap<>();
    private final Map<String, CardView> availableCardViewCache = new HashMap<>();
    private final Map<String, CardView> myCardsCache = new HashMap<>();
    private PlayerStatsView myStatsViewCache;

    public InGameViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        clearAllContainers();
    }

    /**
     * Updates the top status bar with core game metrics.
     */
    public void updateStatusBar(int era, int round, String phase) {
        eraLabel.setText(String.valueOf(era));
        roundLabel.setText(String.valueOf(round));
        phaseLabel.setText(phase.replace("_", " ").toUpperCase());
    }

    /**
     * Performs a full synchronization of the UI with the game state.
     * Delegates specific rendering to cache-aware methods.
     */
    public void fullRender(LightGameState state) {
        updateStatusBar(state.getCurrentEra(), state.getCurrentRound(), state.getCurrentPhase().name());

        String myNick = state.getMyNickname();
        renderOpponents(state.getPlayers(), myNick);
        renderAvailableCards(getAllAvailableCards(state));
        renderOfferTiles(state.getOfferTrack());
        renderMyBoard(state.getPlayers().get(myNick));
        renderTurnOrder(state.getTurnOrderTile());
        renderDeckInfo(state.getTribeDeckSize(), state.getTribeDeckTopCardEra());
    }

    /**
     * Orchestrates UI updates based on game phase transitions.
     * Implements selective rendering to avoid unnecessary DOM operations.
     */
    @Override
    public void onGamePhaseUpdated(GamePhaseChangedEvent event) {
        LightGameState state = clientController.getLightGameState();
        updateStatusBar(state.getCurrentEra(), state.getCurrentRound(), state.getCurrentPhase().name());
        renderDeckInfo(state.getTribeDeckSize(), state.getTribeDeckTopCardEra());

        if (event.getPhase() != GamePhase.RESOLVE_ACTIONS) {
            selectedCharacterIds.clear();
            selectedBuildingIds.clear();
            if (confirmActionButton != null) confirmActionButton.setVisible(false);
        }

        switch (event.getPhase()) {
            case PLACE_TOTEMS:
                renderOfferTiles(state.getOfferTrack());
                renderAvailableCards(getAllAvailableCards(state));
                renderTurnOrder(state.getTurnOrderTile());
                break;
            case RESOLVE_ACTIONS:
                renderOfferTiles(state.getOfferTrack());
                renderAvailableCards(getAllAvailableCards(state));
                break;
            case END_ROUND:
                renderOpponents(state.getPlayers(), state.getMyNickname());
                renderMyBoard(state.getPlayers().get(state.getMyNickname()));
                break;
            default:
                break;
        }
    }

    // --- Rendering Logic (Cache-Based / Diffing) ---

    private void renderOpponents(Map<String, LightPlayer> players, String localPlayerName) {
        players.forEach((nickname, player) -> {
            if (!nickname.equals(localPlayerName)) {
                if (opponentViewsCache.containsKey(nickname)) {
                    opponentViewsCache.get(nickname).updateValues(player);
                } else {
                    OpponentView newView = new OpponentView(player);
                    opponentViewsCache.put(nickname, newView);
                    opponentsContainer.getChildren().add(newView);
                }
            }
        });
    }

    private void renderAvailableCards(List<String> cardIds) {
        Set<String> currentIds = new HashSet<>(cardIds);

        availableCardViewCache.keySet().removeIf(id -> !currentIds.contains(id));

        for (String cardId : cardIds) {
            if (!availableCardViewCache.containsKey(cardId)) {
                CardView cardView = new CardView(cardId);
                cardView.setOnMouseClicked(e -> handleCardSelection(cardView));
                availableCardViewCache.put(cardId, cardView);
            }
        }

        List<CardView> orderedViews = cardIds.stream()
                .map(availableCardViewCache::get)
                .collect(Collectors.toList());

        availableCardsContainer.getChildren().setAll(orderedViews);
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

        List<OfferView> orderedViews = tiles.stream()
                .map(tile -> offerViewCache.get(tile.getId()))
                .collect(Collectors.toList());

        offersContainer.getChildren().setAll(orderedViews);
    }

    private void renderMyBoard(LightPlayer localPlayer) {
        if (localPlayer == null) return;

        if (myStatsViewCache != null) {
            myStatsViewCache.updateStats(localPlayer);
        } else {
            myStatsViewCache = new PlayerStatsView(localPlayer);
            myStatsContainer.getChildren().add(myStatsViewCache);
        }

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

        List<CardView> orderedViews = allMyCards.stream()
                .map(myCardsCache::get)
                .collect(Collectors.toList());

        myCardsContainer.getChildren().setAll(orderedViews);
    }

    // --- Event Handlers ---

    private void handleCardSelection(CardView selectedCard) {
        selectedCard.getCardId().ifPresent(cardId -> {
            boolean isCharacter = cardId.startsWith("character");
            List<String> targetList = isCharacter ? selectedCharacterIds : selectedBuildingIds;

            if (targetList.contains(cardId)) {
                targetList.remove(cardId);
                selectedCard.setHighlight(false);
            } else {
                targetList.add(cardId);
                selectedCard.setHighlight(true);
            }

            boolean hasSelection = !selectedCharacterIds.isEmpty() || !selectedBuildingIds.isEmpty();
            if (confirmActionButton != null) {
                confirmActionButton.setVisible(hasSelection);
            }
        });
    }

    @FXML
    private void handleConfirmAction() {
        clientController.requestResolveAction(
                new ArrayList<>(selectedCharacterIds),
                new ArrayList<>(selectedBuildingIds)
        );

        // Reset local selection state
        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) confirmActionButton.setVisible(false);

        // Clean UI highlights using the typed cache (anti-pattern free)
        availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));
    }

    private void handleTotemPlacement(OfferView selectedTile) {
        String tileId = selectedTile.getTileData().getId();
        selectedTile.setDisable(true); // Prevent double-clicks
        clientController.requestPlaceTotem(tileId);
    }

    // --- Helpers ---

    private List<String> getAllAvailableCards(LightGameState state) {
        List<String> cards = new ArrayList<>();
        cards.addAll(state.getUpperTribeRow());
        cards.addAll(state.getLowerTribeRow());
        cards.addAll(state.getUpperBuildingRow());
        cards.addAll(state.getLowerBuildingRow());
        return cards.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void renderTurnOrder(it.polimi.ingsw.am32.client.model.LightTurnOrderTile turnOrderTile) {
        turnOrderContainer.getChildren().clear();

        if (turnOrderTile == null) return;

        Label titleLabel = new Label("TURN ORDER");
        titleLabel.getStyleClass().add("subtitle-text");
        turnOrderContainer.getChildren().add(titleLabel);

        List<String> players = turnOrderTile.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Label playerLabel = new Label((i + 1) + ". " + players.get(i));

            if (players.get(i).equals(clientController.getLightGameState().getMyNickname())) {
                playerLabel.getStyleClass().add("turn-order-local-player");
            } else {
                playerLabel.getStyleClass().add("turn-order-opponent");
            }

            turnOrderContainer.getChildren().add(playerLabel);
        }
    }

    private void renderDeckInfo(int deckSize, int topCardEra) {
        if (eventContainer == null) return;

        eventContainer.getChildren().removeIf(node -> "deck-info".equals(node.getId()));

        Label deckLabel = new Label("Tribe Deck: " + deckSize + " cards (Era " + topCardEra + ")");
        deckLabel.setId("deck-info");

        deckLabel.getStyleClass().add("subtitle-text");

        eventContainer.getChildren().add(deckLabel);
    }

    private void clearAllContainers() {
        opponentsContainer.getChildren().clear();
        opponentViewsCache.clear();
        myStatsContainer.getChildren().clear();
        myStatsViewCache = null;
        offersContainer.getChildren().clear();
        offerViewCache.clear();
        myCardsContainer.getChildren().clear();
        myCardsCache.clear();
        availableCardsContainer.getChildren().clear();
        availableCardViewCache.clear();
        turnOrderContainer.getChildren().clear();
        eventContainer.getChildren().clear();

        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) confirmActionButton.setVisible(false);
    }

    @Override public void onGameStarted(GameStartedEvent event) { fullRender(clientController.getLightGameState()); }
    @Override public void onGameStateRestored(GameStateEvent event) { fullRender(clientController.getLightGameState()); }

    public void showNotification(String message) {
        System.out.println("[GUI] " + message);

        if (eventContainer == null) return;

        Label notifLabel = new Label("📢 " + message);
        notifLabel.setWrapText(true);

        notifLabel.getStyleClass().add("game-notification");

        eventContainer.getChildren().add(0, notifLabel);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
        pause.setOnFinished(e -> eventContainer.getChildren().remove(notifLabel));
        pause.play();
    }
}
