package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Direction;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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

    public ScriptGhost() {
        super(null);
    }

    public ScriptGhost(MapService mapService, ScriptGhostDifficulty difficulty) {
        super(mapService);
        this.difficulty = difficulty;
    }

    public ScriptGhost(MapService mapService, Square initialPosition, ScriptGhostDifficulty difficulty) {
        this(mapService, initialPosition);
        this.difficulty = difficulty;
    }

    public ScriptGhost(MapService mapService, Square initialPosition) {
        super(mapService);
        id = generateId();
        this.ghostPosX = initialPosition.getIndexX();
        this.ghostPosZ = initialPosition.getIndexZ();
        initialPosition.addMob(this);

        this.isWalking = true;
        this.lookingDirection = Direction.NORTH;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Method to generate the next id of a new ScriptGhost. It is synchronized because of thread-safety.
     *
     * @return the next incremented id
     */
    private synchronized static long generateId() {
        return idCounter++;
    }

    /**
     * Contains the movement logic for the ghost. The ghost calculates its next
     * moves and updates its position.
     */
    protected void move() {
        initJython();
        while (isWalking) {
            // get 9 squares
            Square currentPosition = super.mapService.getSquareAtIndexXZ(this.ghostPosX, this.ghostPosZ);
            List<String> squares = super.mapService.getSquaresVisibleForGhost(currentPosition, lookingDirection);
            log.debug("Squares ghost is seeing: {}", squares);

            log.debug("Current position is x {} z {}", this.ghostPosX, this.ghostPosZ);

            int newMove = 0;
            if(this.difficulty == ScriptGhostDifficulty.EASY) {
                newMove = executeMovementSkript(squares);
            } else {
                List<List<String>> pythonList = new ArrayList<>();
                for (String[] row : mapService.getGameMap().getStringMap(this.id)) {
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

            PyObject func = pythonInterpreter.get("choose_next_move");
            PyObject result = func.__call__(new PyList(pythonList));

            return Integer.parseInt(result.toString());
        } catch (Exception ex) {
            log.error("Error while executing ghost python script: ", ex);
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Initializes Jython for executing the ghost's movement script.
     * Sets up the required Python environment and interpreter.
     */
    public void initJython() {
        pythonProps.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman");
        PythonInterpreter.initialize(System.getProperties(), pythonProps, new String[0]);
        log.info("Initialising jython for ghost movement");
        this.pythonInterpreter = new PythonInterpreter();
        pythonInterpreter.exec("from GhostMovementSkript import choose_next_square");
        pythonInterpreter.exec("from SmartGhostMovementSkript import choose_next_move");
    }

    /**
     * Updates the ghost's position based on the chosen move and sets its new
     * direction.
     *
     * @param newMove a list representing the next move for the ghost.
     */
    private void setNewPosition(int newMove) {
        //get positions
        Direction walkingDirection = Direction.getDirection(newMove);
        log.debug("Walking direction is: {}", walkingDirection);

        this.lookingDirection = walkingDirection;
        Square oldPosition = super.mapService.getSquareAtIndexXZ(this.ghostPosX, this.ghostPosZ);
        Square newPosition = walkingDirection.getNewPosition(super.mapService, this.ghostPosX, this.ghostPosZ, walkingDirection);
        propertyChangeSupport.firePropertyChange("scriptGhost", null, this);      // todo update in frontend + init in backend

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
        propertyChangeSupport.firePropertyChange("scriptGhost", null, this);      // todo update in frontend + init in backend
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
