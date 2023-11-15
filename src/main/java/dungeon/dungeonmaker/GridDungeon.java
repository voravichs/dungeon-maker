package dungeon.dungeonmaker;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

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
    private final DungeonRoom[] dungeonRooms;
    private final DungeonRoom[][] dungeonRoomGrid;
    private int totalRooms;
    private Point2D startingRoom;
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
        this.tileSize = Controller.DUNGEON_SIZE_X / x;
        this.dungeonPane = dungeonPane;
        dungeonRooms = new DungeonRoom[8];
        dungeonRoomGrid = new DungeonRoom[4][2];
        initMap();
    }

    /**
     * Initializes the dungeon map with empty tiles and rooms.
     */
    @Override
    public void initMap() {
        // Initialize the tileMap
        for (int x = 0; x < mapSizeX; x++) {
            for (int y = 0; y < mapSizeY; y++) {
                tileMap[x][y] = new DungeonTile(
                        new Rectangle(tileSize,tileSize),
                        new Point2D(x * tileSize, y * tileSize),
                        DungeonTile.TileType.WALL);
            }
        }

        // Initialize the rooms
        for (int i = 0; i < 8; i++) {
            dungeonRooms[i] = new DungeonRoom();
        }
    }

    /**
     * Populates the tileMap with 3-7 rooms.
     * Decides randomly which rooms in the dungeonRooms array
     * to populate, then calls generateRoom for each of those rooms.
     */
    public void createRooms() {
        // Random number of rooms 3-7
        int randomNumRooms = (int)(Math.random() * 4 + 4);
        totalRooms = randomNumRooms;

        // Randomly select the rooms to generate
        List<Integer> randInts = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            randInts.add(i);
        }
        Collections.shuffle(randInts);

        // Generate the rooms
        for (int i = 0; i < randomNumRooms; i++) {
            int currentRoom = randInts.get(i);
            generateRoom(currentRoom);
        }

        // Add centers for the missing rooms
        for (int i = randomNumRooms; i < 8; i++) {
            int currRoom = randInts.get(i);
            Point2D center;
            int roomXOffset, roomYOffset = 0;
            if (currRoom % 2 != 0) {
                roomXOffset = (currRoom - 1) / 2;
                roomYOffset = roomSectionY;
            } else {
                roomXOffset = currRoom / 2;
            }

            // Set the center
            center = new Point2D(
                    (int) ((roomSectionX * roomXOffset) + (double) (roomSectionX / 2)),
                    (int) (roomYOffset + (double) roomSectionY / 2));
            // Store center of room
            dungeonRooms[currRoom].setRoomCenter(center);
            tileMap[(int) center.getX()][(int) center.getY()].setType(DungeonTile.TileType.CENTER);
        }

        // transform dungeon rooms into a 4x2
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                dungeonRoomGrid[i / 2][0] = dungeonRooms[i];
                dungeonRoomGrid[i / 2][0].setRoomIndex(new Point2D((double) i / 2, 0));
            } else {
                dungeonRoomGrid[(i - 1) / 2][1] = dungeonRooms[i];
                dungeonRoomGrid[(i - 1) / 2][1].setRoomIndex(new Point2D((double) (i - 1) / 2, 1));
            }
        }

        // Set starting room
        int startingRoomNum = randInts.get(0);
        if (startingRoomNum % 2 == 0) {
            startingRoom = new Point2D((double) startingRoomNum / 2, 0);
        } else {
            startingRoom = new Point2D((double) (startingRoomNum - 1) / 2, 1);
        }

        // Create linkedList of rooms
        Node[][] visited = new Node[4][2];
        linkedDungeonRooms = construct(dungeonRoomGrid, 0, 0, 4, 2, visited);
    }

    /**
     * Generates a room at the given roomNum.
     * @param roomNum the number room in dungeonRooms
     *                to generate.
     */
    private void generateRoom(int roomNum) {
        // Set x/y offset according to the room number
        int roomXOffset, roomYOffset = 0;
        if (roomNum % 2 != 0) {
            roomXOffset = (roomNum - 1) / 2;
            roomYOffset = roomSectionY;
        } else {
            roomXOffset = roomNum / 2;
        }

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

        // Set the center
        Point2D center = new Point2D(
                (roomSectionX * roomXOffset) + (int)(origin.getX() + randomXSize / 2),
                roomYOffset + (int)(origin.getY() + randomYSize / 2));

        // Store center of room, set containsRoom, and set room index
        dungeonRooms[roomNum].setRoomCenter(center);
        dungeonRooms[roomNum].setContainsRoom(true);

        // Fill the coords of the room graphically
        for (int x = 0; x < randomXSize; x++) {
            for (int y = 0; y < randomYSize; y++) {
                int xCoord = (roomSectionX * roomXOffset) + (int)origin.getX() + x;
                int yCoord = (int)origin.getY() + y + roomYOffset;
                // if center, instead it as center
                if (xCoord == center.getX() && yCoord == center.getY()) {
                    tileMap[xCoord][yCoord].setType(DungeonTile.TileType.CENTER);
                } else {
                    tileMap[xCoord][yCoord].setType(DungeonTile.TileType.FLOOR);
                }
            }
        }
    }

    public void connectRooms() {
        // Create a list of connected nodes
        ArrayList<Node> connectedNodes = new ArrayList<>();
        Queue<Node> roomQueue = new LinkedList<>();
        ArrayList<Pair<Node, Node>> connections = new ArrayList<>();
        int totalRoomsConnected = 1;

        // Find the room node associated with the starting room
        System.out.println(startingRoom);
        Node startRoomNode = linkedDungeonRooms;
        for (int x = 0; x < startingRoom.getX(); x++) {
            startRoomNode = startRoomNode.right;
        }
        for (int y = 0; y < startingRoom.getY(); y++) {
            startRoomNode = startRoomNode.down;
        }

        // Starting from the start room, continually find adj rooms
        connectedNodes.add(startRoomNode);
        roomQueue.add(startRoomNode);
        while(!roomQueue.isEmpty()) {
            Node currNode = roomQueue.remove();
            int roomsConnected = findAdjRooms(currNode, connectedNodes, roomQueue, connections,false);
            totalRoomsConnected += roomsConnected;
            if(roomsConnected == 0 && totalRoomsConnected < totalRooms) {
                findAdjRooms(currNode, connectedNodes, roomQueue, connections, true);
            }
        }
        // Remove connections that go nowhere
        // Put connections incoming to non-rooms in a HashMap
        HashMap<Node, Integer> numNonRoomIncoming = new HashMap<>();
        ArrayList<Node> incomingNodes = new ArrayList<>();
        for (Pair<Node, Node> connection: connections) {
            Node end = connection.getValue();
            if (!end.data.containsRoom()) {
                if (numNonRoomIncoming.containsKey(end)) {
                    int prev = numNonRoomIncoming.get(end);
                    numNonRoomIncoming.put(end, prev + 1);
                } else {
                    numNonRoomIncoming.put(end, 1);
                }
            }
            incomingNodes.add(connection.getKey());
        }
        // Delete any connections that are dead ends in the HashMap
        ArrayList<Point2D> remove = new ArrayList<>();
        for (Node nonRoomPoint: numNonRoomIncoming.keySet()) {
            if (!incomingNodes.contains(nonRoomPoint)) {
                remove.add(nonRoomPoint.data.getRoomCenter());
            }
        }

        // Check if
        if (totalRoomsConnected < totalRooms) {
            System.out.println("fuck");
        }

        for (Pair<Node, Node> connection: connections) {
            Point2D start = connection.getKey().data.getRoomCenter();
            Point2D end = connection.getValue().data.getRoomCenter();
            if (remove.contains(end)) {
                continue;
            }
            BFS(start, end);
        }
    }

    private int findAdjRooms(
            Node root,
            ArrayList<Node> connectedNodes,
            Queue<Node> roomQueue,
            ArrayList<Pair<Node, Node>> connections,
            boolean emptyAllowed) {
        boolean foundRoom = false;
        int roomsConnected = 0;

        // Either bias leftward or rightward depending on room index
        ArrayList<Node> adjNodes = new ArrayList<>();
        if (root.data.getRoomIndex().getX() < 2) {
            adjNodes.addAll(Arrays.asList(
                    root.up, root.right, root.down, root.left));
        } else {
            adjNodes.addAll(Arrays.asList(
                    root.up, root.left, root.down, root.right));
        }

        if (!emptyAllowed) {
            // Go through all the adjNodes and add adj rooms
            for (Node currAdjNode: adjNodes) {
                // check that the node is not out of bounds
                if (currAdjNode != null) {
                    // If not on the list of current connected nodes, add it and return
                    if (!connectedNodes.contains(currAdjNode) && (currAdjNode.data.containsRoom())) {
                        connectedNodes.add(currAdjNode);
                        roomQueue.add(currAdjNode);
                        connections.add(new Pair<>(root, currAdjNode));
                        roomsConnected++;
                    }
                }
            }
        } else {
            // Go through enough adjNodes to find one empty room
            for (Node currAdjNode: adjNodes) {
                // check that the node is not out of bounds
                if (currAdjNode != null) {
                    // If not on the list of current connected nodes, add it and return
                    if (!connectedNodes.contains(currAdjNode)) {
                        connectedNodes.add(currAdjNode);
                        roomQueue.add(currAdjNode);
                        connections.add(new Pair<>(root, currAdjNode));
                        return 0;
                    }
                }
            }
        }

        return roomsConnected;
    }

