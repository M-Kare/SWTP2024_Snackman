package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Direction;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * The ScriptGhost represents a ghost entity in the game that
 * moves autonomously by executing Python scripts. It extends the Mob class
 * and implements Runnable to handle threaded movement logic.
 * Based on the visible squares around its current position, the script decides
 * the next move for the ghost.
 */
public class ScriptGhost extends Mob implements Runnable {

    private static long idCounter = 0;
    private long id;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final Logger log = LoggerFactory.getLogger(ScriptGhost.class);
    private Direction lookingDirection;
    private boolean isWalking;
    private final int WAITING_TIME = 2000;  // in ms
    private int ghostPosX, ghostPosZ;
    // python
    private PythonInterpreter pythonInterpreter = null;
    private final Properties pythonProps = new Properties();
    private ScriptGhostDifficulty difficulty = ScriptGhostDifficulty.EASY;
    private GameMap gameMap;

    public ScriptGhost() {
        super();
        this.difficulty = ScriptGhostDifficulty.EASY;
        this.lookingDirection = Direction.getRandomDirection();
        initJython();
    }

    public ScriptGhost(GameMap gameMap, ScriptGhostDifficulty difficulty) {
        super();
        this.gameMap = gameMap;
        this.difficulty = difficulty;
        this.lookingDirection = Direction.getRandomDirection();
        initJython();
    }

    public ScriptGhost(GameMap gameMap, Square initialPosition, ScriptGhostDifficulty difficulty) {
        this(gameMap, initialPosition);
        //this.difficulty = difficulty;     // todo giving every ghost its own difficulty
    }

    public ScriptGhost(GameMap gameMap, Square initialPosition) {
        super();
        this.difficulty = ScriptGhostDifficulty.getRandomScriptGhostDifficulty();
        this.gameMap = gameMap;
        id = generateId();
        this.ghostPosX = initialPosition.getIndexX();
        this.ghostPosZ = initialPosition.getIndexZ();
        initialPosition.addMob(this);
        this.isWalking = true;
        this.lookingDirection = Direction.getRandomDirection();
        initJython();
    }

