package it.polimi.ingsw.am32.client.view.gui;

/**
 * FXML paths for all GUI views are centralized.
 */
public enum ViewPaths {
    CONNECTION("/it/polimi/ingsw/am32/connection-view.fxml"),
    LOGIN("/it/polimi/ingsw/am32/login-view.fxml"),
    LOBBY_SELECTION("/it/polimi/ingsw/am32/lobby-selection-view.fxml"),
    WAITING_ROOM("/it/polimi/ingsw/am32/waiting-room-view.fxml"),
    IN_GAME("/it/polimi/ingsw/am32/in-game-view.fxml"),
    END_GAME("/it/polimi/ingsw/am32/end-game-view.fxml");

    private final String path;

    /**
     * A view path is stored.
     *
     * @param path the classpath path to the FXML file
     */
    ViewPaths(String path) {
        this.path = path;
    }

    /**
     * The classpath path to the FXML file is returned.
     *
     * @return the fxml classpath path
     */
    public String getPath() {
        return path;
    }
}
