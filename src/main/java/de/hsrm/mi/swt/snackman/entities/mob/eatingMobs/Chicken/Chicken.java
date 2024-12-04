package de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken;

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

    private final Logger logger = LoggerFactory.getLogger(Chicken.class);
    private boolean blockingPath = false;
    private Thickness thickness = Thickness.THIN;
    private int posX;
    private int posZ;
    private Direction lookingDirection;
    private final int MIN_DELAY = 30;
    private final int MAX_DELAY = 30;
    private boolean isWalking;
    private MapService mapService;
    public static final int DEFAULT_HEIGHT = 2;
    // python
    private PythonInterpreter pythonInterpreter = null;
    private Properties pythonProps = new Properties();

    public Chicken() {
        super();
    }

    //@todo add PropertyChangeSupport -> siehe setSnack() in Square.java

    public Chicken(Square initialPosition, MapService mapService) {
        this.posX = initialPosition.getIndexX();
        this.posZ = initialPosition.getIndexZ();
        initialPosition.addMob(this);

        this.isWalking = true;
        this.lookingDirection = Direction.NORTH;
        this.mapService = mapService;
        initTimer();
    }

    // initialises the timer for laying eggs
    private void initTimer() {

    }

    /**
     * Chooses the next walking path based on the chicken's visible environment.
     *
     * @param currentlyVisibleEnvironment a list of information about the squares
     *                                    visible around the chicken (8 squares).
     * @return a list of directions or moves determined by the chicken's movement
     *         script.
     */
    public List<String> chooseWalkingPath(List<String> currentlyVisibleEnvironment) {
        return executeMovementSkript(currentlyVisibleEnvironment);
    }

    /**
     * Updates the chicken's position based on the chosen move and sets its new
     * direction.
     *
     * @param newMove a list representing the next move for the chicken.
     */
    private void setNewPosition(List<String> newMove) {
        Direction walkingDirection = Direction.getDirection(newMove.getLast());
        this.lookingDirection = walkingDirection;
        try {
            logger.info("Wating 1 sec before walking on next square.");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
        }
        Square oldPosition = this.mapService.getSquareAtIndexXZ(this.posX, this.posZ);
        Square newPosition = this.mapService.getNewPosition(this.posX, this.posZ, walkingDirection);
        oldPosition.removeMob(this);
        newPosition.addMob(this);
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
            List<String> squares = this.mapService.getSquaresVisibleForChicken(currentPosition);
            squares.add(this.lookingDirection.toString());
            List<String> newMove = chooseWalkingPath(squares);
            // set new square you move to
            setNewPosition(newMove);
            // consume snack if present
            if (currentPosition.getSnack() != null) {
                consumeSnackOnSquare();
            }
        }
    }

    /**
     * Consumes all snacks at the chicken's current position, updating the chicken's
     * calorie count and removing consumed snacks from the map.
    private void consumeSnack() {
        Square currentPosition = this.mapService.getSquareAtIndexXZ(this.posX, this.posZ);
        this.kcal = currentPosition.getKcal();
        this.mapService.deleteConsumedSnacks(this.currentPosition);
    }*/

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     */
    public void consumeSnackOnSquare(){
        Square currentSquare = this.mapService.getSquareAtIndexXZ(this.posX, this.posZ);
        Snack snackOnSquare = currentSquare.getSnack();

        if(snackOnSquare != null){
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
        pythonProps.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman/entities/mob/Chicken");
        PythonInterpreter.initialize(System.getProperties(), pythonProps, new String[0]);
        this.pythonInterpreter = new PythonInterpreter();
        logger.info("Initialised jython for chicken movement");
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
            logger.info("Running python chicken script with: {}", squares.toString());
            pythonInterpreter.exec("from ChickenMovementSkript import choose_next_square");
            PyObject func = pythonInterpreter.get("choose_next_square");
            PyObject result = func.__call__(new PyList(squares));

            if (result instanceof PyList) {
                PyList pyList = (PyList) result;
                return convertPythonList(pyList);
            }

            throw new Exception("Python chicken script did not load.");
        } catch (Exception ex) {
            logger.error("Error while executing chicken python script: ", ex);
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
        logger.info("Python script result is {}", javaList.toString());
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
     * @todo zurück auskommentieren, damit chicken sich bewegen kann ->
     *       voraussetzung dafür: es muss global der state des gesammten backends an
     *       das frontend geschickt werden!!
     */
    @Override
    public void run() {
        // move();
    }

}
