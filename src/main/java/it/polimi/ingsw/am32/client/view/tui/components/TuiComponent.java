package it.polimi.ingsw.am32.client.view.tui.components;

import org.jline.terminal.Terminal;

/**
 * Contract for a drawable text interface component.
 */
public interface TuiComponent {

    /**
     * The component is drawn on the terminal within the specified bounds.
     *
     * @param terminal the terminal instance
     * @param startX the starting x coordinate
     * @param startY the starting y coordinate
     * @param width the available width
     * @param height the available height
     */
    void draw(Terminal terminal, int startX, int startY, int width, int height);
}
