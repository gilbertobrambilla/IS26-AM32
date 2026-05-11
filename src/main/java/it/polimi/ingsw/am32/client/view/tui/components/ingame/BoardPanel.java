package it.polimi.ingsw.am32.client.view.tui.components.ingame;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.model.LightTurnOrderTile;
import it.polimi.ingsw.am32.client.view.tui.components.global.AbstractTuiPanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.TuiCardRenderer;
import it.polimi.ingsw.am32.client.view.tui.utils.TuiColorPalette;
import it.polimi.ingsw.am32.client.view.utils.CardFormatter;
import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.dto.setup.EventCardDTO;
import it.polimi.ingsw.am32.dto.setup.OfferTileDTO;
import org.jline.terminal.Terminal;

import java.util.List;

/**
 * Panel responsible for displaying the central game board with drawn cards.
 */
public class BoardPanel extends AbstractTuiPanel {
    private LightGameState state;
    private final CardFormatter formatter;
    private final TuiCardRenderer renderer;

    /**
     * Board panel is instantiated and internal utilities are configured.
     */
    public BoardPanel() {
        // card formatter is initialized
        this.formatter = new CardFormatter();
        // dictionary is loaded for translations
        this.formatter.loadDictionary("strings_en.json");
        // renderer utility is initialized
        this.renderer = new TuiCardRenderer();
    }

    /**
     * The game state data is updated.
     *
     * @param state the current game state.
     */
    public void updateData(LightGameState state) {
        // state is updated
        this.state = state;
    }

    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // bounding box is drawn
        drawBox(terminal, startX, startY, width, height, "Game Board");

        // content area is cleared
        clearContent(terminal, startX, startY, width, height);

        if (state == null) return;

        // initial row offset is defined
        int currentY = startY + 2;
        int maxTextWidth = width - 4;

        // deck info is drawn
        moveCursor(terminal, startX + 2, currentY++);
        terminal.writer().print("Tribe deck size: " + state.getTribeDeckSize() + " | Top card era: " + state.getTribeDeckTopCardEra());
        currentY++;

        // upper tribe row is drawn
        moveCursor(terminal, startX + 2, currentY++);
        terminal.writer().print("Upper Tribe Row:");
        currentY += drawCardRow(terminal, state.getUpperTribeRow(), startX + 2, currentY, maxTextWidth);

        // lower tribe row is drawn
        moveCursor(terminal, startX + 2, currentY++);
        terminal.writer().print("Lower Tribe Row:");
        currentY += drawCardRow(terminal, state.getLowerTribeRow(), startX + 2, currentY, maxTextWidth);

        // upper building row is drawn
        moveCursor(terminal, startX + 2, currentY++);
        terminal.writer().print("Upper Building Row:");
        currentY += drawCardRow(terminal, state.getUpperBuildingRow(), startX + 2, currentY, maxTextWidth);

        // lower building row is drawn
        moveCursor(terminal, startX + 2, currentY++);
        terminal.writer().print("Lower Building Row:");
        currentY += drawCardRow(terminal, state.getLowerBuildingRow(), startX + 2, currentY, maxTextWidth);

        // offer track is drawn
        if (currentY < startY + height - 4) {
            moveCursor(terminal, startX + 2, currentY++);
            terminal.writer().print("Offer Track:");
            currentY += drawOfferTrack(terminal, state.getOfferTrack(), startX + 2, currentY, maxTextWidth);
        }

