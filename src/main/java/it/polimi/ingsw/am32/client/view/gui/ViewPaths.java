package it.polimi.ingsw.am32.client.view.gui;

/**
 * Enum representing the paths to the FXML files for each view in the application.
 * This centralizes the management of view paths and makes it easier to maintain and update them.
 */
public enum ViewPaths {
    CONNECTION("/it/polimi/ingsw/am32/connection-view.fxml"),
    LOGIN("/it/polimi/ingsw/am32/login-view.fxml"),
    LOBBY_SELECTION("/it/polimi/ingsw/am32/lobby-selection-view.fxml"),
    WAITING_ROOM("/it/polimi/ingsw/am32/waiting-room-view.fxml"),
    IN_GAME("/it/polimi/ingsw/am32/in-game-view.fxml"),
    END_GAME("/it/polimi/ingsw/am32/end-game-view.fxml");

    private final String path;

    ViewPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
