package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.gui.controllers.ViewController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Utility class responsible for managing JavaFX scenes and loading FXML files fluently.
 * Implements a completely fluid layout that automatically adapts to any display size.
 */
public class SceneManager {

    private static Stage primaryStage;
    private static ClientController clientController;
    private static ViewController currentController;

    // Larghezza base di riferimento per calcolare la proporzione dei font
    private static final double BASE_WIDTH = 1280.0;

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
                            }
                        }
                        return controllerClass.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName(), e);
                    }
                });

                Parent root = loader.load();
                currentController = loader.getController();

                // 1. Configurazione per consentire il ridimensionamento fluido completo (niente più bande nere)
                if (root instanceof Region) {
                    Region region = (Region) root;
                    region.setMaxWidth(Double.MAX_VALUE);
                    region.setMaxHeight(Double.MAX_VALUE);
                }

                Scene currentScene = primaryStage.getScene();
                if (currentScene == null) {
                    currentScene = new Scene(root);
                    primaryStage.setScene(currentScene);

                    currentScene.setOnKeyPressed(event -> {
                        if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                            GuiLogger.info("[GUI] Uscita forzata tramite tasto ESC");
                            Platform.exit();
                            System.exit(0);
                        }
                    });
                } else {
                    currentScene.setRoot(root);
                }

                // 2. Sistema di Responsive Typography basato sulla larghezza della finestra
                final Parent finalRoot = root;
                Runnable updateFontSize = () -> {
                    double width = primaryStage.getWidth();
                    if (width > 0) {
                        // Calcola un fattore di scala (limitato tra 0.8x e 1.5x per evitare testi microscopici o enormi)
                        double scaleFactor = Math.max(0.8, Math.min(1.5, width / BASE_WIDTH));
                        double calculatedFontSize = 14.0 * scaleFactor;
                        finalRoot.setStyle("-fx-font-size: " + calculatedFontSize + "px;");
                    }
                };

                // Aggiorna il font ogni volta che l'utente ridimensiona la finestra
                primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> updateFontSize.run());
                Platform.runLater(updateFontSize); // Prima applicazione immediata

                // 3. Caricamento del CSS globale
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