module it.polimi.ingsw.am32 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires org.jline.reader;
    requires org.jline.terminal;
    requires java.desktop;


    exports it.polimi.ingsw.am32.model.enums;
    opens it.polimi.ingsw.am32.model.enums to javafx.fxml;
    exports it.polimi.ingsw.am32.model;
    opens it.polimi.ingsw.am32.model to javafx.fxml;

    exports it.polimi.ingsw.am32.client.view.gui;
    opens it.polimi.ingsw.am32.client.view.gui to javafx.fxml;

    exports it.polimi.ingsw.am32.client.view.gui.controllers;
    opens it.polimi.ingsw.am32.client.view.gui.controllers to javafx.fxml;

    exports it.polimi.ingsw.am32.network.rmi.server;
    opens it.polimi.ingsw.am32.network.rmi.server to java.rmi;

    opens it.polimi.ingsw.am32.client.assets to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.am32.dto.setup to com.fasterxml.jackson.databind;
}
