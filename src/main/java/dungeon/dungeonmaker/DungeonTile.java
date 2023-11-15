package dungeon.dungeonmaker;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DungeonTile extends FXShape{
    private TileType type;

    public DungeonTile(Rectangle square, Point2D location, TileType type) {
        super(square, location);
        this.type = type;
        setType(type);
        setTilePosition();
    }

    /**
     * Sets the position of the tile in the GUI.
     */
    private void setTilePosition() {
        this.square.setX(this.location.getX());
        this.square.setY(this.location.getY());
    }

    /**
     * @return the TileType of the current tile
     */
    public TileType getType() {
        return type;
    }

    /**
     * Sets the type of the tile, then colors the tile.
     * @param type TileType to set this tile to.
     */
    public void setType(TileType type) {
        this.type = type;
        this.square.setFill(this.type.fill());
        this.square.setStroke(Color.GRAY);
    }
    public enum TileType {
        WALL(Color.BLACK),
        FLOOR(Color.WHITE),
        CENTER(Color.RED);

        private final Color fill;

        TileType(Color fill) {
            this.fill = fill;
        }

        private Color fill() {
            return fill;
        }
    }
}
