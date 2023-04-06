package dungeon.dungeonmaker;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

import java.util.*;

/**
 * Represents a dungeon made of a 4x2 grid of rooms
 * connected by corridors.
 * @author bainrow
 */
public class GridDungeon extends Dungeon {

    // Direction vectors
    static int[] dRow = { -1, 0, 1, 0 };
    static int[] dCol = { 0, 1, 0, -1 };

    // Instance variables
    private final DungeonRoom[][] dungeonRooms;
    private Node linkedDungeonRooms;

    // Derived values from mapSize
    private final int roomSectionX;
    private final int roomSectionY;

    /**
     * Constructor to instantiate a new GridDungeon
     * @param dungeonPane layout pane in which the dungeon
     *                    will be displayed on the GUI.
     * @param x the width of the dungeon
     * @param y the height of the dungeon
     */
    public GridDungeon(Pane dungeonPane, int x, int y) {
        this.tileMap = new DungeonTile[x][y];
        this.mapSizeX = x;
        this.mapSizeY = y;
        this.roomSectionX = x/4;
        this.roomSectionY = y/2;
        this.dungeonPane = dungeonPane;
        dungeonRooms = new DungeonRoom[4][2];
        initMap();
    }

    @Override
    public void initMap() {
        // Initialize the tileMap
        for (int x = 0; x < mapSizeX; x++) {
            for (int y = 0; y < mapSizeY; y++) {
                tileMap[x][y] = new DungeonTile();
            }
        }

        // Initialize the rooms
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                dungeonRooms[i][j] = new DungeonRoom();
            }
        }
    }

    /**
     * Populates the tileMap with 3-7 rooms.
     * Decides randomly which rooms in the dungeonRooms array
     * to populate, then calls generateRoom for each of those rooms.
     */
    public void createRooms() {
        // Random number of rooms 3-7
        int randomNumRooms = (int)(Math.random() * 5 + 3);

        // Randomly select the rooms to generate
        List<Integer> randInts = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            randInts.add(i);
        }
        Collections.shuffle(randInts);

        // Generate the rooms twice for interesting
        for (int i = 0; i < randomNumRooms; i++) {
            int currentRoom = randInts.get(i);
            generateRoom(currentRoom);
            generateRoom(currentRoom);
        }

        // Create data structure to store the rooms
        createLinkedRooms();

        // Set centers for empty rooms
        for (int roomNum = randomNumRooms; roomNum < 8; roomNum++) {
            if (roomNum <= 3) {
                Point2D center = new Point2D(
                        (roomSectionX * roomNum) + roomSectionX / 2,
                        roomSectionY / 2);
                dungeonRooms[roomNum][0].setContainsRoom(true);
                dungeonRooms[roomNum][0].setRoomCenter(center);
            } else {
                Point2D center = new Point2D(
                        (roomSectionX * roomNum) + roomSectionX / 2,
                        roomSectionY / 2);
            }
        }
    }

    /**
     * Generates a room at the given roomNum.
     * @param roomNum the number room in dungeonRooms
     *                to generate.
     */
    private void generateRoom(int roomNum) {
        // Get a random x/y size between 5-10 blocks for the room
        int randomXSize = (int)(Math.random() * 7 + 5);
        int randomYSize = (int)(Math.random() * 7 + 5);

        // Get the max coordinate at which a room may start generating
        Point2D max = new Point2D(
                roomSectionX - 1 - randomXSize,
                roomSectionY - 1 - randomYSize);
        // From the max, randomly decide the origin of the room
        Point2D origin = new Point2D(
                (int)(Math.random() * max.getX() + 1),
                (int)(Math.random() * max.getY() + 1));

        // Room nums 0-3 are top
        Point2D center;
        if (roomNum <= 3) {
            // Set the center
            center = new Point2D(
                    (roomSectionX * roomNum) + (int)(origin.getX() + randomXSize / 2),
                    (int)(origin.getY() + randomYSize / 2));

            // Only first room generation stores the center
            if (!dungeonRooms[roomNum][0].containsRoom()) {
                dungeonRooms[roomNum][0].setContainsRoom(true);
                dungeonRooms[roomNum][0].setRoomCenter(center);
            }

            for (int x = 0; x < randomXSize; x++) {
                for (int y = 0; y < randomYSize; y++) {
                    int xCoord = (roomSectionX * roomNum) + (int)origin.getX() + x;
                    int yCoord = (int)origin.getY() + y;
                    tileMap[xCoord][yCoord].setOccupied(true);
                }
            }
        }
        // room nums 4-7 are bottom
        else {
            center = new Point2D(
                    (roomSectionX * (roomNum - 4)) + (int)(origin.getX() + randomXSize / 2),
                    roomSectionY + (int)(origin.getY() + randomYSize / 2));

            if (!dungeonRooms[roomNum - 4][1].containsRoom()) {
                dungeonRooms[roomNum - 4][1].setContainsRoom(true);
                dungeonRooms[roomNum - 4][1].setRoomCenter(center);
            }

            for (int x = 0; x < randomXSize; x++) {
                for (int y = 0; y < randomYSize; y++) {
                    int xCoord = (roomSectionX * (roomNum - 4)) + (int)origin.getX() + x;
                    int yCoord = roomSectionY + (int)origin.getY() + y;
                    tileMap[xCoord][yCoord].setOccupied(true);
                }
            }
        }
    }

    /**
     * Creates a doubly linked list on a 2D array that
     * connects each room to their adjacent rooms.
     */
    private void createLinkedRooms() {
        // function call for construct
        // the doubly linked list
        linkedDungeonRooms = constructDoublyListUtil(this.dungeonRooms, 0, 0, null);
    }

    public void connectRooms() {
        // Connect rooms horizontally, taking in the top and bottom nodes
        connectHorizontalRooms(linkedDungeonRooms);
        connectHorizontalRooms(linkedDungeonRooms.down);
    }

    private void connectHorizontalRooms(Node dungeonNode) {
        if (dungeonNode == null) {
            return;
        }
        if (dungeonNode.data.containsRoom()) {
            Node endingNode = findNextRoomHorizontal(dungeonNode);
            if (endingNode != null) {
                BFS(dungeonNode.data.getRoomCenter(),
                        endingNode.data.getRoomCenter());
            }

        }
        connectHorizontalRooms(dungeonNode.next);
    }

    private Node findNextRoomHorizontal(Node currNode) {
        if (currNode.next == null) {
            return null;
        }
        if (currNode.next.data.containsRoom()) {
            return currNode.next;
        } else {
            return findNextRoomHorizontal(currNode.next);
        }
    }

    private void BFS(Point2D start, Point2D end) {
        Queue<Point2D> coordQueue = new LinkedList<>();
        boolean[][] visited = new boolean[this.mapSizeX][this.mapSizeY];
        Point2D[][] pred = new Point2D[this.mapSizeX][this.mapSizeY];

        coordQueue.add(start);

        boolean endReached = false;
        while (!coordQueue.isEmpty()) {
            Point2D currCoord = coordQueue.poll();
            Point2D closestCoord = new Point2D(0,0);
            double shortestDist = Double.POSITIVE_INFINITY;

            for(int i = 0; i < 4; i++) {
                int adjX = (int)currCoord.getX() + dRow[i];
                int adjY = (int)currCoord.getY() + dCol[i];
                Point2D adj = new Point2D(adjX, adjY);

                double distanceToEnd = compareDistance(adj, end);
                if (distanceToEnd == 0) {
                    endReached = true;
                    break;
                }
                if (distanceToEnd < shortestDist) {
                    shortestDist = distanceToEnd;
                    closestCoord = adj;
                }

                if (isValid(visited, adjX, adjY)) {
                    visited[adjX][adjY] = true;
                }
            }
            if (endReached) {
                pred[(int)end.getX()][(int)end.getY()]
                        = currCoord;
                break;
            }

            coordQueue.add(closestCoord);
            pred[(int)closestCoord.getX()][(int)closestCoord.getY()]
                    = currCoord;
            visited[(int)currCoord.getX()][(int)currCoord.getY()]
                    = true;
        }
        traceCorridor(pred, end);
    }

    private boolean isValid(boolean[][] visited, int x, int y) {
        if (x < 0 || y < 0 ||
            x >= this.mapSizeX || y >= this.mapSizeY) {
            return false;
        }

        if (visited[x][y]) {
            return false;
        }

        return true;
    }

    private double compareDistance(Point2D start, Point2D end) {
        return Math.sqrt(
                Math.pow((end.getX() - start.getX()), 2) +
                Math.pow((end.getY() - start.getY()), 2));
    }

    private void traceCorridor(Point2D[][] pred, Point2D end) {
        Point2D currPoint = pred[(int)end.getX()][(int)end.getY()];
        if (currPoint == null) {
            return;
        }
        DungeonTile currTile = tileMap[(int) currPoint.getX()][(int) currPoint.getY()];
        if (!currTile.isOccupied()) {
            currTile.setOccupied(true);
        }
        traceCorridor(pred, currPoint);
    }

    public DungeonRoom[][] getDungeonRooms() {
        return this.dungeonRooms;
    }

    // struct node of doubly linked
    // list with four pointer
    // next, prev, up, down
    static class Node {
        DungeonRoom data;
        Node next;
        Node prev;
        Node up;
        Node down;
    }

    // function to create a new node
    static Node createNode(DungeonRoom data) {
        Node temp = new Node();
        temp.data = data;
        temp.next = null;
        temp.prev = null;
        temp.up = null;
        temp.down = null;
        return temp;
    }

    // function to construct the
    // doubly linked list
    static Node constructDoublyListUtil(DungeonRoom[][] mtrx, int i, int j, Node curr) {
        if (i >= 4 || j >= 2) {
            return null;
        }

        // Create Node with value contain
        // in matrix at index (i, j)
        Node temp = createNode(mtrx[i][j]);

        // Assign address of curr into
        // the prev pointer of temp
        temp.prev = curr;

        // Assign address of curr into
        // the up pointer of temp
        temp.up = curr;

        // Recursive call for next pointer
        temp.next
                = constructDoublyListUtil(mtrx, i + 1, j, temp);

        // Recursive call for down pointer
        temp.down= constructDoublyListUtil(mtrx, i, j + 1, temp);

        // Return newly constructed node
        // who's all four node connected
        // at it's appropriate position
        return temp;

    }
}
