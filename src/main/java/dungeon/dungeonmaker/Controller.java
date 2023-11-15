package dungeon.dungeonmaker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Controller class handling input to the root level GUI.
 * @author bainrow
 */
public class Controller {

    // static final variables
    public static final int DUNGEON_SIZE_X = 900;

    // Instance variables
    private final Dungeon dungeon;

    @FXML
    private Pane dungeonPane;

    /**
     * Controller constructor that instantiates a dungeon
     * on program start.
     */
    public Controller() {
        this.dungeon = new GridDungeon(dungeonPane,60,30);
    }

    /**
     * Shows the current tileMap of the dungeon on the GUI.
     */
    public void showGrid() {
        clearGrid();
        System.out.println("Showing grid...");
        DungeonTile[][] tileMap = dungeon.getTileMap();

        // render rooms
        for (DungeonTile[] tiles : tileMap) {
            for (DungeonTile tile : tiles) {
                Rectangle currTile = tile.getSquare();
                dungeonPane.getChildren().add(currTile);
            }
        }
    }

    /**
     * Generates all the rooms in the dungeon.
     */
    public void generateRooms() {
        System.out.println("Generating rooms...");
        GridDungeon gridDungeon = (GridDungeon) this.dungeon;
        gridDungeon.initMap();
        gridDungeon.createRooms();
        showGrid();
    }

    /**
     * Generates all the corridors of the dungeon.
     */
    public void generateCorridor() {
        System.out.println("Generating corridors...");
        GridDungeon gridDungeon = (GridDungeon) this.dungeon;
        gridDungeon.connectRooms();
        showGrid();
    }

    public void generateItems(ActionEvent e) {
        System.out.println("Generating items...");
    }

    /**
     * Generates rooms, corridors in dungeon.
     */
    public void generateAll() {
        generateRooms();
        generateCorridor();
        showGrid();
    }

    /**
     * Re-initializes the dungeon and clears the GUI.
     */
    public void clearGrid() {
        System.out.println("Clearing grid...");
        DungeonTile[][] tileMap = dungeon.getTileMap();

        // render rooms
        for (DungeonTile[] tiles : tileMap) {
            for (DungeonTile tile : tiles) {
                dungeonPane.getChildren().remove(tile.getSquare());
            }
        }
    }
}
