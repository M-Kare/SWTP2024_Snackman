package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Direction;
import de.hsrm.mi.swt.snackman.messaging.ChangeType;
import de.hsrm.mi.swt.snackman.messaging.EventType;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageEvent;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * Service class for managing the game map
 * This class is responsible for loading and providing access to the game map data
 */
@Service
public class MapService {

    private FrontendMessageService frontendMessageService;

    Logger log = LoggerFactory.getLogger(MapService.class);
    private String filePath;
    private GameMap gameMap;

    /**
     * Constructs a new MapService
     * Initializes the maze data by reading from a file and creates a Map object
     */
    @Autowired
    public MapService( FrontendMessageService frontendMessageService, ReadMazeService readMazeService) {
        this(frontendMessageService, readMazeService, "mini-maze.txt");
    }

    public MapService(FrontendMessageService frontendMessageService,ReadMazeService readMazeService,
                      String filePath) {
        this.frontendMessageService = frontendMessageService;
        this.filePath = filePath;
        char[][] mazeData = readMazeService.readMazeFromFile(this.filePath);
        gameMap = convertMazeDataGameMap(mazeData);
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
                break;
            }
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
            default: {
                throw new IllegalArgumentException("CAN'T BUILD! " + symbol + " doesn't exist");
            }
        }

        addRandomSnackToSquare(square);

        square.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (evt.getPropertyName().equals("square")) {
                FrontendMessageEvent messageEvent = new FrontendMessageEvent(EventType.SNACK, ChangeType.UPDATE,
                        (Square) evt.getNewValue());

                frontendMessageService.sendEvent(messageEvent);
            }
        });

        return square;
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

    ;


    public GameMap getGameMap() {
        return gameMap;
    }

    public Square getSquareAtIndexXZ(int x, int z) {
        return gameMap.getSquareAtIndexXZ(x, z);
    }

    /**
     * @param currentPosition for which all snacks have been eaten
     */
    public synchronized void deleteConsumedSnacks(Square currentPosition) {
        currentPosition.deleteAllSnacks();
    }

}
