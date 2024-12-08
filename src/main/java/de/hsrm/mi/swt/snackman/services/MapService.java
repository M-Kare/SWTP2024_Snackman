package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken.Direction;
import de.hsrm.mi.swt.snackman.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.python.util.PythonInterpreter;

/**
 * Service class for managing the game map
 * This class is responsible for loading and providing access to the game map data
 */
@Service
public class MapService {

    Logger log = LoggerFactory.getLogger(MapService.class);
    private FrontendMessageService frontendMessageService;
    private String filePath;
    private GameMap gameMap;

    /**
     * Constructs a new MapService
     * Initializes the maze data by reading from a file and creates a Map object
     */
    @Autowired
    public MapService(FrontendMessageService frontendMessageService, ReadMazeService readMazeService) {
        this(frontendMessageService, readMazeService, "mini-maze.txt");
    }

    public MapService(FrontendMessageService frontendMessageService, ReadMazeService readMazeService,
                      String filePath) {
        this.frontendMessageService = frontendMessageService;
        //generateNewMaze();
        this.filePath = filePath;
        char[][] mazeData = readMazeService.readMazeFromFile(this.filePath);
        gameMap = convertMazeDataGameMap(mazeData);
        printGameMap();
    }

    /**
     * Converts the char array maze data into MapObjects and populates the game map
     *
     * @param mazeData the char array representing the maze
     */
    private GameMap convertMazeDataGameMap(char[][] mazeData) {
        Square[][] squaresBuildingMap = new Square[mazeData.length][mazeData[0].length];

        for (int x = 0; x < mazeData.length; x++) {
            for (int z = 0; z < mazeData[0].length; z++) {
                try {
                    Square squareToAdd = createSquare(mazeData[x][z], x, z);

                    squaresBuildingMap[x][z] = squareToAdd;

                } catch (IllegalArgumentException e) {
                    log.debug(e.getMessage());
                }
            }
        }

        return new GameMap(squaresBuildingMap);
    }

    /**
     * Generates a new Maze and saves it in a Maze.txt file
     */
    public void generateNewMaze() {
        String path = System.getProperty("user.dir") + "/src/main/java/de/hsrm/mi/swt/snackman/Maze.py";

        //generates a new randome Maze
        try (PythonInterpreter interpreter = new PythonInterpreter()) {
            interpreter.execfile(path);
        }
    }

    /**
     * Creates a Square by given indexes
     *
     * @param symbol from char array
     * @param x      index
     * @param z      index
     * @return a created Square
     */
    private Square createSquare(char symbol, int x, int z) {
        Square square;

        switch (symbol) {
            case '#': {
                square = new Square(MapObjectType.WALL, x, z);
                break;
            }
            case ' ': {
                square = new Square(MapObjectType.FLOOR, x, z);
                addRandomSnackToSquare(square);

                square.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                    if (evt.getPropertyName().equals("square")) {
                        FrontendMessageEvent messageEvent = new FrontendMessageEvent(EventType.SNACK, ChangeType.UPDATE,
                                (Square) evt.getNewValue());

                        frontendMessageService.sendEvent(messageEvent);
                    }
                });
                break;
            }
            case 'C':
                log.debug("Initialising chicken");
                square = new Square(MapObjectType.FLOOR, x, z);
                Chicken newChicken = new Chicken(square, this);
                Thread chickenThread = new Thread(newChicken);
                chickenThread.start();

                newChicken.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                    if (evt.getPropertyName().equals("chicken")) {
                        FrontendChickenMessageEvent messageEvent = new FrontendChickenMessageEvent(EventType.CHICKEN, ChangeType.UPDATE,
                                (Square) evt.getOldValue(), (Square) evt.getNewValue());

                        frontendMessageService.sendChickenEvent(messageEvent);
                    }
                });
                break;
            default: {
                square = new Square(MapObjectType.FLOOR, x, z);
            }
        }

        return square;
    }

    /**
     * @param currentPosition  the square the chicken is standing on top of
     * @param lookingDirection
     * @return a list of 8 square which are around the current square + the direction the chicken is looking in the order:
     * northwest_square, north_square, northeast_square, east_square, southeast_square, south_square, southwest_square, west_square, direction
     */
    public synchronized List<String> getSquaresVisibleForChicken(Square currentPosition, Direction lookingDirection) {
        List<String> squares = new ArrayList<>();
        squares.add(Direction.NORTH_WEST.getNorthWestSquare(this, currentPosition).getPrimaryType());
        log.debug("NORTH_WEST square of chicken: {}", Direction.NORTH_WEST.getNorthWestSquare(this, currentPosition).toString());

        squares.add(Direction.NORTH.getNorthSquare(this, currentPosition).getPrimaryType());
        log.debug("NORTH square of chicken: {}", Direction.NORTH.getNorthWestSquare(this, currentPosition).toString());

        squares.add(Direction.NORTH_EAST.getNorthEastSquare(this, currentPosition).getPrimaryType());
        log.debug("NORTH_EAST square of chicken: {}", Direction.NORTH_EAST.getNorthWestSquare(this, currentPosition).toString());

        squares.add(Direction.EAST.getEastSquare(this, currentPosition).getPrimaryType());
        log.debug("EAST square of chicken: {}", Direction.EAST.getNorthWestSquare(this, currentPosition).toString());

        squares.add(Direction.SOUTH_EAST.getSouthEastSquare(this, currentPosition).getPrimaryType());
        log.debug("SOUTH_EAST square of chicken: {}", Direction.SOUTH_EAST.getNorthWestSquare(this, currentPosition).toString());

        squares.add(Direction.SOUTH.getSouthSquare(this, currentPosition).getPrimaryType());
        log.debug("SOUTH square of chicken: {}", Direction.SOUTH.getNorthWestSquare(this, currentPosition).toString());

        squares.add(Direction.SOUTH_WEST.getSouthWestSquare(this, currentPosition).getPrimaryType());
        log.debug("SOUTH_WEST square of chicken: {}", Direction.SOUTH_WEST.getNorthWestSquare(this, currentPosition));

        squares.add(Direction.WEST.getWestSquare(this, currentPosition).getPrimaryType());
        log.debug("WEST square of chicken: {}", Direction.WEST.getNorthWestSquare(this, currentPosition).toString());

        squares.add(lookingDirection.toString());
        return squares;
    }

    /**
     * Adds a random generated snack inside a square of type FLOOR
     *
     * @param square to put snack in
     */
    private void addRandomSnackToSquare(Square square) {
        if (square.getType() == MapObjectType.FLOOR) {
            SnackType randomSnackType = SnackType.getRandomSnack();

            square.setSnack(new Snack(randomSnackType));
        }
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Square getSquareAtIndexXZ(int x, int z) {
        return gameMap.getSquareAtIndexXZ(x, z);
    }

    public void printGameMap() {
        Square[][] gameMap = this.gameMap.getGameMap();

        for (int x = 0; x < gameMap.length; x++) { //x = Zeilen
            System.out.print("x");
            for (int z = 0; z < gameMap[x].length; z++) { //y = Spalten
                Square square = gameMap[x][z];
                System.out.print(square.getPrimaryType());
            }
            System.out.println("");
        }
    }

}
