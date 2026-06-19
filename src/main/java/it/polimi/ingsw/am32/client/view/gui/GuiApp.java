package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;
import java.util.Objects;

/**
 * The main entry point for the JavaFX graphical user interface is represented.
 * The primary stage, scene manager, and ui bridge are initialized.
 */
public class GuiApp extends Application {

    private static String pendingServerAddress;

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

        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(GuiApp.class.getResourceAsStream("/assets/images/ui_assets/app_icon.png"))
        ));

        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {

                    java.awt.Image awtImage = ImageIO.read(
                            Objects.requireNonNull(GuiApp.class.getResourceAsStream("/assets/images/ui_assets/app_icon.png"))
                    );
                    taskbar.setIconImage(awtImage);
                }
            }
        } catch (Exception e) {
            GuiLogger.error("[GUI] Failed to set the system taskbar icon", e);
        }

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

    /**
     * A graceful shutdown of GUI-owned services is performed.
     */
    @Override
    public void stop() {
        GuiLogger.info("[GUI] JavaFX application stopped");
    }
}
