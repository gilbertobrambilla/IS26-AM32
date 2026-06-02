package it.polimi.ingsw.am32.client.view.tui.scenes;

import org.jline.terminal.Terminal;

/**
 * Contract for a drawable screen scene.
 */
public interface Scene {

    /**
     * The scene is rendered on the terminal within the specified dimensions.
     *
     * @param terminal the terminal instance
     * @param width the available terminal width
     * @param height the available terminal height
     */
    void draw(Terminal terminal, int width, int height);
}
