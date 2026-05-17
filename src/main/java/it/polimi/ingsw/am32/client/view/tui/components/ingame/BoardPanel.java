package it.polimi.ingsw.am32.client.view.tui.components.ingame;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightOfferTile;
import it.polimi.ingsw.am32.client.model.LightTurnOrderTile;
import it.polimi.ingsw.am32.client.view.tui.components.global.AbstractTuiPanel;
import it.polimi.ingsw.am32.client.view.tui.components.global.TuiCardRenderer;
import it.polimi.ingsw.am32.client.view.tui.utils.TuiColorPalette;
import it.polimi.ingsw.am32.client.view.tui.utils.CardFormatter;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import it.polimi.ingsw.am32.dto.setup.*;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel responsible for displaying the central game board with drawn cards.
 */
public class BoardPanel extends AbstractTuiPanel {
    private LightGameState state;
    private final TuiCardRenderer renderer;

    /**
     * Board panel is instantiated and internal utilities are configured.
     */
    public BoardPanel() {
        // renderer utility is initialized
        this.renderer = new TuiCardRenderer();
    }

    /**
     * Game state data is updated.
     *
     * @param state the current game state
     */
    public void updateData(LightGameState state) {
        // state is updated
        this.state = state;
    }

    /**
     * The game board is drawn with concatenated rows and a central divider for turn order and offers.
     *
     * @param terminal the terminal instance
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
     * @param width the available width
     * @param height the available height
     */
    @Override
    public void draw(Terminal terminal, int startX, int startY, int width, int height) {
        // bounding box is drawn
        drawBox(terminal, startX, startY, width, height, "Game Board");

        // content area is cleared
        clearContent(terminal, startX, startY, width, height);

        if (state == null) return;

        int currentY = startY + 1;
        int usableWidth = width - 4;

        // lists are gathered
        List<String> upperCards = new ArrayList<>(state.getUpperTribeRow());
        upperCards.addAll(state.getUpperBuildingRow());

        List<String> lowerCards = new ArrayList<>(state.getLowerTribeRow());
        lowerCards.addAll(state.getLowerBuildingRow());

        // view mode is switched based on terminal dimensions
        if (terminal.getWidth() >= 153 && terminal.getHeight() >= 47) {
            // normal view with tiles is drawn
            currentY += drawCardRow(terminal, upperCards, startX + 2, currentY, usableWidth);

            int spacing = 2;

            // turn order is drawn on the left
            int turnOrderWidth = drawTurnOrderTile(terminal, state.getTurnOrderTile(), startX + 2, currentY);
            int offerWidth = usableWidth - turnOrderWidth - spacing;

            // offer track is drawn on the right and its dynamic height is captured
            int offerHeight = drawOfferTrack(terminal, state.getOfferTrack(), startX + 2 + turnOrderWidth + spacing, currentY, offerWidth);

            // vertical cursor is advanced dynamically based on the tallest element
            currentY += Math.max(7, offerHeight) + 1;

            // lower row with tiles is drawn
            drawCardRow(terminal, lowerCards, startX + 2, currentY, usableWidth);

        } else {
            // degraded view with two columns is drawn
            int maxListHeight = drawTwoColumnCards(terminal, upperCards, lowerCards, startX + 2, currentY, usableWidth);

            // vertical cursor is advanced
            currentY += maxListHeight + 2;

            int spacing = 2;

            // turn order is drawn on the left
            int turnOrderWidth = drawTurnOrderTile(terminal, state.getTurnOrderTile(), startX + 2, currentY);
            int offerWidth = usableWidth - turnOrderWidth - spacing;

            // offer track is drawn on the right
            drawOfferTrack(terminal, state.getOfferTrack(), startX + 2 + turnOrderWidth + spacing, currentY, offerWidth);
        }
    }

