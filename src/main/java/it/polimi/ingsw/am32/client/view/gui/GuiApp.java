package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * The main entry point for the JavaFX graphical user interface is represented.
 * The primary stage, scene manager, and ui bridge are initialized.
 */
public class GuiApp extends Application {

    private static String pendingServerAddress;

    public static void setServerAddress(String address) {
        pendingServerAddress = address;
    }

    public static String getServerAddress() {
        return pendingServerAddress != null ? pendingServerAddress : "unknown";
    }

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
            event.consume();
            SceneManager.exitApplication();
        });

        SceneManager.switchScene(ViewPaths.CONNECTION);

        primaryStage.centerOnScreen();
    }

    @Override
    public void stop() {
        GuiLogger.info("[GUI] JavaFX application stopped");
    }
}
