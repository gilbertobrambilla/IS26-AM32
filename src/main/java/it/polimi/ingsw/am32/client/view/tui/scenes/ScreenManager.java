package it.polimi.ingsw.am32.client.view.tui.scenes;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

/**
 * Coordinates the layout and rendering of the active scene.
 */
public class ScreenManager {
    private final Terminal terminal;
    private LineReader lineReader;
    private Scene activeScene;

    /**
     * The screen manager is instantiated with the given terminal.
     *
     * @param terminal the underlying jline terminal.
     */
    public ScreenManager(Terminal terminal) {
        this.terminal = terminal;
        // terminal signal handler is registered for window resize
        this.terminal.handle(Terminal.Signal.WINCH, this::handleResize);
    }

    /**
     * The line reader is set for input restoration.
     *
     * @param lineReader the jline line reader.
     */
    public void setLineReader(LineReader lineReader) {
        this.lineReader = lineReader;
    }

    /**
     * The active scene is updated.
     *
     * @param scene the new scene to be rendered.
     */
    public void setActiveScene(Scene scene) {
        this.activeScene = scene;
    }

    /**
     * The entire screen is recalculated and the active scene is rendered.
     */
    public synchronized void render() {
        if (activeScene == null) {
            return;
        }

        // terminal dimensions are retrieved
        int width = terminal.getWidth();
        int height = terminal.getHeight();

        // fallback dimensions are applied if terminal is dumb
        if (width == 0 || height == 0) {
            width = 120;
            height = 30;
        }

        // screen is cleared
        terminal.puts(InfoCmp.Capability.clear_screen);

        if (width < 60 || height < 15) {
            // warning is printed for insufficient terminal size
            terminal.writer().print("\033[" + (height / 2) + ";" + (Math.max(1, width / 2 - 15)) + "H");
            terminal.writer().print("terminal too small. please resize.");
        } else {
            // active scene is drawn
            activeScene.draw(terminal, width, height);
        }

        // cursor is moved to the input area
        restoreInputArea(height);

        // output is flushed
        terminal.flush();
    }

    /**
     * The terminal resize signal is handled.
     *
     * @param signal the received signal.
     */
    private void handleResize(Terminal.Signal signal) {
        // screen is rendered again
        render();
    }

    /**
     * The cursor is restored to the input prompt area.
     *
     * @param terminalHeight the total height of the terminal.
     */
    private void restoreInputArea(int terminalHeight) {
        // cursor is moved to the bottom row using the jline writer
        terminal.writer().print("\033[" + terminalHeight + ";1H");
        // row is cleared to ensure clean prompt rendering
        terminal.writer().print("\033[2K");

        if (lineReader != null && lineReader.isReading()) {
            // prompt is redrawn
            lineReader.callWidget(LineReader.REDRAW_LINE);
            // line is redisplayed
            lineReader.callWidget(LineReader.REDISPLAY);
        }
    }
}
