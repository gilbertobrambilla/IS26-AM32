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
import javafx.geometry.Pos;
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

    // NUOVI CONTENITORI AL POSTO DI availableCardsContainer
    @FXML private HBox upperRowCardsContainer;
    @FXML private HBox lowerRowCardsContainer;

    @FXML private HBox myCardsContainer;

    // Rimuoviamo myStatsContainer e usiamo le Label dirette (o puoi mantenere il tuo PlayerStatsView, ho adattato il codice per supportare la tua classe)
    @FXML private VBox myStatsContainer;

    @FXML private VBox turnOrderContainer;
    @FXML private VBox eventContainer;
    @FXML private Button confirmActionButton;

    // --- Local Selection State ---
    private final List<String> selectedCharacterIds = new ArrayList<>();
    private final List<String> selectedBuildingIds = new ArrayList<>();

    // --- UI Components Cache (MANTENUTA PERCHÈ È OTTIMA) ---
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

    public void updateStatusBar(int era, int round, String phase) {
        eraLabel.setText(String.valueOf(era));
        roundLabel.setText(String.valueOf(round));
        phaseLabel.setText(phase.replace("_", " ").toUpperCase());
    }

    public void fullRender(LightGameState state) {
        updateStatusBar(state.getCurrentEra(), state.getCurrentRound(), state.getCurrentPhase().name());

        String myNick = state.getMyNickname();
        renderOpponents(state.getPlayers(), myNick);

        // Nuova logica separata per le righe
        renderBoardRows(state);

        renderOfferTiles(state.getOfferTrack());
        renderMyBoard(state.getPlayers().get(myNick));
        renderTurnOrder(state.getTurnOrderTile());
        renderDeckInfo(state.getTribeDeckSize(), state.getTribeDeckTopCardEra());
    }

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
            case RESOLVE_ACTIONS:
                renderOfferTiles(state.getOfferTrack());
                renderBoardRows(state);
                renderTurnOrder(state.getTurnOrderTile());
                break;
            case END_ROUND:
                renderOpponents(state.getPlayers(), state.getMyNickname());
                renderMyBoard(state.getPlayers().get(state.getMyNickname()));
                break;
            default:
                break;
        }
    }

    // --- NUOVA LOGICA: RIGHE SUPERIORI E INFERIORI ---
    private void renderBoardRows(LightGameState state) {
        // Raccogli tutti gli ID per gestire la cache
        List<String> upperIds = new ArrayList<>();
        if(state.getUpperTribeRow() != null) upperIds.addAll(state.getUpperTribeRow());
        if(state.getUpperBuildingRow() != null) upperIds.addAll(state.getUpperBuildingRow());

        List<String> lowerIds = new ArrayList<>();
        if(state.getLowerTribeRow() != null) lowerIds.addAll(state.getLowerTribeRow());
        if(state.getLowerBuildingRow() != null) lowerIds.addAll(state.getLowerBuildingRow());

        List<String> allIds = new ArrayList<>(upperIds);
        allIds.addAll(lowerIds);

        // Pulisci cache per carte non più sul tabellone
        availableCardViewCache.keySet().removeIf(id -> !allIds.contains(id));

        // Crea/Aggiorna le View
        for (String cardId : allIds) {
            if (!availableCardViewCache.containsKey(cardId)) {
                CardView cardView = new CardView(cardId);
                cardView.setOnMouseClicked(e -> handleCardSelection(cardView));
                availableCardViewCache.put(cardId, cardView);
            }
        }

        // Posiziona nelle righe corrette
        upperRowCardsContainer.getChildren().setAll(
                upperIds.stream().map(availableCardViewCache::get).collect(Collectors.toList())
        );
        lowerRowCardsContainer.getChildren().setAll(
                lowerIds.stream().map(availableCardViewCache::get).collect(Collectors.toList())
        );
    }

    // --- NUOVA LOGICA: LA TUA PLANCIA A CASCATA ---
    private void renderMyBoard(LightPlayer localPlayer) {
        if (localPlayer == null) return;

        // Aggiorna la view delle stat (Cibo/Prestigio)
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

        // Rimuovi dalla cache le carte non più possedute
        myCardsCache.keySet().removeIf(id -> !currentIds.contains(id));

        // Crea le nuove CardView se non esistono
        for (String cardId : allMyCards) {
            if (!myCardsCache.containsKey(cardId)) {
                CardView cardView = new CardView(cardId);
                myCardsCache.put(cardId, cardView);
            }
        }

        // PULISCI IL CONTENITORE PRINCIPALE
        myCardsContainer.getChildren().clear();

        // RAGGRUPPA LE CARTE PER CATEGORIA PER FARE LA CASCATA
        Map<String, List<CardView>> groupedCards = new HashMap<>();
        for (String cardId : allMyCards) {
            String category = getCardCategory(cardId);
            groupedCards.computeIfAbsent(category, k -> new ArrayList<>()).add(myCardsCache.get(cardId));
        }

        // CREA UNA COLONNA PER OGNI CATEGORIA
        for (Map.Entry<String, List<CardView>> entry : groupedCards.entrySet()) {
            VBox column = new VBox();
            column.setAlignment(Pos.TOP_CENTER);

            // IL TRUCCO: Spaziatura negativa! Modifica -120 in base alle dimensioni del tuo componente CardView
            column.setSpacing(-150.0);

            // Aggiungi le carte (ordinandole se vuoi)
            column.getChildren().addAll(entry.getValue());

            myCardsContainer.getChildren().add(column);
        }
    }

    // --- Metodo di Utility per la cascata ---
    private String getCardCategory(String cardId) {
        if (cardId.startsWith("building")) return "Building";
        if (cardId.contains("artist")) return "Artist";
        if (cardId.contains("builder")) return "Builder";
        if (cardId.contains("hunter")) return "Hunter";
        if (cardId.contains("gatherer")) return "Gatherer";
        if (cardId.contains("inventor")) return "Inventor";
        if (cardId.contains("shaman")) return "Shaman";
        return "Generic";
    }

    // --- IL RESTO DEL TUO CODICE RIMANE UGUALE ---

    private void renderOpponents(Map<String, LightPlayer> players, String localPlayerName) {
        // [TUO CODICE ORIGINALE, NON TOCCATO]
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

    private void renderOfferTiles(List<LightOfferTile> tiles) {
        // [TUO CODICE ORIGINALE, NON TOCCATO]
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
        // [TUO CODICE ORIGINALE, NON TOCCATO]
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
            if (confirmActionButton != null) confirmActionButton.setVisible(hasSelection);
        });
    }

    @FXML
    private void handleConfirmAction() {
        // [TUO CODICE ORIGINALE, NON TOCCATO]
        clientController.requestResolveAction(new ArrayList<>(selectedCharacterIds), new ArrayList<>(selectedBuildingIds));
        selectedCharacterIds.clear();
        selectedBuildingIds.clear();
        if (confirmActionButton != null) confirmActionButton.setVisible(false);
        availableCardViewCache.values().forEach(cv -> cv.setHighlight(false));
    }

    private void handleTotemPlacement(OfferView selectedTile) {
        // [TUO CODICE ORIGINALE, NON TOCCATO]
        String tileId = selectedTile.getTileData().getId();
        selectedTile.setDisable(true);
        clientController.requestPlaceTotem(tileId);
    }

    private void renderTurnOrder(it.polimi.ingsw.am32.client.model.LightTurnOrderTile turnOrderTile) {
        // [TUO CODICE ORIGINALE, NON TOCCATO]
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
        // [TUO CODICE ORIGINALE, NON TOCCATO]
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
        upperRowCardsContainer.getChildren().clear(); // AGGIORNATO
        lowerRowCardsContainer.getChildren().clear(); // AGGIORNATO
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
        // [TUO CODICE ORIGINALE, NON TOCCATO]
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