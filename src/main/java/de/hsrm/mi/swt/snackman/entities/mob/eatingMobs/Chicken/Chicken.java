package de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.EatingMob;
import de.hsrm.mi.swt.snackman.entities.mob.Thickness;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a chicken entity in the game, capable of moving around the map,
 * consuming snacks, and executing Python-based movement logic
 */
public class Chicken extends EatingMob implements Runnable {

    private static long idCounter = 0;
    private long id;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final Logger log = LoggerFactory.getLogger(Chicken.class);
    private boolean blockingPath = false;
    private Thickness thickness = Thickness.THIN;
    private int posX;
    private int posZ;
    private Direction lookingDirection;
    private boolean isWalking;
    private MapService mapService;
    public static final int DEFAULT_HEIGHT = 2;
    private final int WAITING_TIME = 2000;  // in ms
    // python
    private PythonInterpreter pythonInterpreter = null;
    private Properties pythonProps = new Properties();

    public Chicken() {
        super();
    }

    public Chicken(Square initialPosition, MapService mapService) {
        id = generateId();
        this.posX = initialPosition.getIndexX();
        this.posZ = initialPosition.getIndexZ();
        initialPosition.addMob(this);

        this.isWalking = true;
        this.lookingDirection = Direction.NORTH;
        this.mapService = mapService;
        initTimer();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Method to generate the next id of a new Square. It is synchronized because of thread-safety.
     *
     * @return the next incremented id
     */
    private synchronized static long generateId() {
        return idCounter++;
    }

    // initialises the timer for laying eggs
    private void initTimer() {

    }

    /**
     * Updates the chicken's position based on the chosen move and sets its new
     * direction.
     *
     * @param newMove a list representing the next move for the chicken.
     */
    private void setNewPosition(List<String> newMove) {
        //get positions
        Direction walkingDirection = Direction.getDirection(newMove.getLast());
        log.debug("Walking direction is: {}", walkingDirection);

        this.lookingDirection = walkingDirection;
        Square oldPosition = this.mapService.getSquareAtIndexXZ(this.posX, this.posZ);
        Square newPosition = walkingDirection.getNewPosition(this.mapService, this.posX, this.posZ, walkingDirection);
        propertyChangeSupport.firePropertyChange("chicken", null, this);

        try {
            log.debug("Waiting " + WAITING_TIME + " sec before walking on next square.");
            Thread.sleep(WAITING_TIME);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        // set new position
        this.posX = newPosition.getIndexX();
        this.posZ = newPosition.getIndexZ();
        oldPosition.removeMob(this);
        newPosition.addMob(this);
        propertyChangeSupport.firePropertyChange("chicken", null, this);
    }

    /**
     * Contains the movement logic for the chicken. The chicken calculates its next
     * move,
     * updates its position and consumes any snacks found at its current location.
     */
    @Override
    protected void move() {
        initJython();
        while (isWalking) {
            // get 9 squares
            Square currentPosition = this.mapService.getSquareAtIndexXZ(this.posX, this.posZ);
            List<String> squares = this.mapService.getSquaresVisibleForChicken(currentPosition, lookingDirection);
            log.debug("Squares chicken is seeing: {}", squares);

            log.debug("Current position is x {} z {}", this.posX, this.posZ);
            //this.mapService.printGameMap();

            List<String> newMove = executeMovementSkript(squares);

            // set new square you move to
            setNewPosition(newMove);
            log.debug("New position is x {} z {}", this.posX, this.posZ);

            // consume snack if present
            currentPosition = this.mapService.getSquareAtIndexXZ(this.posX, this.posZ);
            if (currentPosition.getSnack() != null) {
                log.debug("Snack being eaten at x {} z {}", this.posX, this.posZ);
                consumeSnackOnSquare();
            }
        }
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     */
    public void consumeSnackOnSquare() {
        Square currentSquare = this.mapService.getSquareAtIndexXZ(this.posX, this.posZ);
        Snack snackOnSquare = currentSquare.getSnack();

        if (snackOnSquare != null) {
            this.kcal += snackOnSquare.getCalories();

            //set snack to null after consuming it
            currentSquare.setSnack(null);
        }
    }

    /**
     * Initializes Jython for executing the chicken's movement script.
     * Sets up the required Python environment and interpreter.
     */
    public void initJython() {
        //pythonProps.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman/entities/mob/eatingMobs/Chicken/ChickenMovementSkript.py");
        pythonProps.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman/entities/mob/eatingMobs/Chicken");
        PythonInterpreter.initialize(System.getProperties(), pythonProps, new String[0]);
        log.debug("Initialised jython for chicken movement");
        this.pythonInterpreter = new PythonInterpreter();
    }

    /**
     * Executes the chicken's movement script written in Python and determines the
     * next move.
     *
     * @param squares a list of squares visible from the chicken's current position.
     * @return a list of moves resulting from the Python script's execution.
     */
    public List<String> executeMovementSkript(List<String> squares) {
        try {
            log.debug("Running python chicken script with: {}", squares.toString());
            pythonInterpreter.exec("from ChickenMovementSkript import choose_next_square");
            PyObject func = pythonInterpreter.get("choose_next_square");
            PyObject result = func.__call__(new PyList(squares));

            if (result instanceof PyList) {
                PyList pyList = (PyList) result;
                log.debug("Python chicken script return: {}", pyList);
                return convertPythonList(pyList);
            }

            throw new Exception("Python chicken script did not load.");
        } catch (Exception ex) {
            log.error("Error while executing chicken python script: ", ex);
            ex.printStackTrace();
        }
        return squares;
    }

    /**
     * Converts a Python list to a Java list.
     *
     * @param pyList the Python list to convert.
     * @return the corresponding Java list.
     */
    private List<String> convertPythonList(PyList pyList) {
        List<String> javaList = new ArrayList<>();
        for (Object item : pyList) {
            javaList.add(item.toString());
        }
        log.debug("Python script result is {}", javaList);
        return javaList;
    }

    /**
     * Adjusts the chicken's thickness state, cycling through predefined values,
     * and updates its path-blocking status accordingly.
     */
    private void incrementThickness() {
        switch (this.thickness) {
            case Thickness.THIN:
                this.thickness = Thickness.SLIGHTLY_THICK;
                break;
            case Thickness.SLIGHTLY_THICK:
                this.thickness = Thickness.MEDIUM;
                break;
            case Thickness.MEDIUM:
                this.thickness = Thickness.HEAVY;
                break;
            case Thickness.HEAVY:
                this.thickness = Thickness.VERY_HEAVY;
                blockingPath = true;
                break;
            case Thickness.VERY_HEAVY:
                this.thickness = Thickness.THIN;
                blockingPath = true;
                break;
        }
    }

    public boolean getBlockingPath() {
        return this.blockingPath;
    }

    public Thickness getThickness() {
        return this.thickness;
    }

    public void setBlockingPath(boolean blockingPath) {
        this.blockingPath = blockingPath;
    }

    public void setThickness(Thickness thickness) {
        this.thickness = thickness;
    }

    /**
     * starts a thread for moving the chicken
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

    public int getPosX() {
        return posX;
    }

    public int getPosZ() {
        return posZ;
    }

    public Direction getLookingDirection() {
        return lookingDirection;
    }

    @Override
    public String toString() {
        return "Chicken{" +
                "thickness=" + thickness +
                ", posX=" + posX +
                ", posZ=" + posZ +
                ", lookingDirection=" + lookingDirection +
                ", kcal=" + kcal +
                '}';
    }
}
