package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.EatingMob;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.hsrm.mi.swt.snackman.configuration.GameConfig;

/**
 * Represents a chicken entity in the game, capable of moving around the map,
 * consuming snacks, laying eggs and executing Python-based movement logic
 */
public class Chicken extends EatingMob implements Runnable {

    private static long idCounter = 0;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final Logger log = LoggerFactory.getLogger(Chicken.class);
    //private final int WAITING_TIME = GameConfig.WAITING_TIME;  // in ms
    private int waitingTime;
    private final int MAX_CALORIES = GameConfig.MAX_KALORIEN;
    private final int CALORIES_PER_SIXTH = (MAX_CALORIES / 6);
    private long id;
    private Thickness thickness = Thickness.THIN;
    private int chickenPosX, chickenPosZ;
    private Direction lookingDirection;
    private boolean timerRestarted = false;
    private boolean isWalking;
    private boolean blockingPath = false;
    private boolean isScared = false;
    private Timer eggLayingTimer;
    // python
    private PythonInterpreter pythonInterpreter = null;
    private Properties pythonProps = new Properties();
    private String fileName;
    private GameMap gameMap;

    public Chicken() {
        super();
        initJython();
        this.fileName = "ChickenMovementSkript";
    }

    public Chicken(String fileName) {
        super();
        this.fileName = fileName;
        initJython();
    }

    public Chicken(Square initialPosition, GameMap gameMap, String fileName) {
        super();
        id = generateId();
        this.gameMap = gameMap;
        this.chickenPosX = initialPosition.getIndexX();
        this.chickenPosZ = initialPosition.getIndexZ();
        initialPosition.addMob(this);
        this.fileName = fileName;
        this.isWalking = true;
        this.lookingDirection = Direction.getRandomDirection();
        log.debug("Chicken looking direction is {}", lookingDirection);
        initJython();
        initTimer();
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
     * Method to generate the next id of a new Chicken. It is synchronized because of thread-safety.
     *
     * @return the next incremented id
     */
    protected synchronized static long generateId() {
        return idCounter++;
    }

    /**
     * Adds a {@link PropertyChangeListener} to this object.
     * The listener will be notified whenever a bound property changes.
     *
     * @param listener the {@link PropertyChangeListener} to be added
     */
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
    private void setNewPosition(int newMove) {
        //get positions
        Direction walkingDirection = Direction.getDirection(newMove);
        log.debug("Walking direction is: {}", walkingDirection);

        this.lookingDirection = walkingDirection;
        Square oldPosition = this.gameMap.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);
        Square newPosition = walkingDirection.getNewPosition(this.gameMap, this.chickenPosX, this.chickenPosZ,
                walkingDirection);
        propertyChangeSupport.firePropertyChange("chicken", null, this);

        try {
            log.debug("Waiting " + waitingTime + " sec before walking on next square.");
            Thread.sleep(waitingTime);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
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
        //initJython();
        while (isWalking) {
            // get 9 squares
            Square currentPosition = this.gameMap.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);
            List<String> squares = getSquaresVisibleForChicken(this.gameMap, currentPosition, lookingDirection);
            log.debug("Squares chicken is seeing: {}", squares);

            if (!blockingPath) {
                log.debug("Current position is x {} z {}", this.chickenPosX, this.chickenPosZ);

                int newMove = executeMovementSkript(squares);

                // set new square you move to
                setNewPosition(newMove);
                log.debug("New position is x {} z {}", this.chickenPosX, this.chickenPosZ);
            }else{
                Square chickensAktSquare = this.gameMap.getSquareAtIndexXZ(chickenPosX, chickenPosZ);
                chickensAktSquare.setType(MapObjectType.WALL);                
            }

            // consume snack if present
            currentPosition = this.gameMap.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);
            if (currentPosition.getSnack().getSnackType() != SnackType.EMPTY && super.getKcal() < MAX_CALORIES && !currentPosition.getSnack().getSnackType().equals(SnackType.EGG)) {
                log.debug("Snack being eaten at x {} z {}", this.chickenPosX, this.chickenPosZ);
                consumeSnackOnSquare();
            }
        }
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     */
    public void consumeSnackOnSquare() {
        Square currentSquare = this.gameMap.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);
        Snack snackOnSquare = currentSquare.getSnack();

