package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing the game map
 * This class is responsible for loading and providing access to the game map data
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
    @Autowired
    public MapService(ReadMazeService readMazeService) {
        this(readMazeService, "mini-maze.txt");
    }

    public MapService(ReadMazeService readMazeService, String filePath) {
        this.filePath = filePath;
        char[][] mazeData = readMazeService.readMazeFromFile(this.filePath);
        gameMap = convertMazeDataGameMap(mazeData);
    }


    /**
     * Converts the char array maze data into MapObjects and populates the game map
     *
     * @param mazeData the char array representing the maze
     */
    private GameMap convertMazeDataGameMap(char[][] mazeData) {
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

    /**
     * Creates a Square by given indexes
     *
     * @param symbol from char array
     * @param x index
     * @param z index
     * @return a created Square
     */
    private Square createSquare(char symbol, int x, int z) {
        return switch (symbol) {
            case '#' -> new Square(MapObjectType.WALL, x, z);
            case ' ' -> new Square(MapObjectType.FLOOR, x, z);
            // TODO weitere Fälle hinzufügen
            default -> throw new IllegalArgumentException("CAN'T BUILD! " + symbol + " doesn't exist");
        };
    }


    /**
     * Adds a random generated snack inside a square of type FLOOR
     * @param square to put snack in
     */
    private void addRandomSnackToSquare(Square square) {
        if(square.getType() == MapObjectType.FLOOR){
            SnackType randomSnackType = SnackType.getRandomSnack();

            square.setSnack(new Snack(randomSnackType));
        }
    };


    public GameMap getGameMap() {
        return gameMap;
    }
}
