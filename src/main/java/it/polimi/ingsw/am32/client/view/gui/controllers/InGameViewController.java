package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.client.view.gui.components.OpponentView;
import it.polimi.ingsw.am32.dto.messages.game.events.GamePhaseChangedEvent;
import it.polimi.ingsw.am32.dto.messages.game.events.GameStartedEvent;
import it.polimi.ingsw.am32.dto.messages.game.events.GameStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.Optional;

/**
 * The main controller for the In-Game view.
 * It manages the synchronization between the LightGameState and the visual components.
 * Adheres to SOLID principles by delegating specific rendering logic to Custom Nodes.
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

    private final java.util.Map<String, OpponentView> opponentViewsCache = new java.util.HashMap<>();
    private final java.util.Map<String, it.polimi.ingsw.am32.client.view.gui.components.OfferView> offerViewCache = new java.util.HashMap<>();
    private it.polimi.ingsw.am32.client.view.gui.components.PlayerStatsView myStatsViewCache;

    private int renderedCharacterCardCount = 0;
    private int renderedBuildingCardCount = 0;

    /**
     * Constructor injected by SceneManager.
     * @param clientController The logic controller of the client.
     */
    public InGameViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        clearAllContainers();
    }

    /**
     * Updates the status bar with Era, Round, and Phase information.
     */
    public void updateStatusBar(int era, int round, String phase) {
        eraLabel.setText(String.valueOf(era));
        roundLabel.setText(String.valueOf(round));
        phaseLabel.setText(phase.replace("_", " ").toUpperCase());
    }

    /**
     * Fully refreshes the UI based on the entire Game State.
     */
    public void fullRender(LightGameState state) {
        updateStatusBar(state.getCurrentEra(), state.getCurrentRound(), state.getCurrentPhase().name());

        String myNick = state.getMyNickname();
        renderOpponents(state.getPlayers(), myNick);

        // Combine all available cards from the board rows into a single list
        java.util.List<String> allAvailableCards = new java.util.ArrayList<>();
        allAvailableCards.addAll(state.getUpperTribeRow());
        allAvailableCards.addAll(state.getLowerTribeRow());
        allAvailableCards.addAll(state.getUpperBuildingRow());
        allAvailableCards.addAll(state.getLowerBuildingRow());

        renderAvailableCards(allAvailableCards);
        renderOfferTiles(state.getOfferTrack());
        renderMyBoard(state.getPlayers().get(myNick));
    }

    // --- Private Rendering Logic ---

    private void renderOpponents(Map<String, LightPlayer> players, String localPlayerName) {
        players.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(localPlayerName))
                .forEach(entry -> {
                    String nickname = entry.getKey();
                    LightPlayer player = entry.getValue();

                    if (opponentViewsCache.containsKey(nickname)) {
                        // exists in cache: update values without re-creating the node again
                        opponentViewsCache.get(nickname).updateValues(player);
                    } else {
                        // not in cache: create new node and add to container and cache
                        OpponentView newView = new OpponentView(player);
                        opponentViewsCache.put(nickname, newView);
                        opponentsContainer.getChildren().add(newView);
                    }
                });
    }

    private void renderAvailableCards(java.util.List<String> cardIds) {
        availableCardsContainer.getChildren().clear();
        for (String cardId : cardIds) {
            if (cardId != null) {
                CardView cardView = new CardView(cardId);

                // Add interaction logic for acquiring a card
                cardView.setOnMouseClicked(event -> handleCardSelection(cardView));

                availableCardsContainer.getChildren().add(cardView);
            }
        }
    }

    private void renderOfferTiles(java.util.List<it.polimi.ingsw.am32.client.model.LightOfferTile> tiles) {
        for (it.polimi.ingsw.am32.client.model.LightOfferTile tile : tiles) {
            String tileId = tile.getId();

            if (offerViewCache.containsKey(tileId)) {

                offerViewCache.get(tileId).renderTotemState();
            } else {
                it.polimi.ingsw.am32.client.view.gui.components.OfferView offerView =
                        new it.polimi.ingsw.am32.client.view.gui.components.OfferView(tile);

                offerView.setOnMouseClicked(event -> handleTotemPlacement(offerView));

                offerViewCache.put(tileId, offerView);
                offersContainer.getChildren().add(offerView);
            }
        }
    }

    private void handleTotemPlacement(it.polimi.ingsw.am32.client.view.gui.components.OfferView selectedTile) {
        String tileId = selectedTile.getTileData().getId();
        System.out.println("[GUI] User requested to place totem on tile: " + tileId);

        selectedTile.setDisable(true);

        clientController.requestPlaceTotem(tileId);
    }

    private void renderMyBoard(LightPlayer localPlayer) {
        if (localPlayer == null) {
            return;
        }

        if (myStatsViewCache != null) {
            myStatsViewCache.updateStats(localPlayer);
        } else {
            myStatsViewCache = new it.polimi.ingsw.am32.client.view.gui.components.PlayerStatsView(localPlayer);
            myStatsContainer.getChildren().add(myStatsViewCache);
        }

        java.util.List<String> characterCards = localPlayer.getCharacterCards();
        for (int i = renderedCharacterCardCount; i < characterCards.size(); i++) {
            myCardsContainer.getChildren().add(new CardView(characterCards.get(i)));
        }
        renderedCharacterCardCount = characterCards.size();

        java.util.List<String> buildingCards = localPlayer.getBuildingCards();
        for (int i = renderedBuildingCardCount; i < buildingCards.size(); i++) {
            myCardsContainer.getChildren().add(new CardView(buildingCards.get(i)));
        }
        renderedBuildingCardCount = buildingCards.size();
    }

    /**
     * Handles the click event on an available card.
     * @param selectedCard The CardView component that was clicked.
     */
    private void handleCardSelection(CardView selectedCard) {
        Optional<String> cardId = selectedCard.getCardId();
        System.out.println("[GUI] User clicked on card: " + cardId);

        // Logic to highlight or send selection to network will go here
    }

    private void clearAllContainers() {
        opponentsContainer.getChildren().clear();
        opponentViewsCache.clear();

        myStatsContainer.getChildren().clear();
        myStatsViewCache = null;

        offersContainer.getChildren().clear();
        offerViewCache.clear();

        myCardsContainer.getChildren().clear();
        renderedCharacterCardCount = 0;
        renderedBuildingCardCount = 0;

        availableCardsContainer.getChildren().clear();
        turnOrderContainer.getChildren().clear();
        eventContainer.getChildren().clear();
    }

    public void showNotification(String message) {
        System.out.println("[GAME NOTIFICATION] " + message);
    }


    @Override
    public void onGameStarted(GameStartedEvent event) {
        this.fullRender(clientController.getLightGameState());
        this.showNotification("Game Started! Next player: " + event.getNextPlayer());
    }

    @Override
    public void onGameStateRestored(GameStateEvent event) {
        this.fullRender(clientController.getLightGameState());
    }

    @Override
    public void onGamePhaseUpdated(GamePhaseChangedEvent event) {
        this.fullRender(clientController.getLightGameState());
    }
}