        // turn order is drawn vertically
        LightTurnOrderTile turnOrder = state.getTurnOrderTile();
        if (turnOrder != null && currentY < startY + height - 2) {
            currentY++;
            moveCursor(terminal, startX + 2, currentY++);
            terminal.writer().print("Turn Order:");

            // players are iterated
            List<String> players = turnOrder.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                // bounds are checked
                if (currentY >= startY + height - 1) break;

                moveCursor(terminal, startX + 4, currentY++);
                terminal.writer().print((i + 1) + ". " + players.get(i));
            }
        }
    }

    /**
     * A row of cards is dynamically drawn with automatic line wrapping.
     *
     * @param terminal the terminal instance.
     * @param cardIds the list of card identifiers.
     * @param startX the starting x coordinate.
     * @param startY the starting y coordinate.
     * @param maxWidth the maximum available width.
     * @return the total number of vertical lines consumed.
     */
    private int drawCardRow(Terminal terminal, List<String> cardIds, int startX, int startY, int maxWidth) {
        // empty row check is performed
        if (cardIds == null || cardIds.isEmpty()) return 1;

        // card dimensions are defined
        int cardWidth = 18;
        int cardHeight = 6;
        int currentX = startX;
        int currentY = startY;

        // cards are iterated
        for (String fullId : cardIds) {
            // bounds are checked to wrap logic
            if (currentX + cardWidth > startX + maxWidth) {
                currentX = startX;
                currentY += cardHeight + 1;
            }

            // individual card is drawn
            drawSingleCard(terminal, fullId, currentX, currentY, cardWidth, cardHeight);

            // cursor is advanced
            currentX += cardWidth + 1;
        }

        // total consumed height is returned
        return (currentY - startY) + cardHeight + 1;
    }

    /**
     * The specific visual representation of a single card is evaluated and drawn.
     *
     * @param terminal the terminal instance.
     * @param fullId the full card identifier.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param width the width of the card.
     * @param height the height of the card.
     */
    private void drawSingleCard(Terminal terminal, String fullId, int x, int y, int width, int height) {
        // asset manager is retrieved
        AssetManager am = AssetManager.getInstance();

        // pure numeric id is extracted
        String idNum = fullId.substring(fullId.lastIndexOf("_") + 1);

        if (fullId.startsWith("character")) {
            // character dto is fetched
            CharacterCardDTO dto = am.getCharacterCard(fullId);
            if (dto == null) return;

            String type = dto.getCharacterType();
            String color = TuiColorPalette.getCharacterColor(type);
            String prestige = dto.getBasePrestige() > 0 ? dto.getBasePrestige() + " PP" : "";
            String effect = formatter.formatCharacter(dto);

            // card is rendered
            renderer.drawCardBox(terminal, x, y, width, height, idNum, type, "", prestige, color, effect);

        } else if (fullId.startsWith("building")) {
            // building dto is fetched
            BuildingCardDTO dto = am.getBuildingCard(fullId);
            if (dto == null) return;

            String color = TuiColorPalette.getBuildingColor();
            String cost = dto.getCost() + "F";
            String prestige = dto.getBasePrestige() > 0 ? dto.getBasePrestige() + " PP" : "";
            String effect = formatter.formatBuilding(dto);

            // card is rendered
            renderer.drawCardBox(terminal, x, y, width, height, idNum, "BUILDING", cost, prestige, color, effect);

        } else if (fullId.startsWith("event")) {
            // event dto is fetched
            EventCardDTO dto = am.getEventCard(fullId);
            if (dto == null) return;

            String color = TuiColorPalette.getEventColor();
            String effect = formatter.formatEvent(dto);

            // card is rendered
            renderer.drawCardBox(terminal, x, y, width, height, idNum, "EVENT", "", "", color, effect);
        }
    }

    /**
     * The offer track is dynamically drawn with automatic line wrapping.
     *
     * @param terminal the terminal instance.
     * @param track the list of light offer tiles.
     * @param startX the starting x coordinate.
     * @param startY the starting y coordinate.
     * @param maxWidth the maximum available width.
     * @return the total number of vertical lines consumed.
     */
    private int drawOfferTrack(Terminal terminal, List<LightOfferTile> track, int startX, int startY, int maxWidth) {
        // tile dimensions are defined
        int tileWidth = 12;
        int tileHeight = 4;
        int currentX = startX;
        int currentY = startY;

        // tiles are iterated
        for (LightOfferTile tile : track) {
            // bounds are checked to wrap logic
            if (currentX + tileWidth > startX + maxWidth) {
                currentX = startX;
                currentY += tileHeight + 1;
            }

            // identification letter is extracted
            String idLetter = tile.getId().substring(tile.getId().lastIndexOf("_") + 1);

            // tile data is fetched
            OfferTileDTO dto = AssetManager.getInstance().getOfferTile(tile.getId());

            // string statistics are formulated
            String stats = "";
            if (dto != null) {
                String upper = dto.getActDrawsUpper() > 0 ? "U:" + dto.getActDrawsUpper() + " " : "";
                String lower = dto.getActDrawsLower() > 0 ? "L:" + dto.getActDrawsLower() + " " : "";
                String food = dto.getActFoodBonus() > 0 ? "+" + dto.getActFoodBonus() + "F" : "";
                stats = upper + lower + food;
            }

            // tile box is rendered
            renderer.drawOfferTileBox(terminal, currentX, currentY, tileWidth, tileHeight, idLetter, tile.getOccupyingPlayer(), stats.trim());

            // cursor is advanced
            currentX += tileWidth + 1;
        }

        // total consumed height is returned
        return (currentY - startY) + tileHeight + 1;
    }
}
