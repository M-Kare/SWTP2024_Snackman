package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing the game map
 * This class is responsible for loading and providing access to the maze data
 */
@Service
public class MapService {
    private String filePath;
    private GameMap gameMap;
    Logger log = LoggerFactory.getLogger(MapService.class);

    /**
     * Constructs a new MapService
     * Initializes the maze data by reading from a file and creates a Map object
     */
    public MapService() {
        //String pathToScript = getClass().getClassLoader().getResource("Maze.py").getPath();
        //PythonInterpreter interpreter = new PythonInterpreter();
        //interpreter.execfile("./Maze.py");
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "./Maze.py");
            pb.directory(new File("./Maze.py"));
            Process process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.filePath = "Maze.txt";
        char[][] mazeData = readMazeFromFile(this.filePath);
        gameMap = switchMazeDataIntoMapObjectsInMaze(mazeData);
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
            throw new RuntimeException("Fehler beim Lesen der Maze-Datei", e);
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
    private GameMap switchMazeDataIntoMapObjectsInMaze(char[][] mazeData) {
        Square[][] squaresBuildingMap = new Square[mazeData.length][mazeData[0].length];

        for (int i = 0; i < mazeData.length; i++) {
            for (int j = 0; j < mazeData[0].length; j++) {
                try {
                    Square squareToAdd = createSquare(mazeData[i][j], i, j);
                    addRandomSnackToSquare(squareToAdd);

                    squaresBuildingMap[i][j] = squareToAdd;

                } catch (IllegalArgumentException e) {
                    log.debug(e.getMessage());
                }
            }
        }

        return new GameMap(squaresBuildingMap);
    }

    //TODO add Javadoc
    private Square createSquare(char symbol, int x, int z) {
        return switch (symbol) {
            case '#' -> new Square(MapObjectType.WALL, x, z);
            case ' ' -> new Square(MapObjectType.FLOOR, x, z);
            // TODO weitere Fälle hinzufügen
            default -> throw new IllegalArgumentException("CAN'T BUILD! " + symbol + " doesn't exist");
        };
    }


    //TODO add Javadoc
    private void addRandomSnackToSquare(Square square) {
        SnackType randomSnackType = SnackType.getRandomSnack();

        square.addSnack(new Snack(randomSnackType));
    };


    public GameMap getGameMap() {
        return gameMap;
    }
}
