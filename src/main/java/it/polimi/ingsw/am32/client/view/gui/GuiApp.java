package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * The main entry point for the JavaFX graphical user interface is represented.
 * The primary stage, scene manager, and ui bridge are initialized.
 */
public class GuiApp extends Application {

    private static ClientController pendingController;
    private static String pendingServerAddress;

    /**
     * The client controller to be used by the JavaFX application is stored.
     *
     * @param controller the client controller instance
     */
    public static void setController(ClientController controller) {
        pendingController = controller;
    }

    /**
     * The server address selected before the JavaFX launch is stored.
     *
     * @param address the selected server address
     */
    public static void setServerAddress(String address) {
        pendingServerAddress = address;
    }

    /**
     * The configured server address is returned.
     *
     * @return the configured address, or {@code "unknown"} if unavailable
     */
    public static String getServerAddress() {
        return pendingServerAddress != null ? pendingServerAddress : "unknown";
    }

    /**
     * The JavaFX primary stage is initialized, and the first scene is displayed.
     *
     * @param primaryStage the primary JavaFX stage
     */
    @Override
    public void start(Stage primaryStage) {
        GuiLogger.info("[SYSTEM] Loading game assets");
        try {
            AssetManager.getInstance().loadAssets();
            InputStream dictionaryStream = AssetManager.getInstance().getDictionaryStream("/assets/strings_en.json");
            GameDictionary.getInstance().loadFromJson(dictionaryStream);
        } catch (Exception e) {
            GuiLogger.error("[GUI] Failed to load assets/dictionary", e);
        }

        SceneManager.initialize(primaryStage, null);

        primaryStage.setTitle("Mesos - Digital Edition");

        primaryStage.setResizable(true);
        primaryStage.setFullScreen(false);

        primaryStage.setOnCloseRequest(event -> {
            GuiLogger.info("[GUI] Window close requested");
            System.exit(0);
        });

        SceneManager.switchScene(ViewPaths.CONNECTION);

        primaryStage.centerOnScreen();
    }

    /**
     * A graceful shutdown of GUI-owned services is performed.
     */
    @Override
    public void stop() {
        GuiLogger.info("[GUI] Executing graceful shutdown");
        if (pendingController != null) {
            pendingController.shutdown();
        }
        GuiLogger.info("[GUI] All services stopped");
    }
}
