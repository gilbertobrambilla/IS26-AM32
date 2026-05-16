package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.controllers.ViewController;
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
    private static ViewController currentController;

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
     * @param viewPath the absolute path to the FXML file within the resources folder (e.g., "/fxml/login-view.fxml").
     */
    public static void switchScene(ViewPaths viewPath) {
        if (primaryStage == null || clientController == null) {
            throw new IllegalStateException("SceneManager has not been initialized yet.");
        }

        // scene switch is always guaranteed to run on the JavaFX Application Thread
        Runnable sceneSwitch = () -> {
            try {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(viewPath.getPath()));

                loader.setControllerFactory(controllerClass -> {
                    try {
                        return controllerClass.getConstructor(ClientController.class).newInstance(clientController);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName() +
                                ". Ensure it has a constructor accepting ClientController.", e);
                    }
                });

                Parent root = loader.load();
                currentController = loader.getController();

                Scene currentScene = primaryStage.getScene();
                if (currentScene == null) {
                    currentScene = new Scene(root, 1440, 900);;
                    primaryStage.setScene(currentScene);
                } else {
                    currentScene.setRoot(root);
                    primaryStage.sizeToScene();
                }

                URL cssResource = SceneManager.class.getResource("/css/global.css");
                if (cssResource != null) {
                    String cssUrl = cssResource.toExternalForm();
                    if (!currentScene.getStylesheets().contains(cssUrl)) {
                        currentScene.getStylesheets().add(cssUrl);
                    }
                } else {
                    System.err.println("[GUI WARNING] Style resource not found: /css/global.css");
                }

                primaryStage.show();
                System.out.println("[GUI] Switched to scene: " + viewPath.name());

            } catch (IOException e) {
                System.err.println("[GUI ERROR] Failed to load FXML file: " + viewPath.getPath());
                e.printStackTrace();
            }
        };

        if (Platform.isFxApplicationThread()) {
            // already on JavaFX thread — execute directly to avoid unnecessary queuing
            sceneSwitch.run();
        } else {
            Platform.runLater(sceneSwitch);
        }
    }

    public static ViewController getCurrentController() {
        return currentController;
    }
}