    /**
     * @param currentPosition  the square the ghost is standing on top of
     * @param lookingDirection
     * @return a list of 8 square which are around the current square + the
     * direction the ghost is looking in the order:
     * northwest_square, north_square, northeast_square, east_square,
     * southeast_square, south_square, southwest_square, west_square,
     * direction
     */
    public synchronized List<String> getSquaresVisibleForGhost(Square currentPosition, Direction lookingDirection) {
        List<String> squares = new ArrayList<>();
        squares.add(Direction.ONE_NORTH_ONE_WEST.get_one_North_one_West_Square(this.gameMap, currentPosition).getPrimaryTypeForGhost());
        squares.add(Direction.ONE_NORTH.get_one_North_Square(this.gameMap, currentPosition).getPrimaryTypeForGhost());
        squares.add(Direction.ONE_NORTH_ONE_EAST.get_one_North_one_East_Square(this.gameMap, currentPosition).getPrimaryTypeForGhost());
        squares.add(Direction.ONE_EAST.get_one_East_Square(this.gameMap, currentPosition).getPrimaryTypeForGhost());
        squares.add(Direction.ONE_SOUTH_TWO_EAST.get_one_South_one_East_Square(this.gameMap, currentPosition).getPrimaryTypeForGhost());
        squares.add(Direction.ONE_SOUTH.get_one_South_Square(this.gameMap, currentPosition).getPrimaryTypeForGhost());
        squares.add(Direction.ONE_SOUTH_ONE_WEST.get_one_South_one_West_Square(this.gameMap, currentPosition).getPrimaryTypeForGhost());
        squares.add(Direction.ONE_WEST.get_one_West_Square(this.gameMap, currentPosition).getPrimaryTypeForGhost());
        squares.add(lookingDirection.toString());
        return squares;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Method to generate the next id of a new ScriptGhost. It is synchronized because of thread-safety.
     *
     * @return the next incremented id
     */
    protected synchronized static long generateId() {
        return idCounter++;
    }

    /**
     * Contains the movement logic for the ghost. The ghost calculates its next
     * moves and updates its position.
     */
    protected void move() {
        while (isWalking) {
            // get 9 squares
            Square currentPosition = this.gameMap.getSquareAtIndexXZ(this.ghostPosX, this.ghostPosZ);
            List<String> squares = getSquaresVisibleForGhost(currentPosition, lookingDirection);
            log.debug("Squares ghost is seeing: {}", squares);
            log.debug("Current position is x {} z {}", this.ghostPosX, this.ghostPosZ);

            if (standingOnSameSquareAsSnackman()) {
                int newMove = 0;
                if (this.difficulty == ScriptGhostDifficulty.EASY) {
                    newMove = executeMovementSkript(squares);
                } else {
                    List<List<String>> pythonList = new ArrayList<>();
                    for (String[] row : this.gameMap.getStringMap(this.id)) {
                        pythonList.add(Arrays.asList(row));
                    }
                    newMove = executeMovementSkriptDifficult(pythonList);
                }

                if (difficulty == ScriptGhostDifficulty.EASY) {
                    pythonInterpreter.exec("from GhostMovementSkript import choose_next_square");
                } else {
                    pythonInterpreter.exec("from SmartGhostMovementSkript import choose_next_square");
                }

                // set new square to move to
                setNewPosition(newMove);
                log.debug("New position is x {} z {}", this.ghostPosX, this.ghostPosZ);
            }

        }
    }

    private boolean standingOnSameSquareAsSnackman(){
        return this.gameMap.getSquareAtIndexXZ(this.ghostPosX, this.ghostPosZ).getMobs().stream().noneMatch(mob -> mob instanceof SnackMan);
    }

    /**
     * Executes the ghost's movement script written in Python and determines the
     * next move.
     *
     * @param squares a list of squares visible from the ghost's current position.
     * @return the index of the next move resulting from the Python script's execution.
     */
    public int executeMovementSkript(List<String> squares) {
        try {
            log.debug("Running python ghost script with: {}", squares.toString());

            PyObject func = pythonInterpreter.get("choose_next_square");
            PyObject result = func.__call__(new PyList(squares));

            return Integer.parseInt(result.toString());
        } catch (Exception ex) {
            log.error("Error while executing ghost python script: ", ex);
            ex.printStackTrace();
        }
        return 0;
    }

    public int executeMovementSkriptDifficult(List<List<String>> pythonList) {
        try {
            log.debug("Running python ghost script with: {}", pythonList.toString());

            PyObject func = pythonInterpreter.get("choose_next_square");
            PyObject result = func.__call__(new PyList(pythonList));

            return Integer.parseInt(result.toString());
        } catch (Exception ex) {
            log.error("Error while executing ghost python script: ", ex);
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Initializes Jython for executing the chicken's movement script.
     * Sets up the required Python environment and interpreter.
     */
    public void initJython() {
        this.pythonInterpreter = new PythonInterpreter();
        String scriptPath = "";

        try {
            if(this.difficulty == ScriptGhostDifficulty.EASY) {
                scriptPath = Paths.get("extensions/ghost/GhostMovementSkript.py").normalize().toAbsolutePath().toString();
            } else {
                scriptPath = Paths.get("extensions/ghost/SmartGhostMovementSkript.py").normalize().toAbsolutePath().toString();
            }
            log.debug("Resolved script path: {}", scriptPath);

            // Get the directory of the script (without the .)
            String scriptDir = Paths.get(scriptPath).getParent().toString();
            this.pythonInterpreter.exec("import sys");
            this.pythonInterpreter.exec(String.format("sys.path.append('%s')", scriptDir.replace("\\", "\\\\")));
            // Log sys.path to ensure it's correct
            this.pythonInterpreter.exec("import sys; print(sys.path)");
            // Execute the Python script
            this.pythonInterpreter.execfile(scriptPath);

        } catch (Exception ex) {
            log.error("Error initializing {} exception is: ", scriptPath, ex);
        }

        if(this.difficulty == ScriptGhostDifficulty.EASY) {
            this.pythonInterpreter.exec("from GhostMovementSkript import choose_next_square");
        } else {
            this.pythonInterpreter.exec("from SmartGhostMovementSkript import choose_next_square");
        }
    }

    /**
     * Updates the ghost's position based on the chosen move and sets its new
     * direction.
     *
     * @param newMove a list representing the next move for the ghost.
     */
    private void setNewPosition(int newMove) {
        //get positions
        Direction walkingDirection = Direction.getDirection(String.valueOf(newMove));
        log.debug("Walking direction is: {}", walkingDirection);

        this.lookingDirection = walkingDirection;
        Square oldPosition = this.gameMap.getSquareAtIndexXZ(this.ghostPosX, this.ghostPosZ);
        Square newPosition = walkingDirection.getNewPosition(this.gameMap, this.ghostPosX, this.ghostPosZ, walkingDirection);
        propertyChangeSupport.firePropertyChange("scriptGhost", null, this);

        try {
            log.debug("Waiting " + WAITING_TIME + " sec before walking on next square.");
            Thread.sleep(WAITING_TIME);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        // set new position
        this.ghostPosX = newPosition.getIndexX();
        this.ghostPosZ = newPosition.getIndexZ();
        this.setPosX(newPosition.getIndexX());
        this.setPosZ(newPosition.getIndexZ());
        oldPosition.removeMob(this);
        newPosition.addMob(this);
        propertyChangeSupport.firePropertyChange("scriptGhost", null, this);
    }

    /**
     * starts a thread for moving the ghost
     */
    @Override
    public void run() {
        try {
            Thread.sleep(WAITING_TIME);
            move();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void scareSnackMan(SnackMan snackMan) {
        snackMan.loseKcal();
    }

    public long getId() {
        return id;
    }

    public int getGhostPosX() {
        return ghostPosX;
    }

    public int getGhostPosZ() {
        return ghostPosZ;
    }

    public Direction getLookingDirection() {
        return lookingDirection;
    }

    @Override
    public String toString() {
        return "ScriptGhost{" +
                "ghostPosZ=" + ghostPosZ +
                ", ghostPosX=" + ghostPosX +
                ", id=" + id +
                ", lookingDirection=" + lookingDirection +
                '}';
    }
}
