package it.polimi.ingsw.am32.client.view.gui;
/**
 * Enum representing the paths to the FXML files for each view in the application.
 * This centralizes the management of view paths and makes it easier to maintain and update them.
 */
public enum ViewPaths {
    LOGIN("/fxml/login-view.fxml"),
    LOBBY("/fxml/lobby-view.fxml"),
    IN_GAME("/fxml/in-game-view.fxml");
    // ... other views
    private final String path;

    ViewPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
