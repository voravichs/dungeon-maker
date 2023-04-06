module dungeon.dungeonmaker {
    requires javafx.controls;
    requires javafx.fxml;


    opens dungeon.dungeonmaker to javafx.fxml;
    exports dungeon.dungeonmaker;
}