package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.gui.controllers.ViewController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
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

    // Risoluzione logica di base su cui è disegnata la grafica del gioco
    private static final double LOGICAL_WIDTH = 1280.0;
    private static final double LOGICAL_HEIGHT = 720.0;

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

                // --- SISTEMA LETTERBOX INFALLIBILE ---

                // 1. Blocchiamo il pannello del gioco alle dimensioni native (16:9)
                if (root instanceof Region) {
                    Region region = (Region) root;
                    region.setPrefSize(LOGICAL_WIDTH, LOGICAL_HEIGHT);
                    region.setMinSize(LOGICAL_WIDTH, LOGICAL_HEIGHT);
                    region.setMaxSize(LOGICAL_WIDTH, LOGICAL_HEIGHT);
                }

                // 2. Creiamo lo zoom con punto di ancoraggio fisso in alto a sinistra (0,0)
                Scale scale = new Scale(1, 1, 0, 0);
                root.getTransforms().clear(); // Rimuove eventuali zoom precedenti
                root.getTransforms().add(scale);

                // 3. Congeliamo il layout in un Group
                Group group = new Group(root);

                // 4. Lo StackPane esterno farà da sfondo (bande nere) e centrerà il Group automaticamente
                StackPane wrapper = new StackPane(group);
                wrapper.setStyle("-fx-background-color: #1a1a1a;");

                Scene currentScene = primaryStage.getScene();
                if (currentScene == null) {
                    // Creazione iniziale
                    currentScene = new Scene(wrapper, LOGICAL_WIDTH, LOGICAL_HEIGHT);
                    primaryStage.setScene(currentScene);

                    currentScene.setOnKeyPressed(event -> {
                        if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                            GuiLogger.info("[GUI] Uscita forzata tramite tasto ESC");
                            Platform.exit();
                            System.exit(0);
                        }
                    });
                } else {
                    // Sostituzione scena esistente
                    currentScene.setRoot(wrapper);
                }

                // 5. La formula per aggiornare lo zoom
                Runnable updateScale = () -> {
                    // CHIAVE: Leggiamo la dimensione del WRAPPER (la finestra fisica)
                    double w = wrapper.getWidth();
                    double h = wrapper.getHeight();

                    // Previene crash se la finestra è ridotta a icona
                    if (w <= 0 || h <= 0) return;

                    // Calcola il fattore di scala mantenendo l'aspect ratio (prende il minore)
                    double scaleFactor = Math.min(w / LOGICAL_WIDTH, h / LOGICAL_HEIGHT);
                    scale.setX(scaleFactor);
                    scale.setY(scaleFactor);
                };

                // CHIAVE: Colleghiamo i listener al wrapper, reagendo ai cambi di dimensione
                wrapper.widthProperty().addListener((obs, oldVal, newVal) -> updateScale.run());
                wrapper.heightProperty().addListener((obs, oldVal, newVal) -> updateScale.run());

                // Forza il primo ricalcolo non appena l'interfaccia è pronta
                Platform.runLater(updateScale);
                // -----------------------------------------------------

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