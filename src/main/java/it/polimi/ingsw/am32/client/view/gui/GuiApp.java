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
 * the main entry point for the JavaFX graphical user interface.
 * it sets up the primary stage, initializes the SceneManager, and creates the UI bridge.
 */
public class GuiApp extends Application {

    /**
     * temporary static reference used to pass the controller from ClientMain
     * to the JavaFX application thread.
     */
    private static ClientController pendingController;


    private static String pendingServerAddress;


    /**
     * sets the ClientController before launching the JavaFX application.
     * this must be called before Application.launch().
     *
     * @param controller the main ClientController instantiated in ClientMain.
     */
    public static void setController(ClientController controller) {
        pendingController = controller;
    }


    public static void setServerAddress(String address) {
        pendingServerAddress = address;
    }

    public static String getServerAddress() {
        return pendingServerAddress != null ? pendingServerAddress : "unknown";
    }


    /**
     * the main entry point for all JavaFX applications.
     * the start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
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

        primaryStage.setOnCloseRequest(event -> {
            GuiLogger.info("[GUI] Window close requested");
            System.exit(0);
        });

        SceneManager.switchScene(ViewPaths.CONNECTION);
    }

    /**
     *  Called when the application is about to stop, either by user action or programmatically.
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