    /**
     * Cards are printed in a two-column layout for the degraded view.
     *
     * @param terminal the terminal instance
     * @param upperCards the list of upper row cards
     * @param lowerCards the list of lower row cards
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
     * @param maxWidth the maximum available width
     * @return the total number of vertical lines consumed
     */
    private int drawTwoColumnCards(Terminal terminal, List<String> upperCards, List<String> lowerCards, int startX, int startY, int maxWidth) {
        int leftColWidth = maxWidth / 2 - 1;
        int rightColWidth = maxWidth - maxWidth / 2 - 1;
        int rightColX = startX + maxWidth / 2 + 1;

        int currentY = startY;

        // titles are printed
        moveCursor(terminal, startX, currentY);
        terminal.writer().print("\033[1m--- Upper Row ---\033[0m");

        moveCursor(terminal, rightColX, currentY);
        terminal.writer().print("\033[1m--- Lower Row ---\033[0m");

        currentY++;

        int maxRows = Math.max(upperCards != null ? upperCards.size() : 0, lowerCards != null ? lowerCards.size() : 0);

        // lists are iterated row by row
        for (int i = 0; i < maxRows; i++) {
            // left side is handled
            if (upperCards != null && i < upperCards.size()) {
                String line = formatCardStringCompact(upperCards.get(i), leftColWidth);
                moveCursor(terminal, startX, currentY + i);
                terminal.writer().print(line);
            }

            // right side is handled
            if (lowerCards != null && i < lowerCards.size()) {
                String line = formatCardStringCompact(lowerCards.get(i), rightColWidth);
                moveCursor(terminal, rightColX, currentY + i);
                terminal.writer().print(line);
            }
        }

        // consumed height is returned
        return maxRows + 1;
    }

    /**
     * Card information is formatted into a single string for the degraded view.
     *
     * @param fullId the card identifier
     * @param maxWidth the maximum allowed string width
     * @return the formatted ansi string
     */
    private String formatCardStringCompact(String fullId, int maxWidth) {
        AssetManager am = AssetManager.getInstance();
        CardFormatter formatter = CardFormatter.getInstance();
        GameDictionary dict = GameDictionary.getInstance();

        String idNum = fullId.substring(fullId.lastIndexOf("_") + 1);
        String prefixVisible = "[" + idNum + "] ";
        String prefixAnsi = prefixVisible;
        String typeVisible = "";
        String typeAnsi = "";
        String effect = "";

        if (fullId.startsWith("event")) {
            EventCardDTO dto = am.getEventCard(fullId);
            if (dto != null) {
                // event id is hidden
                prefixVisible = "[  ] ";
                prefixAnsi = prefixVisible;
                typeVisible = dict.translate(dto.getEventType());
                typeAnsi = TuiColorPalette.getEventColor() + typeVisible + TuiColorPalette.getReset();
                effect = formatter.formatEvent(dto).replace("\n", " ");
            }
        } else if (fullId.startsWith("character")) {
            CharacterCardDTO dto = am.getCharacterCard(fullId);
            if (dto != null) {
                typeVisible = dict.translate(dto.getCharacterType());
                String color = TuiColorPalette.getCharacterColor(dto.getCharacterType());
                typeAnsi = color + typeVisible + TuiColorPalette.getReset();
                effect = formatter.formatCharacter(dto).replace("\n", " ");
            }
        } else if (fullId.startsWith("building")) {
            BuildingCardDTO dto = am.getBuildingCard(fullId);
            if (dto != null) {
                typeVisible = "BUILDING | Cost: " + dto.getCost() + " 🍖";
                typeAnsi = TuiColorPalette.getBuildingColor() + "BUILDING" + TuiColorPalette.getReset() + " | Cost: " + dto.getCost() + " 🍖";
                effect = formatter.formatBuilding(dto).replace("\n", " ");
            }
        }

        String separator = " | ";
        int visibleBaseLen = prefixVisible.length() + typeVisible.length() + separator.length();

        // string is safely truncated
        if (visibleBaseLen + effect.length() > maxWidth) {
            int allowedEffectLen = Math.max(0, maxWidth - visibleBaseLen - 3);
            if (effect.length() > allowedEffectLen) {
                effect = effect.substring(0, allowedEffectLen) + "...";
            }
        }

        // formatted string is returned
        return prefixAnsi + typeAnsi + separator + effect;
    }

