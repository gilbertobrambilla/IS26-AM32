package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

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
        if (pendingController == null) {
            System.err.println("[GUI ERROR] ClientController was not set before launching GuiApp.");
            Platform.exit();
            System.exit(1);
        }

        // initialize the UI bridge (links the controller's callbacks to JavaFX)
        GuiUI guiUI = new GuiUI(pendingController);

        // initialize the SceneManager with the stage and controller
        SceneManager.initialize(primaryStage, pendingController);

        // configure the primary stage (the OS window)
        primaryStage.setTitle("Mesos - Digital Edition");
        primaryStage.setResizable(false); // Fixed size to maintain layout consistency during early development

        // ensure all network threads and the JVM shut down cleanly when the window is closed
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("[GUI] Window close requested.");
            Platform.exit();
        });

        SceneManager.switchScene(ViewPaths.LOGIN);
    }

    /**
     *  Called when the application is about to stop, either by user action or programmatically.
     */
    @Override
    public void stop() {
        System.out.println("[GUI] Executing graceful shutdown...");
        if (pendingController != null) {
            pendingController.shutdown();
        }
        System.out.println("[GUI] All services stopped. Goodbye.");
    }
}
