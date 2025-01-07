package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.EatingMob;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * Represents a chicken entity in the game, capable of moving around the map,
 * consuming snacks, laying eggs and executing Python-based movement logic
 */
public class Chicken extends EatingMob implements Runnable {

    private static long idCounter = 0;
    private final int WAITING_TIME = 2000;  // in ms
    private final int MAX_CALORIES = 3000;
    private final int CALORIES_PER_SIXTH = (MAX_CALORIES / 6);
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final Logger log = LoggerFactory.getLogger(Chicken.class);
    private long id;
    private int chickenPosX, chickenPosZ;
    private boolean isWalking;
    private boolean blockingPath = false;
    private boolean isScared = false;
    private Direction lookingDirection;
    private Thickness thickness = Thickness.THIN;
    private Timer eggLayingTimer;
    // python
    private PythonInterpreter pythonInterpreter = null;
    private Properties pythonProps = new Properties();

    public Chicken() {
        super(null);
    }

    public Chicken(Square initialPosition, MapService mapService) {
        super(mapService);
        id = generateId();
        this.chickenPosX = initialPosition.getIndexX();
        this.chickenPosZ = initialPosition.getIndexZ();
        initialPosition.addMob(this);

        this.isWalking = true;
        this.lookingDirection = Direction.NORTH;
        initTimer();
    }

    /**
     * Method to generate the next id of a new Square. It is synchronized because of thread-safety.
     *
     * @return the next incremented id
     */
    private synchronized static long generateId() {
        return idCounter++;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * initialises the timer for laying eggs
     */
    private void initTimer() {
        this.eggLayingTimer = new Timer();
        startNewTimer();
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

        this.lookingDirection = walkingDirection;
        Square oldPosition = super.mapService.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);
        Square newPosition = walkingDirection.getNewPosition(super.mapService, this.chickenPosX, this.chickenPosZ, walkingDirection);
        propertyChangeSupport.firePropertyChange("chicken", null, this);

        try {
            Thread.sleep(WAITING_TIME);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        // set new position
        this.chickenPosX = newPosition.getIndexX();
        this.chickenPosZ = newPosition.getIndexZ();
        this.setPosX(newPosition.getIndexX());
        this.setPosZ(newPosition.getIndexZ());
        oldPosition.removeMob(this);
        newPosition.addMob(this);
        propertyChangeSupport.firePropertyChange("chicken", null, this);

    }

    /**
     * Contains the movement logic for the chicken. The chicken calculates its next move,
     * updates its position and consumes any snacks found at its current location.
     */
    protected void move() {
        initJython();
        while (isWalking) {
            // get 9 squares
            Square currentPosition = super.mapService.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);
            List<String> squares = super.mapService.getSquaresVisibleForChicken(currentPosition, lookingDirection);

            if (!blockingPath) {
                List<String> newMove = executeMovementSkript(squares);
                // set new square you move to
                setNewPosition(newMove);
            }

            // consume snack if present
            currentPosition = super.mapService.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);
            if (currentPosition.getSnack() != null && super.getKcal() < MAX_CALORIES
                    && currentPosition.getSnack().getSnackType() != SnackType.EGG) {

                consumeSnackOnSquare();
            }

        }
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     */
    public void consumeSnackOnSquare() {
        Square currentSquare = super.mapService.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);
        Snack snackOnSquare = currentSquare.getSnack();

