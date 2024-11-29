package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.floor.Floor;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.mapObject.wall.Wall;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing the game map
 * This class is responsible for loading and providing access to the maze data
 */
@Service
public class MapService {

    // TODO map is currently "unnecessary", it will probably be needed as soon as snacks and other items are added
    // private final Map map;

    private String filePath;

    private Square[][] maze;

    private List<MapObject> mapObjects;

    /**
     * Constructs a new MapService
     * Initializes the maze data by reading from a file and creates a Map object
     */
    public MapService() {
        this.filePath = "mini-maze.txt";
        char[][] mazeData = readMazeFromFile(this.filePath);
        this.maze = new Square[mazeData.length][mazeData[0].length];
        switchMazeDataIntoMapObjectsInMaze(mazeData);
    }

    /**
     * Reads maze data from a file and converts it into a char array with [x][z]-coordinates
     *
     * @param filePath the path to the file containing the maze data
     * @return a char array representing the maze
     * @throws RuntimeException if there's an error reading the file
     */
    protected char[][] readMazeFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Lesen der Labyrinth-Datei", e);
        }

        int rows = lines.size();
        int cols = lines.getFirst().length();
        char[][] mazeAsCharArray = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            mazeAsCharArray[i] = lines.get(i).toCharArray();
        }
        return mazeAsCharArray;
    }

    /**
     * Converts the char array maze data into MapObjects and populates the maze
     *
     * @param mazeData the char array representing the maze
     */
    protected void switchMazeDataIntoMapObjectsInMaze(char[][] mazeData) {
        for (int i = 0; i < mazeData.length; i++) {
            for (int j = 0; j < mazeData[0].length; j++) {
                switch (mazeData[i][j]) {
                    case '#':
                        Wall wall = new Wall();
                        this.mapObjects = new ArrayList<>();
                        this.mapObjects.add(wall);
                        this.maze[i][j] = new Square(mapObjects, i, j);
                        break;
                    case ' ':
                        Floor floor = new Floor();
                        this.maze[i][j] = new Square(new ArrayList<>(), i, j);
                        this.mapObjects = new ArrayList<>();
                        this.mapObjects.add(floor);
                        this.maze[i][j] = new Square(mapObjects, i, j);
                        break;
                    // TODO hier weitere mögliche mapObjects hinzufügen mit ihren Zeichen
                    default:
                        System.out.println("CAN'T BUILD! " + mazeData[i][j] + " doesn't exist");
                }
            }
        }
    }

    /**
     * Prepares the maze data for JSON serialization
     *
     * @return a Map containing the maze data in a format suitable for JSON conversion
     */
    public Map<String, Object> prepareMazeForJson() {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (int i = 0; i < this.maze.length; i++) {
            for (int j = 0; j < this.maze[i].length; j++) {
                Map<String, Object> squareInfo = new HashMap<>();
                squareInfo.put("x", j);
                squareInfo.put("z", i);

                if (this.maze[i][j].getMapObjects().getFirst() instanceof Wall) {
                    squareInfo.put("type", MapObjectType.WALL);
                } else if (this.maze[i][j].getMapObjects().getFirst() instanceof Floor) {
                    squareInfo.put("type", MapObjectType.FLOOR);
                }
                mapList.add(squareInfo);
            }
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("map", mapList);
        responseMap.put("height", Wall.DEFAULT_HEIGHT);
        responseMap.put("default-side-length", Square.DEFAULT_SIDE_LENGTH);

        return responseMap;
    }
}
