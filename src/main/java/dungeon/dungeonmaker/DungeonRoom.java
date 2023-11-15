package dungeon.dungeonmaker;

import javafx.geometry.Point2D;

public class DungeonRoom {
    private boolean containsRoom;
    private Point2D roomCenter;
    private Point2D roomIndex;

    public DungeonRoom() {
        containsRoom = false;
        roomCenter = new Point2D(0,0);
    }


    public Point2D getRoomCenter() { return this.roomCenter; }

    public Point2D getRoomIndex() {
        return roomIndex;
    }

    public boolean containsRoom() {
        return this.containsRoom;
    }

    public void setContainsRoom(boolean containsRoom) {
        this.containsRoom = containsRoom;
    }

    public void setRoomCenter(Point2D roomCenter) {
        this.roomCenter = roomCenter;
    }

    public void setRoomIndex(Point2D roomIndex) {
        this.roomIndex = roomIndex;
    }
}
