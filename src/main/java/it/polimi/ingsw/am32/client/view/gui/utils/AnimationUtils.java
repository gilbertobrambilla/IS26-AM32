package it.polimi.ingsw.am32.client.view.gui.utils;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * utility class is implemented to provide reusable javafx animations.
 * micro-interactions for error feedback are centralized here.
 */
public class AnimationUtils {

    /**
     * horizontal shake animation is applied to the specified node to indicate an invalid action.
     *
     * @param node the javafx node to be animated
     */
    public static void shakeNode(Node node) {
        if (node == null) return;

        // translate transition is instantiated with short duration
        TranslateTransition transition = new TranslateTransition(Duration.millis(50), node);

        // horizontal offset is defined
        transition.setFromX(0f);
        transition.setByX(8f);

        // rapid back-and-forth movement is configured
        transition.setCycleCount(6);
        transition.setAutoReverse(true);

        // animation is played
        transition.playFromStart();
    }

    /**
     * smooth entry, pause, and exit animation sequence is applied to the toast notification node.
     *
     * @param node the javafx node representing the toast layout container
     */
    public static void playToastAnimation(Node node) {
        if (node == null) return;

        // node is prepared and made visible before transitions begin
        node.setVisible(true);
        node.setOpacity(0.0);

        // fade-in transition is configured
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // display duration pause is configured
        PauseTransition stayVisible = new PauseTransition(Duration.seconds(3.5));

        // fade-out transition is configured
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // visibility is completely disabled after fade-out completion
        fadeOut.setOnFinished(event -> node.setVisible(false));

        // sequential pipeline is assembled and executed
        SequentialTransition sequence = new SequentialTransition(fadeIn, stayVisible, fadeOut);
        sequence.playFromStart();
    }
}
