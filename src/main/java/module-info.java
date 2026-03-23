module it.polimi.ingsw.am32 {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.polimi.ingsw.am32 to javafx.fxml;
    exports it.polimi.ingsw.am32;
}