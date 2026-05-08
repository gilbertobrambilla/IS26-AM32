package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class responsible for managing JavaFX scenes and loading FXML files.
 * It implements a custom Controller Factory to automatically inject the ClientController
 * into every loaded ViewController.
 */
public class SceneManager {

    private static Stage primaryStage;
    private static ClientController clientController;

    /**
     * Initializes the SceneManager with the primary stage and the main controller.
     * This must be called once during the application startup.
     *
     * @param stage      the primary JavaFX Stage.
     * @param controller the main ClientController to be injected into views.
     */
    public static void initialize(Stage stage, ClientController controller) {
        primaryStage = stage;
        clientController = controller;
    }

    /**
     * Loads an FXML file, instantiates its controller (injecting dependencies),
     * and switches the primary stage to the new scene.
     *
     * @param fxmlPath the absolute path to the FXML file within the resources folder (e.g., "/fxml/login-view.fxml").
     */
    public static void switchScene(String fxmlPath) {
        if (primaryStage == null || clientController == null) {
            throw new IllegalStateException("SceneManager has not been initialized yet.");
        }

        // --- CRITICAL ARCHITECTURE RULE ---
        // Network events trigger UI changes. We MUST route this to the JavaFX Application Thread.
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));

                /*
                 * CUSTOM CONTROLLER FACTORY
                 * Instead of letting JavaFX use the default no-args constructor, we intercept
                 * the controller creation and force the injection of the ClientController via constructor.
                 */
                loader.setControllerFactory(controllerClass -> {
                    try {
                        // Look for a constructor that accepts a ClientController parameter and instantiate it
                        return controllerClass.getConstructor(ClientController.class).newInstance(clientController);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName() +
                                ". Ensure it has a constructor accepting ClientController.", e);
                    }
                });

                Parent root = loader.load();

                // --- OPTIMIZATION: Reuse existing scene if possible ---
                Scene currentScene = primaryStage.getScene();
                if (currentScene == null) {
                    currentScene = new Scene(root);
                    primaryStage.setScene(currentScene);
                } else {
                    currentScene.setRoot(root); // Swap only the root node for better performance
                }

                // --- APPLY GLOBAL CSS ---
                URL cssResource = SceneManager.class.getResource("/css/global.css");

                if (cssResource != null) {
                    String cssUrl = cssResource.toExternalForm();
                    // Only add the stylesheet if it's not already present in the scene
                    if (!currentScene.getStylesheets().contains(cssUrl)) {
                        currentScene.getStylesheets().add(cssUrl);
                    }
                } else {
                    // Log a warning if the file is missing to help debugging
                    System.err.println("[GUI WARNING] Style resource not found: /css/global.css");
                }

                // Ensure the stage is visible
                primaryStage.show();

                System.out.println("[GUI] Switched to scene: " + fxmlPath);

            } catch (IOException e) {
                System.err.println("[GUI ERROR] Failed to load FXML file: " + fxmlPath);
                e.printStackTrace();
            }
        });
    }
}
