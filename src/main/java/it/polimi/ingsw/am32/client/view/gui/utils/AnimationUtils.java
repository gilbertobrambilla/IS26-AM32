package it.polimi.ingsw.am32.client.view.gui.utils;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Reusable JavaFX micro-animations for ui feedback are provided.
 *
 * <p>All methods are stateless and operate directly on the supplied {@link Node}.
 * Instantiation is prevented.</p>
 */
public final class AnimationUtils {

    // shake
    private static final int    SHAKE_STEP_MS   = 50;
    private static final double SHAKE_OFFSET_PX = 8.0;
    private static final int    SHAKE_CYCLES    = 6;

    // toast
    private static final int    TOAST_FADE_IN_MS      = 250;
    private static final double TOAST_HOLD_SECONDS    = 3.5;
    private static final int    TOAST_FADE_OUT_MS     = 300;

    // pulse
    private static final int    PULSE_PERIOD_MS = 800;
    private static final double PULSE_SCALE_MAX = 1.25;

    /**
     * Instantiation is prevented for the utility class.
     */
    private AnimationUtils() {}

    /**
     * A horizontal shake animation is played on {@code node} to signal an invalid action.
     *
     * @param node the target node; silently ignored if {@code null}
     */
    public static void shakeNode(Node node) {
        if (node == null) return;

        TranslateTransition shake = new TranslateTransition(Duration.millis(SHAKE_STEP_MS), node);
        shake.setFromX(0.0);
        shake.setByX(SHAKE_OFFSET_PX);
        shake.setCycleCount(SHAKE_CYCLES);
        shake.setAutoReverse(true);
        shake.playFromStart();
    }

    /**
     * A fade-in, hold, and fade-out sequence is played on {@code node}.
     * The sequence is suitable for transient toast notifications.
     *
     * <p>The node is made visible and fully transparent before the animation starts
     * and hidden again when the sequence completes.</p>
     *
     * @param node the toast container node; silently ignored if {@code null}
     */
    public static void playToastAnimation(Node node) {
        if (node == null) return;

        node.setVisible(true);
        node.setOpacity(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(TOAST_FADE_IN_MS), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition hold = new PauseTransition(Duration.seconds(TOAST_HOLD_SECONDS));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(TOAST_FADE_OUT_MS), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> node.setVisible(false));

        new SequentialTransition(fadeIn, hold, fadeOut).playFromStart();
    }

    /**
     * An indefinite pulsing scale animation is started on {@code node}.
     * The animation is intended for turn-order highlights.
     *
     * <p>The returned {@link ScaleTransition} is already playing; call {@code stop()} on it
     * and reset scale to 1.0 to cancel.</p>
     *
     * @param node the target node; silently ignored if {@code null}
     * @return the running transition, or {@code null} if node was null
     */
    public static ScaleTransition playPulse(Node node) {
        if (node == null) return null;

        ScaleTransition pulse = new ScaleTransition(Duration.millis(PULSE_PERIOD_MS), node);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(PULSE_SCALE_MAX);
        pulse.setToY(PULSE_SCALE_MAX);
        pulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
        return pulse;
    }
}
