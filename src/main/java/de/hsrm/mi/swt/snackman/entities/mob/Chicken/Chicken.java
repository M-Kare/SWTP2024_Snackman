package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import de.hsrm.mi.swt.snackman.entities.mob.EatingMob;
import de.hsrm.mi.swt.snackman.entities.mob.Thickness;
import de.hsrm.mi.swt.snackman.entities.square.Square;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chicken extends EatingMob implements Runnable{

    private final Logger logger = LoggerFactory.getLogger(Chicken.class);
    private boolean blockingPath;
    private Thickness thickness;
    private Square currentPosition;
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

    public Chicken(Square initialPosition, MapService mapService) {
        this.blockingPath = false;
        this.thickness = Thickness.THIN;
        this.currentPosition = initialPosition;
        this.isWalking = true;
        this.lookingDirection = Direction.NORTH;
        this.mapService = mapService;
        initTimer();
        //move();
    }

    // initialises the timer for laying eggs
    private void initTimer() {

    }

    public List<String> chooseWalkingPath(List<String> currentlyVisibleEnvironment) {
        return executeMovementSkript(currentlyVisibleEnvironment);
    }

    private void setNewPosition(List<String> newMove) {
        Direction walkingDirection = Direction.getDirection(newMove.getLast());
        this.lookingDirection = walkingDirection;
        try {
            logger.info("Wating 1 sec before walking on next square.");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
        }
        this.currentPosition = this.mapService.getNewPosition(this.currentPosition, walkingDirection);
    }

    /**
     * implement moving logic into each chicken
     */
    @Override
    protected void move() {
        initJython();
        while (isWalking) {
            // get 9 squares
            List<String> squares = this.mapService.getSquaresVisibleForChicken(this.currentPosition);
            squares.add(this.lookingDirection.toString());
            List<String> newMove = chooseWalkingPath(squares);
            // set new square you move to
            setNewPosition(newMove);
            // consume snack if present
            if (this.currentPosition.hasSnack()) {
                consumeSnack();
            }
        }
    }

    /**
     * consumes all snacks at the current position
     */
    private void consumeSnack() {
        this.kcal = this.currentPosition.getKcal();
        this.mapService.deleteConsumedSnacks(this.currentPosition);
    }

    /**
     * initialises Jython for running chicken movement script
     */
    protected void initJython() {
        pythonProps.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman/entities/mob/Chicken");
        PythonInterpreter.initialize(System.getProperties(), pythonProps, new String[0]);
        this.pythonInterpreter = new PythonInterpreter();
        logger.info("Initialised jython for chicken movement");
    }

    /**
     * runs the jython chicken movement skript and returns its information
     * 
     * @param squares the squares the chicken can (in its current position) see
     * @return information on where the chicken is going
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
     * Converts a python list into a java list
     * 
     * @param pyList the python list
     * @return the java list
     */
    private List<String> convertPythonList(PyList pyList) {
        List<String> javaList = new ArrayList<>();
        for (Object item : pyList) {
            javaList.add(item.toString());
        }
        logger.info("Python script result is {}", javaList.toString());
        return javaList;
    }

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
                blockingPath = false;
                break;
        }
    }

    /*public void layEgg() {
        MapService.layEgg(currentPosition.getIndexX(), currentPosition.getIndexZ(), generateEgg());
    }

    private Egg generateEgg() {
        Random random = new Random();
        return new Egg((10 + random.nextInt(51)) * 10, ); // kcal between 100 and 600
    }*/

    private long getRandomDelayInSeconds() {
        return MIN_DELAY + (int) (Math.random() * ((MAX_DELAY - MIN_DELAY) + 1));
    }

    public boolean getBlockingPath() {
        return this.blockingPath;
    }

    public Thickness getThickness() {
        return this.thickness;
    }

    public Square getCurrentPosition() {
        return this.currentPosition;
    }

    public void setBlockingPath(boolean blockingPath) {
        this.blockingPath = blockingPath;
    }

    public void setThickness(Thickness thickness) {
        this.thickness = thickness;
    }

    public void setCurrentPosition(Square currentPosition) {
        this.currentPosition = currentPosition;
    }

    /**
     * @todo zurück auskommentieren
     */
    @Override
    public void run() {
        //move();    
    }

}