    /**
     * Row of cards is dynamically drawn with smart balanced wrapping.
     *
     * @param terminal the terminal instance
     * @param cardIds the list of card identifiers
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
     * @param maxWidth the maximum available width
     * @return the total number of vertical lines consumed
     */
    private int drawCardRow(Terminal terminal, List<String> cardIds, int startX, int startY, int maxWidth) {
        // empty row check is performed
        if (cardIds == null || cardIds.isEmpty()) return 1;

        // narrow card dimensions are defined with reduced width
        int cardWidth = 18;
        int cardHeight = 6;
        int currentX = startX;
        int currentY = startY;

        // smart wrapping limit is calculated with zero margin
        int maxCapacity = maxWidth / cardWidth;
        int totalCards = cardIds.size();

        // balanced limit is assigned with a safe fallback to prevent division by zero
        int cardsPerRow = Math.max(1, maxCapacity);
        if (totalCards > maxCapacity && maxCapacity > 0) {
            cardsPerRow = (int) Math.ceil((double) totalCards / 2);
        }

        int count = 0;

        // cards are iterated
        for (String fullId : cardIds) {
            // line breaks are evaluated via count
            if (count > 0 && count % cardsPerRow == 0) {
                currentX = startX;
                currentY += cardHeight;
            }

            // individual card is drawn
            drawSingleCard(terminal, fullId, currentX, currentY, cardWidth, cardHeight);

            // cursor is advanced with zero margin
            currentX += cardWidth;
            count++;
        }

        // total consumed height is returned
        return (currentY - startY) + cardHeight + 1;
    }

    /**
     * Specific visual representation of a single card is evaluated and drawn.
     *
     * @param terminal the terminal instance
     * @param fullId the full card identifier
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the card
     * @param height the height of the card
     */
    private void drawSingleCard(Terminal terminal, String fullId, int x, int y, int width, int height) {
        AssetManager am = AssetManager.getInstance();
        CardFormatter formatter = CardFormatter.getInstance();

        // pure numeric id is extracted
        String idNum = fullId.substring(fullId.lastIndexOf("_") + 1);

        if (fullId.startsWith("character")) {
            // character dto is fetched
            CharacterCardDTO dto = am.getCharacterCard(fullId);
            if (dto == null) return;

            String type = dto.getCharacterType();
            // raw type is translated via dictionary
            String translatedType = GameDictionary.getInstance().translate(type);
            String color = TuiColorPalette.getCharacterColor(type);

            // prestige is appended and forced visible for builders
            String prestige = "";
            if (dto.getBasePrestige() > 0 || "BUILDER".equalsIgnoreCase(type)) {
                prestige = dto.getBasePrestige() + " 🏆";
            }

            String effect = formatter.formatCharacter(dto);

            // card is rendered using translated type
            renderer.drawCardBox(terminal, x, y, width, height, idNum, translatedType, "", prestige, color, effect);

        } else if (fullId.startsWith("building")) {
            // building dto is fetched
            BuildingCardDTO dto = am.getBuildingCard(fullId);
            if (dto == null) return;

            String color = TuiColorPalette.getBuildingColor();
            // cost is appended with emoji
            String cost = dto.getCost() + " 🍖";
            // prestige is appended with emoji
            String prestige = dto.getBasePrestige() > 0 ? dto.getBasePrestige() + " 🏆" : "";
            String effect = formatter.formatBuilding(dto);

            // card is rendered
            renderer.drawCardBox(terminal, x, y, width, height, idNum, "BUILDING", cost, prestige, color, effect);

        } else if (fullId.startsWith("event")) {
            // event dto is fetched
            EventCardDTO dto = am.getEventCard(fullId);
            if (dto == null) return;

            // card formatter is retrieved
            String color = TuiColorPalette.getEventColor();

            // title is retrieved securely via translation method
            String eventName = GameDictionary.getInstance().translate(dto.getEventType());

            // effect is generated with strategic \n
            String effect = formatter.formatEvent(dto);

            // card is rendered with empty id label and custom header
            renderer.drawCardBox(terminal, x, y, width, height, "", eventName, "", "", color, effect);
        }
    }

