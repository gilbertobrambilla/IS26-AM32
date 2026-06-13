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

public class SceneManager {

    private static Stage primaryStage;
    private static ClientController clientController;
    private static ViewController currentController;

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
                            } catch (NoSuchMethodException ignored) {}
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
                    currentScene = new Scene(root, 1280, 720);
                    primaryStage.setScene(currentScene);

                    currentScene.setOnKeyPressed(event -> {
                        if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                            Platform.exit();
                            System.exit(0);
                        }
                    });
                } else {
                    currentScene.setRoot(root);
                }

                URL cssResource = SceneManager.class.getResource("/css/global.css");
                if (cssResource != null) {
                    String cssUrl = cssResource.toExternalForm();
                    if (!currentScene.getStylesheets().contains(cssUrl)) {
                        currentScene.getStylesheets().add(cssUrl);
                    }
                }

                primaryStage.show();

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

    public static void forceDisconnect(String reason) {
        Runnable disconnectTask = () -> {
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
