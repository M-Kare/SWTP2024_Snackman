package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Direction;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.messaging.*;

import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
    private char[][] mazeData;
    private PythonInterpreter pythonInterpreter = null;
    private Properties pythonProps = new Properties();
    private SnackMan snackman;
    private ReadMazeService readMazeService;

    /**
     * Constructs a new MapService
     * Initializes the maze data by reading from a file and creates a Map object
     */
    @Autowired
    public MapService(FrontendMessageService frontendMessageService, ReadMazeService readMazeService) {
        this(frontendMessageService, readMazeService, "./extensions/map/Maze.txt");
    }

    public MapService(FrontendMessageService frontendMessageService, ReadMazeService readMazeService,
                      String filePath) {
        this.frontendMessageService = frontendMessageService;
        this.filePath = filePath;
        this.readMazeService = readMazeService;

        generateNewMaze();

        this.mazeData = readMazeService.readMazeFromFile(this.filePath);
        if (this.mazeData == null) {
            throw new IllegalStateException("Maze data cannot be null. Check your ReadMazeService.");
        }

        gameMap = convertMazeDataGameMap(this.mazeData);
        snackman = new SnackMan(this, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS);

        snackman.addPropertyChangeListener(event -> {
            if (event.getPropertyName().equals("currentCalories")) {
                int newCalories = (int) event.getNewValue();
        
                if (newCalories >= 3000) {
                    String message = newCalories == snackman.getMAXKCAL() ? "Maximum calories reached!" : "";
                    FrontendMessageCaloriesEvent messageEvent = new FrontendMessageCaloriesEvent(
                        EventType.CALORIES,
                        ChangeType.UPDATE,
                        newCalories,
                        message
                    );
                    frontendMessageService.sendUpdateCaloriesEvent(messageEvent);
                } else {
                    // Normale Kalorienaktualisierung
                    String message = "";
                    FrontendMessageCaloriesEvent messageEvent = new FrontendMessageCaloriesEvent(EventType.CALORIES, ChangeType.UPDATE, newCalories, message);
                    
                    frontendMessageService.sendUpdateCaloriesEvent(messageEvent);
                }
            }
        });

    }

    /**
     * Converts the char array maze data into MapObjects and populates the game map
     *
     * @param mazeData the char array representing the maze
     */
    public GameMap convertMazeDataGameMap(char[][] mazeData) {
        Square[][] squaresBuildingMap = new Square[mazeData.length][mazeData[0].length];

        for (int x = 0; x < mazeData.length; x++) {
            for (int z = 0; z < mazeData[0].length; z++) {
                try {
                    Square squareToAdd = createSquare(mazeData[x][z], x, z);

                    squaresBuildingMap[x][z] = squareToAdd;
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage());
                }
            }
        }

        return new GameMap(squaresBuildingMap);
    }

    //TODO Maze.py map größe als Argumente herein reichen statt in der python-file selbst zu hinterlegen

    /**
     * Generates a new Maze and saves it in a Maze.txt file
     */
    public void generateNewMaze() {
        String mazeScriptPath = "./extensions/maze/Maze.py";
        try (PythonInterpreter localPythonInterpreter = new PythonInterpreter()) {
            localPythonInterpreter.execfile(mazeScriptPath);
            localPythonInterpreter.exec("main()");
        } catch (Exception e) {
            log.error("Failed to execute maze generation script", e);
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
            case '#':
                square = new Square(MapObjectType.WALL, x, z);
                break;
            case ' ':
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
            case 'C':
                log.debug("Initialising chicken");
                square = new Square(MapObjectType.FLOOR, x, z);
                Chicken newChicken = new Chicken(square, this);
                Thread chickenThread = new Thread(newChicken);
                chickenThread.start();

                newChicken.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                    if (evt.getPropertyName().equals("chicken")) {
                        FrontendChickenMessageEvent messageEvent = new FrontendChickenMessageEvent(EventType.CHICKEN,
                                ChangeType.UPDATE, (Chicken) evt.getNewValue());

                        frontendMessageService.sendChickenEvent(messageEvent);
                    } else if (evt.getPropertyName().equals("egg")) {
                        Square squareToAddEgg = (Square) evt.getNewValue();
                        if (squareToAddEgg != null && squareToAddEgg.getSnack() != null && squareToAddEgg.getSnack().getSnackType() == SnackType.EGG) {
                            FrontendMessageEvent messageEvent = new FrontendMessageEvent(EventType.SNACK, ChangeType.CREATE, squareToAddEgg);
                            frontendMessageService.sendEvent(messageEvent);
                            log.info("Sending FrontendMessageEvent: EventType={}, ChangeType={}, SquareId={}",
                                    messageEvent.eventType().name(), messageEvent.changeType().name(), messageEvent.square().getId());
                        }
                    }
                });
                break;
            default:
                square = new Square(MapObjectType.FLOOR, x, z);
        }
        return square;
    }

    /**
     * @param currentPosition  the square the chicken is standing on top of
     * @param lookingDirection
     * @return a list of 8 square which are around the current square + the
     * direction the chicken is looking in the order:
     * northwest_square, north_square, northeast_square, east_square,
     * southeast_square, south_square, southwest_square, west_square,
     * direction
     */
    public synchronized List<String> getSquaresVisibleForChicken(Square currentPosition, Direction lookingDirection) {
        List<String> squares = new ArrayList<>();

        squares.add(Direction.TWO_NORTH_TWO_WEST.get_two_North_two_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_NORTH_ONE_WEST.get_two_North_one_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_NORTH.get_two_North_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_NORTH_ONE_EAST.get_two_North_one_East_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_NORTH_TWO_EAST.get_two_North_two_East_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH_TWO_WEST.get_one_North_two_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH_ONE_WEST.get_one_North_one_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH.get_one_North_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH_ONE_EAST.get_one_North_one_East_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH_TWO_EAST.get_one_North_two_East_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_WEST.get_two_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_WEST.get_one_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.CHICKEN.get_Chicken_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_EAST.get_one_East_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_EAST.get_two_East_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_SOUTH_TWO_WEST.get_one_South_two_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_SOUTH_ONE_WEST.get_one_South_one_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_SOUTH.get_one_South_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_SOUTH_ONE_EAST.get_one_South_one_East_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_SOUTH_TWO_EAST.get_one_South_two_East_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH_TWO_WEST.get_two_South_two_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH_ONE_WEST.get_two_South_one_West_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH.get_two_South_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH_ONE_EAST.get_two_South_one_East_Square(this, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH_TWO_EAST.get_two_South_two_East_Square(this, currentPosition).getPrimaryType());
        squares.add(lookingDirection.toString());

        return squares;
    }

    /**
     * Adds a random generated snack inside a square of type FLOOR
     *
     * @param square to put snack in
     */
    public void addRandomSnackToSquare(Square square) {
        if (square.getType() == MapObjectType.FLOOR) {
            SnackType randomSnackType = SnackType.getRandomSnack();

            square.setSnack(new Snack(randomSnackType));
        }
    }

    /**
     * Adds a laid egg to a specified square on the map
     *
     * @param square  The square where the egg is to be added
     * @param laidEgg The snack representing the egg that has been laid
     */
    public void addEggToSquare(Square square, Snack laidEgg) {
        square.setSnack(laidEgg);
        log.debug("{} kcal egg add to square {} and square {}", laidEgg.getCalories(), square.getId(), square.getId());
        frontendMessageService.sendEvent(new FrontendMessageEvent(EventType.SNACK, ChangeType.CREATE, square));
    }

    public void printGameMap() {
        Square[][] gameMap = this.gameMap.getGameMap();

        for (int x = 0; x < gameMap.length; x++) {
            log.info("x");
            for (int z = 0; z < gameMap[x].length; z++) {
                Square square = gameMap[x][z];
                log.info(square.getPrimaryType());
            }
            log.info("");
        }
    }

    public Square getSquareAtIndexXZ(int x, int z) {
        return gameMap.getSquareAtIndexXZ(x, z);
    }

    public boolean squareIsBetweenWalls(int x, int z){
        Square squareAbove = this.gameMap.getSquareAtIndexXZ(x - 1, z);
        Square squareBelow = this.gameMap.getSquareAtIndexXZ(x + 1, z);
        Square squareRight = this.gameMap.getSquareAtIndexXZ(x, z + 1);
        Square squareLeft = this.gameMap.getSquareAtIndexXZ(x, z - 1);

        if((squareAbove.getType() == MapObjectType.WALL) && (squareBelow.getType() == MapObjectType.WALL)){
            return true;
        }

        if((squareRight.getType() == MapObjectType.WALL) && (squareLeft.getType() == MapObjectType.WALL)){
            return true;
        }

        return false;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public SnackMan getSnackMan() {
        return snackman;
    }

    public void setSquare(Square square, int x, int y){
        gameMap.setGameMap(square, x, y);
    }

    public String getFilePath(){
        return this.filePath;
    }
}