//    private void connectHorizontalRooms(Node dungeonNode) {
//        if (dungeonNode == null) {
//            return;
//        }
//        if (dungeonNode.data.containsRoom()) {
//            Node endingNode = findNextRoomHorizontal(dungeonNode);
//            if (endingNode != null) {
//                BFS(dungeonNode.data.getRoomCenter(),
//                        endingNode.data.getRoomCenter());
//            }
//
//        }
//        connectHorizontalRooms(dungeonNode.next);
//    }

//    private Node findNextRoomHorizontal(Node currNode) {
//        if (currNode.next == null) {
//            return null;
//        }
//        if (currNode.next.data.containsRoom()) {
//            return currNode.next;
//        } else {
//            return findNextRoomHorizontal(currNode.next);
//        }
//    }

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
        if (currTile.getType() != DungeonTile.TileType.FLOOR) {
            currTile.setType(DungeonTile.TileType.FLOOR);
        }
        traceCorridor(pred, currPoint);
    }

    public DungeonRoom[] getDungeonRooms() {
        return this.dungeonRooms;
    }

    // node of linked list
    static class Node {
        DungeonRoom data;
        Node right;
        Node down;
        Node up;
        Node left;
    };

    // returns head pointer of linked list
    // constructed from 2D matrix
    static Node construct(DungeonRoom[][] arr, int i, int j, int m,
                          int n, Node[][] visited)
    {
        // return if i or j is out of bounds
        if (i > m - 1 || j > n - 1 || i < 0 || j < 0)
            return null;
        // Check if node is previously created then,
        // don't need to create new/
        if (visited[i][j] != null) {
            return visited[i][j];
        }
        // create a new node for current i and j
        // and recursively allocate its down and
        // right pointers
        Node temp = new Node();
        visited[i][j] = temp;
        temp.data = arr[i][j];
        temp.right = construct(arr, i + 1, j, m, n, visited);
        temp.left = construct(arr, i - 1, j, m, n, visited);
        temp.down = construct(arr, i, j + 1, m, n, visited);
        temp.up = construct(arr, i, j - 1, m, n, visited);

        return temp;
    }
}