        if (snackOnSquare != null) {

            try {
                super.gainKcal(snackOnSquare.getCalories());
                //set snack to null after consuming it
                currentSquare.setSnack(null);
                if (super.getKcal() >= this.MAX_CALORIES) {
                    this.thickness = Thickness.VERY_HEAVY;

                    if (mapService.squareIsBetweenWalls(this.chickenPosX, this.chickenPosZ)) {
                        new Thread(() -> {
                            try {
                                blockingPath = true;
                                Thread.sleep(10000);
                                blockingPath = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    layEgg();
                } else {
                    if ((super.getKcal()) <= 2 * CALORIES_PER_SIXTH) {
                        this.thickness = Thickness.THIN;
                    } else if ((super.getKcal()) <= 3 * CALORIES_PER_SIXTH) {
                        this.thickness = Thickness.THIN;
                    } else if ((super.getKcal()) <= 4 * CALORIES_PER_SIXTH) {
                        this.thickness = Thickness.SLIGHTLY_THICK;
                    } else if ((super.getKcal()) <= 5 * CALORIES_PER_SIXTH) {
                        this.thickness = Thickness.MEDIUM;
                    } else if ((super.getKcal()) < this.MAX_CALORIES) {
                        this.thickness = Thickness.HEAVY;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializes Jython for executing the chicken's movement script.
     * Sets up the required Python environment and interpreter.
     */
    public void initJython() {
        pythonProps.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman");
        PythonInterpreter.initialize(System.getProperties(), pythonProps, new String[0]);
        this.pythonInterpreter = new PythonInterpreter();
        pythonInterpreter.exec("from ChickenMovementSkript import choose_next_square");
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
            PyObject func = pythonInterpreter.get("choose_next_square");
            PyObject result = func.__call__(new PyList(squares));

            if (result instanceof PyList) {
                PyList pyList = (PyList) result;
                return convertPythonList(pyList);
            }

            throw new Exception("Python chicken script did not load.");
        } catch (Exception ex) {
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

    public void setBlockingPath(boolean blockingPath) {
        this.blockingPath = blockingPath;
    }

    public Thickness getThickness() {
        return this.thickness;
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
        return this.id;
    }

    public int getChickenPosX() {
        return this.chickenPosX;
    }

    public int getChickenPosZ() {
        return this.chickenPosZ;
    }

    public Direction getLookingDirection() {
        return this.lookingDirection;
    }

    /**
     * Starts a new timer for laying eggs. If the chicken is scared, it adds a delay before starting the timer
     */
    private void startNewTimer() {
        if (eggLayingTimer != null) {
            eggLayingTimer.cancel();
        }
        eggLayingTimer = new Timer();

        TimerTask task = new TimerTask() {
            public void run() {
                layEgg();
            }
        };

        // Random interval between 30 and 60 seconds
        long randomIntervalForLayingANewEgg = new Random().nextInt(30000, 60000);
        long delayBecauseIsScared = 10000;

        if (this.isScared) {
            eggLayingTimer.scheduleAtFixedRate(task, (randomIntervalForLayingANewEgg) + delayBecauseIsScared, randomIntervalForLayingANewEgg);
            this.isScared = false;
        } else {
            this.eggLayingTimer.scheduleAtFixedRate(task, randomIntervalForLayingANewEgg, randomIntervalForLayingANewEgg);
        }
    }

    /**
     * Lays an egg on the current square the chicken is standing on (just if the chicken itself has more than 0 kcal)
     * The calories of the egg are calculated as 1.5 times the current calories of the chicken
     * After laying the egg, the chicken's calories are reset to 0 and its thickness is set to thin
     */
    protected void layEgg() {
        if (super.getKcal() > 0) {
            Square currentSquare = this.mapService.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);

            // new egg with current chicken-calories * 1.5
            int eggCalories = (int) (super.getKcal() * 1.5);
            Snack egg = new Snack(SnackType.EGG);
            egg.setCalories(eggCalories);
            // add egg to current square
            this.mapService.addEggToSquare(currentSquare, egg);
            // Chicken becomes thin again and has no calories after it has laid an egg
            this.setThickness(Thickness.THIN);
            super.setKcal(0);
            startNewTimer();
        } else {
            startNewTimer();
        }
    }

    /**
     * Sets the chicken to be scared and restarts the timer with a delay
     */
    public void getScared() {
        this.isScared = true;
        startNewTimer();
    }

    @Override
    public String toString() {
        return "Chicken{" +
                "thickness=" + thickness +
                ", chickenPosX=" + chickenPosX +
                ", chickenPosZ=" + chickenPosZ +
                ", lookingDirection=" + lookingDirection +
                ", kcal=" + super.getKcal() +
                '}';
    }
}
