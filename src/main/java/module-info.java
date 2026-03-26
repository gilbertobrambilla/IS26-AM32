module it.polimi.ingsw.am32 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.databind;


    opens it.polimi.ingsw.am32 to javafx.fxml;
    exports it.polimi.ingsw.am32;
    exports it.polimi.ingsw.am32.enums;
    opens it.polimi.ingsw.am32.enums to javafx.fxml;
}
