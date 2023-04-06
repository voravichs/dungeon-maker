package dungeon.dungeonmaker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * Controller class handling input to the root level GUI.
 * @author bainrow
 */
public class Controller {

    // Instanced variables
    private Dungeon dungeon;
    private ArrayList<Rectangle> rectList;

    @FXML
    private AnchorPane dungeonPane;

    /**
     * Controller constructor that instantiates a dungeon
     * on program start.
     */
    public Controller() {
        this.dungeon = new GridDungeon(dungeonPane,60,30);
        this.rectList = new ArrayList<>();
    }

    /**
     * Shows the current tileMap of the dungeon on the GUI.
     */
    public void showGrid() {
        System.out.println("Showing grid...");
        DungeonTile[][] tileMap = dungeon.getTileMap();
        DungeonRoom[][] dungeonRooms = ((GridDungeon)dungeon).getDungeonRooms();
        // render rooms
        for (int x = 0; x < tileMap.length; x++) {
            for (int y = 0; y < tileMap[x].length; y++) {
                // Create a rectangle
                Rectangle rect = new Rectangle(
                        x * 15,
                        y * 15,
                        15,15);
                // Color it according to occupancy
                if (tileMap[x][y].isOccupied()) {
                    // Create white tile for rooms and corridors
                    rect.setFill(Color.WHITE);
                    rect.setStroke(Color.BLACK);
                } else {
                    // Create black tile for walls
                    rect.setFill(Color.BLACK);
                    rect.setStroke(Color.WHITE);
                }
                // Add it to the pane, save it to list
                dungeonPane.getChildren().add(rect);
                rectList.add(rect);
            }
        }

        // render centers
        for (DungeonRoom[] dungeonRoom : dungeonRooms) {
            for (DungeonRoom room : dungeonRoom) {
                if (room.containsRoom()) {
                    Point2D roomCenter = room.getRoomCenter();
                    // Create a rectangle
                    Rectangle rect = new Rectangle(
                            roomCenter.getX() * 15,
                            roomCenter.getY() * 15,
                            15, 15);
                    // Create red tile
                    rect.setFill(Color.RED);
                    rect.setStroke(Color.BLACK);
                    // Add it to the pane
                    dungeonPane.getChildren().add(rect);
                    rectList.add(rect);
                }
            }
        }
    }

    /**
     * Generates all the rooms in the dungeon.
     */
    public void generateRooms() {
        System.out.println("Generating rooms...");
        GridDungeon gridDungeon = (GridDungeon) this.dungeon;
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
        clearGUI();
        dungeon.initMap();
        showGrid();
    }

    /**
     * Removes all shapes from the dungeon GUI
     */
    public void clearGUI() {
        for (Rectangle rect:
             rectList) {
            dungeonPane.getChildren().remove(rect);
        }
    }
}