    /**
     * Offer track is dynamically drawn with smart wrapping.
     *
     * @param terminal the terminal instance
     * @param track    the list of light offer tiles
     * @param startX   the starting x coordinate
     * @param startY   the starting y coordinate
     * @param maxWidth the maximum available width
     */
    private int drawOfferTrack(Terminal terminal, List<LightOfferTile> track, int startX, int startY, int maxWidth) {
        // tile dimensions are strictly defined
        int tileWidth = 14;
        int tileHeight = 7;
        int currentX = startX;
        int currentY = startY;

        // smart wrapping limit is calculated with zero margin
        int maxCapacity = maxWidth / tileWidth;
        int totalTiles = track.size();

        // balanced limit is assigned
        int tilesPerRow = Math.max(1, maxCapacity);
        if (totalTiles > maxCapacity && maxCapacity > 0) {
            tilesPerRow = (int) Math.ceil((double) totalTiles / 2);
        }

        int count = 0;

        // tiles are iterated
        for (LightOfferTile tile : track) {
            // line breaks are evaluated
            if (count > 0 && count % tilesPerRow == 0) {
                currentX = startX;
                currentY += tileHeight;
            }

            // identification letter is extracted
            String idLetter = tile.getId().substring(tile.getId().lastIndexOf("_") + 1);

            // tile data is fetched
            OfferTileDTO dto = AssetManager.getInstance().getOfferTile(tile.getId());

            // string statistics are formulated with visual indicators
            String stats = "";
            if (dto != null) {
                String upper = dto.getDrawsFromUpperRow() > 0 ? "↑" + dto.getDrawsFromUpperRow() + " " : "";
                String lower = dto.getDrawsFromLowerRow() > 0 ? "↓" + dto.getDrawsFromLowerRow() + " " : "";
                String food = dto.getFoodBonus() > 0 ? "+" + dto.getFoodBonus() + " 🍖" : "";
                stats = upper + lower + food;
            }

            // totem color is determined
            String totemColor = getPlayerTotemColorAnsi(tile.getOccupyingPlayer());

            // tile box is rendered
            renderer.drawOfferTileBox(terminal, currentX, currentY, tileWidth, tileHeight, idLetter, totemColor, stats.trim());

            // cursor is advanced
            currentX += tileWidth;
            count++;
        }

        // total consumed height is returned
        return (currentY - startY) + tileHeight;
    }

