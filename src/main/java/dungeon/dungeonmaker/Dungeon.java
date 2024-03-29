package dungeon.dungeonmaker;

import javafx.scene.layout.Pane;

abstract class Dungeon {

    DungeonTile[][] tileMap;
    Pane dungeonPane;
    int mapSizeX, mapSizeY, tileSize;

    abstract void initMap();

    public DungeonTile[][] getTileMap() {
        return this.tileMap;
    }
}