        if (snackOnSquare.getSnackType() != SnackType.EMPTY) {

            try {
                gainKcal(snackOnSquare.getCalories());
                currentSquare.setSnack(new Snack(SnackType.EMPTY));
                if (super.getKcal() >= this.MAX_CALORIES) {
                    this.thickness = Thickness.VERY_HEAVY;

                    new Thread(() -> {
                        try {
                            blockingPath = true;
                            Thread.sleep(10000);
                            blockingPath = false;
                            layEgg();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();



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

            currentSquare.setSnack(new Snack(SnackType.EMPTY));
        }
    }


    /**
     * Initializes Jython for executing the chicken's movement script.
     * Sets up the required Python environment and interpreter.
     */
    public void initJython() {
        this.pythonInterpreter = new PythonInterpreter();

        try {
            String scriptPath = Paths.get("extensions/chicken/" + fileName + ".py").normalize().toAbsolutePath().toString();
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
            log.error("Error initializing {}: ", this.fileName, ex);
            ex.printStackTrace();
        }
        setWaitingTime();
        this.pythonInterpreter.exec("from " + fileName + " import choose_next_square");
    }

    private void setWaitingTime(){
        this.pythonInterpreter.exec("from " + fileName + " import getWaitingTime");

        PyObject func = pythonInterpreter.get("getWaitingTime");
        PyObject result = func.__call__();

        this.waitingTime = result.asInt();

    }

    /**
     * Executes the chicken's movement script written in Python and determines the
     * next move.
     *
     * @param squares a list of squares visible from the chicken's current position.
     * @return the movement direction as int resulting from the Python script's execution.
     */
    public int executeMovementSkript(List<String> squares) {
        try {
            log.debug("Running python chicken script with: {}", squares.toString());
            //pythonInterpreter.exec(interpreterCommand);
            PyObject func = pythonInterpreter.get("choose_next_square");
            PyObject result = func.__call__(new PyList(squares));

            return result.asInt();

        } catch (Exception ex) {
            log.error("Error while executing chicken python script: ", ex);
            ex.printStackTrace();
            return 0;
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
            Thread.sleep(waitingTime);
            move();
            log.debug("Stopping chicken with id {}", id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
    public void startNewTimer() {
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
            timerRestarted = false;
            Square currentSquare = this.gameMap.getSquareAtIndexXZ(this.chickenPosX, this.chickenPosZ);

            // new egg with current chicken-calories * 1.5
            int eggCalories = (int) (super.getKcal() * 1.5);
            Snack egg = new Snack(SnackType.EGG);
            egg.setCalories(eggCalories);
            // add egg to current square
            addEggToSquare(currentSquare, egg);
            // Chicken becomes thin again and has no calories after it has laid an egg
            this.setThickness(Thickness.THIN);
            super.setKcal(0);
            startNewTimer();
        } else {
            timerRestarted = true;
            startNewTimer();
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
    }

    /**
     * Sets the chicken to be scared and restarts the timer with a delay
     */
    public void isScaredFromGhost(boolean scared) {
        this.isScared = scared;
        layEgg();
        startNewTimer();
    }

    public boolean wasTimerRestarted() {
        return timerRestarted;
    }

    // For Testing
    public boolean isScared(){
        return this.isScared;
    }

    // For Testing
    public void setScared(boolean b){
        this.isScared = b;
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

    /**
     * @param currentPosition  the square the chicken is standing on top of
     * @param lookingDirection
     * @return a list of 8 square which are around the current square + the
     * direction the chicken is looking in the order:
     * northwest_square, north_square, northeast_square, east_square,
     * southeast_square, south_square, southwest_square, west_square,
     * direction
     */
    public synchronized List<String> getSquaresVisibleForChicken(GameMap gameMap, Square currentPosition,
                                                                 Direction lookingDirection) {
        List<String> squares = new ArrayList<>();

        squares.add(Direction.TWO_NORTH_TWO_WEST.get_two_North_two_West_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_NORTH_ONE_WEST.get_two_North_one_West_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_NORTH.get_two_North_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_NORTH_ONE_EAST.get_two_North_one_East_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_NORTH_TWO_EAST.get_two_North_two_East_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH_TWO_WEST.get_one_North_two_West_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH_ONE_WEST.get_one_North_one_West_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH.get_one_North_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH_ONE_EAST.get_one_North_one_East_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_NORTH_TWO_EAST.get_one_North_two_East_Square(gameMap, currentPosition).getPrimaryType());

        squares.add(Direction.TWO_WEST.get_two_West_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_WEST.get_one_West_Square(gameMap, currentPosition).getPrimaryType());

        squares.add(Direction.CHICKEN.get_Chicken_Square(gameMap, currentPosition).getPrimaryType());

        squares.add(Direction.ONE_EAST.get_one_East_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_EAST.get_two_East_Square(gameMap, currentPosition).getPrimaryType());

        squares.add(Direction.ONE_SOUTH_TWO_WEST.get_one_South_two_West_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_SOUTH_ONE_WEST.get_one_South_one_West_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_SOUTH.get_one_South_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_SOUTH_ONE_EAST.get_one_South_one_East_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.ONE_SOUTH_TWO_EAST.get_one_South_two_East_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH_TWO_WEST.get_two_South_two_West_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH_ONE_WEST.get_two_South_one_West_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH.get_two_South_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH_ONE_EAST.get_two_South_one_East_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(Direction.TWO_SOUTH_TWO_EAST.get_two_South_two_East_Square(gameMap, currentPosition).getPrimaryType());
        squares.add(lookingDirection.toString());

        return squares;
    }

    public void setWalking(boolean walking) {
        isWalking = walking;
    }
}
