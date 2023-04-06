package dungeon.dungeonmaker;

public class DungeonTile {
    private boolean occupied;

    public DungeonTile() {
        this.occupied = false;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isOccupied() {
        return this.occupied;
    }
}
