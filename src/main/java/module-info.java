module it.polimi.ingsw.am32 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;


    opens it.polimi.ingsw.am32 to javafx.fxml;
    exports it.polimi.ingsw.am32;
    exports it.polimi.ingsw.am32.model.enums;
    opens it.polimi.ingsw.am32.model.enums to javafx.fxml;
    exports it.polimi.ingsw.am32.model;
    opens it.polimi.ingsw.am32.model to javafx.fxml;
}
