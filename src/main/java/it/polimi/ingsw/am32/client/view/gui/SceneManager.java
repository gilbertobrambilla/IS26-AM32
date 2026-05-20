package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.gui.controllers.ViewController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

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

    public static void switchScene(ViewPaths viewPath) {
        switchScene(viewPath, null);
    }

    public static void switchScene(ViewPaths viewPath, Consumer<ViewController> onLoaded) {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneManager has not been initialized yet.");
        }

        Runnable sceneSwitch = () -> {
            try {
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(viewPath.getPath()));

                loader.setControllerFactory(controllerClass -> {
                    try {
                        if (clientController != null) {
                            try {
                                return controllerClass.getConstructor(ClientController.class).newInstance(clientController);
                            } catch (NoSuchMethodException ignored) {
                                // Some nested controllers are stateless and intentionally expose only a no-arg constructor.
                            }
                        }
                        return controllerClass.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName(), e);
                    }
                });

                Parent root = loader.load();
                currentController = loader.getController();

                Scene currentScene = primaryStage.getScene();
                if (currentScene == null) {
                    currentScene = new Scene(root, 1440, 900);
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
                }

                primaryStage.show();
                GuiLogger.info("[GUI] Switched to scene: " + viewPath.name());

                if (onLoaded != null) {
                    onLoaded.accept(currentController);
                }

            } catch (IOException e) {
                GuiLogger.error("[GUI] Failed to load FXML file: " + viewPath.getPath(), e);
            }
        };

        if (Platform.isFxApplicationThread()) {
            sceneSwitch.run();
        } else {
            Platform.runLater(sceneSwitch);
        }
    }

    /**
     * Instantly destroys the current scene, returns to the Connection view,
     * and shows an error message. Crucial for ungraceful disconnections (resiliency).
     *
     * @param reason the reason for the disconnection.
     */
    public static void forceDisconnect(String reason) {
        Runnable disconnectTask = () -> {
            GuiLogger.error("[GUI] Forced disconnect: " + reason);
            if (clientController != null) {
                clientController.shutdown();
                clientController = null;
            }
            switchScene(ViewPaths.CONNECTION);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Lost");
            alert.setHeaderText("Disconnected from server");
            alert.setContentText(reason);
            alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            disconnectTask.run();
        } else {
            Platform.runLater(disconnectTask);
        }
    }

    public static void setClientController(ClientController controller) {
        clientController = controller;
    }

    public static ViewController getCurrentController() {
        return currentController;
    }
}