    /**
     * Turn order tile is drawn as a vertical slot container dynamically centered and resized.
     *
     * @param terminal the terminal instance
     * @param turnOrder the light turn order tile
     * @param x the starting x coordinate
     * @param y the starting y coordinate
     * @return the calculated dynamic width of the tile
     */
    private int drawTurnOrderTile(Terminal terminal, LightTurnOrderTile turnOrder, int x, int y) {
        // true total players count is extracted from state to guarantee all slots are drawn
        int numPlayers = state != null && state.getPlayers() != null ? state.getPlayers().size() : 0;
        // fallback limit is applied
        if (numPlayers == 0) numPlayers = 5;

        // maximum text width is initialized
        int maxTextWidth = 0;
        // slot texts list is initialized
        List<String> slotTexts = new ArrayList<>();
        // turn order tile dto is initialized
        TurnOrderTileDTO dto = null;

        if (turnOrder != null && AssetManager.getInstance() != null) {
            dto = AssetManager.getInstance().getTurnOrderTile(turnOrder.getTileId());
        }

        // modifier strings are generated and max width is calculated
        for (int i = 0; i < numPlayers; i++) {
            String text = "";
            if (dto != null) {
                int food = dto.getFoodModifiers().get(i);
                // prestige is extracted correctly
                int prestige = dto.getPrestigeModifiers().get(i);

                if (food > 0) text += "+" + food + " 🍖";
                else if (food < 0) text += food + " 🍖";

                if (prestige != 0) {
                    if (!text.isEmpty()) text += " / ";
                    if (prestige > 0) text += "+" + prestige + " 🏆";
                    else text += prestige + " 🏆";
                }
            }

            slotTexts.add(text.trim());
            // force minimum width of 1 to preserve at least a space when empty
            maxTextWidth = Math.max(maxTextWidth, Math.max(1, text.trim().length()));
        }

        // tile width is dynamically extended based on the longest modifier text
        int width = Math.max(9, maxTextWidth + 6);
        int height = 7;
        String borderColor = "\033[38;5;240m";
        String reset = TuiColorPalette.getReset();

        // borders are drawn
        moveCursor(terminal, x, y);
        terminal.writer().print(borderColor + "┌");
        for (int i = 0; i < width - 2; i++) terminal.writer().print("─");
        terminal.writer().print("┐" + reset);

        for (int i = 1; i < height - 1; i++) {
            moveCursor(terminal, x, y + i);
            terminal.writer().print(borderColor + "│" + reset);
            moveCursor(terminal, x + width - 1, y + i);
            terminal.writer().print(borderColor + "│" + reset);
        }

        moveCursor(terminal, x, y + height - 1);
        terminal.writer().print(borderColor + "└");
        for (int i = 0; i < width - 2; i++) terminal.writer().print("─");
        terminal.writer().print("┘" + reset);

        // slot list is extracted securely
        List<String> players = turnOrder != null ? turnOrder.getPlayers() : new ArrayList<>();

        // vertical offset is calculated to shift up even counts and center odd counts
        int emptyRows = 5 - numPlayers;
        int startOffset = emptyRows / 2;

        // horizontal centers are calculated for tight and wide brackets
        int centerX = x + Math.max(1, (width - 3) / 2);
        int textStartX = x + Math.max(1, (width - (maxTextWidth + 4)) / 2);

        // slots are populated dynamically
        for (int i = 0; i < numPlayers; i++) {
            String playerNickname = i < players.size() ? players.get(i) : null;

            // offer track occupation is verified
            boolean isOccupyingOffer = false;
            if (playerNickname != null && state != null && state.getOfferTrack() != null) {
                for (it.polimi.ingsw.am32.client.model.LightOfferTile tile : state.getOfferTrack()) {
                    if (playerNickname.equals(tile.getOccupyingPlayer())) {
                        isOccupyingOffer = true;
                        break;
                    }
                }
            }

            int slotY = y + 1 + startOffset + i;

            if (playerNickname != null && !isOccupyingOffer) {
                // tight brackets for present player are drawn
                moveCursor(terminal, centerX, slotY);
                String color = getPlayerTotemColorAnsi(playerNickname);
                String safeColor = color != null ? color : reset;
                terminal.writer().print("[" + safeColor + "■" + reset + "]");
            } else {
                // wide brackets with centered modifier text are drawn
                String content = slotTexts.get(i);
                moveCursor(terminal, textStartX, slotY);

                // padding is calculated to center the bracket block
                int padLeft = (maxTextWidth - content.length()) / 2;
                int padRight = maxTextWidth - content.length() - padLeft;

                // padding is applied outside the brackets
                String formattedSlot = " ".repeat(padLeft) + "[" + content + "]" + " ".repeat(padRight);
                terminal.writer().print(" " + formattedSlot + " ");
            }
        }

        // dynamic width is returned for layout calculation
        return width;
    }

    /**
     * The player totem color ansi is retrieved by nickname.
     *
     * @param nickname the player nickname
     * @return the ansi color string or null if not found
     */
    private String getPlayerTotemColorAnsi(String nickname) {
        if (nickname == null || nickname.isEmpty() || state == null) return null;

        // player is retrieved
        it.polimi.ingsw.am32.client.model.LightPlayer player = state.getPlayers().get(nickname);
        if (player != null) {
            return TuiColorPalette.getTotemColor(player.getTotemColor());
        }
        return null;
    }
}
