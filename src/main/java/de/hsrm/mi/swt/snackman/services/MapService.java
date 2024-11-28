package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.MapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.MapObject.floor.Floor;
import de.hsrm.mi.swt.snackman.entities.MapObject.wall.Wall;
import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Direction;

import de.hsrm.mi.swt.snackman.entities.square.Square;

import org.python.modules.synchronize;
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

    private String filePath;

    private Square[][] maze;

    private List<MapObject> mapObjects;

    private List<Chicken> allChickens;

    /**
     * Constructs a new MapService
     * Initializes the maze data by reading from a file and creates a Map object
     */
    public MapService() {
        this.filePath = "mini-maze.txt";
        char[][] mazeData = readMazeFromFile(this.filePath);
        initialiseMaze(mazeData);
        switchMazeDataIntoMapObjectsInMaze(mazeData);
    }

    /**
     * Reads maze data from a file and converts it into a char array with
     * [x][z]-coordinates
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
     * Set the position x and y for each square in the maze
     * 
     * @param mazeData the maze data
     */
    private void initialiseMaze(char[][] mazeData) {
        this.maze = new Square[mazeData.length][mazeData[0].length];

        for (int i = 0; i < mazeData.length; i++) {
            for (int j = 0; j < mazeData[0].length; j++) {
                this.maze[i][j] = new Square(i, j);
            }
        }
    }

    /**
     *
     * Gives back the new square-position of the chicken
     * 
     * @param currentChickenPosition the current position of the chicken
     * @param direction              in which the chicken decided to go
     * @return the square which is laying in the direction of the currentPosition
     */
    public synchronized Square getNewPosition(Square currentChickenPosition, Direction direction) {
        switch (direction) {
            case Direction.NORTH:
                return this.maze[currentChickenPosition.getIndexX()][currentChickenPosition.getIndexZ() - 1];
            case Direction.EAST:
                return this.maze[currentChickenPosition.getIndexX() + 1][currentChickenPosition.getIndexZ()];
            case Direction.SOUTH:
                return this.maze[currentChickenPosition.getIndexX()][currentChickenPosition.getIndexZ() + 1];
            case Direction.WEST:
                return this.maze[currentChickenPosition.getIndexX() - 1][currentChickenPosition.getIndexZ()];
            default:
                return null;
        }
    }

    /**
     * Converts the char array maze data into MapObjects and populates the maze
     *
     * @param mazeData the char array representing the maze
     */
    protected void switchMazeDataIntoMapObjectsInMaze(char[][] mazeData) {
        this.allChickens = new ArrayList<>();

        for (int i = 0; i < mazeData.length; i++) {
            for (int j = 0; j < mazeData[0].length; j++) {
                switch (mazeData[i][j]) {
                    case '#':
                        Wall wall = new Wall();
                        this.mapObjects = new ArrayList<>();
                        this.mapObjects.add(wall);
                        this.maze[i][j].setMapObjects(mapObjects);
                        break;
                    case ' ':
                        Floor floor = new Floor();
                        this.mapObjects = new ArrayList<>();
                        this.mapObjects.add(floor);
                        this.maze[i][j].setMapObjects(mapObjects);
                        break;
                    // TODO hier weitere mögliche mapObjects hinzufügen mit ihren Zeichen
                    case 'C':
                        Chicken newChicken = new Chicken(this.maze[i][j], this);
                        this.allChickens.add(newChicken);
                        Thread chickenThread = new Thread(newChicken);
                        chickenThread.start();
                        //set floor under chicken
                        Floor floor2 = new Floor();
                        this.mapObjects = new ArrayList<>();
                        this.mapObjects.add(floor2);
                        this.maze[i][j].setMapObjects(mapObjects);
                        break;
                    default:
                        System.out.println("CAN'T BUILD! " + mazeData[i][j] + " doesn't exist");
                }
            }
        }
    }

    /**
     *
     * @param currentPosition the square the chicken is standing on top of
     * @return a list of 8 square which are around the current square
     */
    public synchronized List<String> getSquaresVisibleForChicken(Square currentPosition) {
        List<String> squares = new ArrayList<>();
        // northwest_square, north_square, northeast_square, east_square,
        // southeast_square, south_square, southwest_square, west_square, direction
        squares.add(this.maze[currentPosition.getIndexX() - 1][currentPosition.getIndexZ() - 1].getPrimaryType());
        squares.add(this.maze[currentPosition.getIndexX()][currentPosition.getIndexZ() - 1].getPrimaryType());
        squares.add(this.maze[currentPosition.getIndexX() + 1][currentPosition.getIndexZ() + 1].getPrimaryType());
        squares.add(this.maze[currentPosition.getIndexX() + 1][currentPosition.getIndexZ()].getPrimaryType());
        squares.add(this.maze[currentPosition.getIndexX() + 1][currentPosition.getIndexZ() + 1].getPrimaryType());
        squares.add(this.maze[currentPosition.getIndexX()][currentPosition.getIndexZ() + 1].getPrimaryType());
        squares.add(this.maze[currentPosition.getIndexX() - 1][currentPosition.getIndexZ() + 1].getPrimaryType());
        squares.add(this.maze[currentPosition.getIndexX() - 1][currentPosition.getIndexZ()].getPrimaryType());
        return squares;
    }

    /**
     * Prepares the maze data for JSON serialization
     *
     * @return a Map containing the maze data in a format suitable for JSON
     *         conversion
     */
    public Map<String, Object> prepareMazeForJson() {
        List<Map<String, Object>> mapList = new ArrayList<>();

        //add not moving map objects
        for (int i = 0; i < this.maze.length; i++) {
            for (int j = 0; j < this.maze[i].length; j++) {
                Map<String, Object> squareInfo = new HashMap<>();
                squareInfo.put("x", j);
                squareInfo.put("z", i);

                if (this.maze[i][j].getMapObjects().getFirst() instanceof Wall) {
                    squareInfo.put("type", "wall");
                } else if (this.maze[i][j].getMapObjects().getFirst() instanceof Floor) {
                    squareInfo.put("type", "floor");
                }
                mapList.add(squareInfo);
            }
        }

        //add chicken to json
        for (Chicken chicken : this.allChickens) {
            Map<String, Object> chickenInfo = new HashMap<>();
            chickenInfo.put("x", String.valueOf(chicken.getCurrentPosition().getIndexX()));
            chickenInfo.put("z", String.valueOf(chicken.getCurrentPosition().getIndexZ()));
            chickenInfo.put("type", "chicken");
            mapList.add(chickenInfo);
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("map", mapList);
        responseMap.put("height", Wall.DEFAULT_HEIGHT);
        responseMap.put("default-side-length", Square.DEFAULT_SIDE_LENGTH);

        return responseMap;
    }

    /**
     * @param currentPosition for which all snacks have been eaten
     */
    public synchronized void deleteConsumedSnacks(Square currentPosition) {
        currentPosition.deleteAllSnacks();
    }

}
