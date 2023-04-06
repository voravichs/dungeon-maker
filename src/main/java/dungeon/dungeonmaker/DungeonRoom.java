package dungeon.dungeonmaker;

import javafx.geometry.Point2D;

public class DungeonRoom {
    private int exits;
    private boolean containsRoom;
    private Point2D roomCenter;
    private Point2D origin;
    private Point2D roomSize;

    public DungeonRoom() {
        exits = 0;
        containsRoom = false;
        roomCenter = new Point2D(0,0);
        origin = new Point2D(0,0);
        roomSize = new Point2D(0,0);
    }

    public int getExits() {
        return this.exits;
    }

    public boolean containsRoom() {
        return this.containsRoom;
    }

    public Point2D getRoomCenter() { return this.roomCenter; }

    public Point2D getOrigin() {
        return origin;
    }

    public int getRoomSizeX() {
        return (int)roomSize.getX();
    }

    public int getRoomSizeY() {
        return (int)roomSize.getY();
    }

    public void setContainsRoom(boolean containsRoom) {
        this.containsRoom = containsRoom;
    }

    public void setRoomCenter(Point2D roomCenter) {
        this.roomCenter = roomCenter;
    }

    public void setOrigin(Point2D origin) {
        this.origin = origin;
    }

    public void setRoomSize(Point2D roomSize) {
        this.roomSize = roomSize;
    }
}
