package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.hsrm.mi.swt.snackman.entities.MapObject.Egg;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mob.EatingMob;
import de.hsrm.mi.swt.snackman.entities.mob.Thickness;
import de.hsrm.mi.swt.snackman.services.MapService;

import org.python.core.Py;
import org.python.core.PyInstance;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Chicken extends EatingMob {

    private boolean blockingPath = false;
    private Thickness thickness = Thickness.THIN;
    // private ChickenTimer layEggTimer;
    private final int ADDITIONAL_TIME_WHEN_SCARED = 30;
    private Square currentPosition;
    private boolean isScared = false;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private long initialDelay = 0;
    private long delayIncrease = 0;
    private final int MIN_DELAY = 30;
    private final int MAX_DELAY = 30;
    private int eggIndexX = 0;
    private int eggIndexZ = 0;

    public Chicken() {
        super();
    }

    PythonInterpreter interp = null;
    StringWriter clout = null;
    String[] arguments = null;

    private String start = null, end = null, subject = null;
    Properties props = new Properties();

    private void initJython() {

    }

    public String executeMovementSkript(List<String>squares) {
        try {
            setInputs(start, end, subject);
            arguments = getInputs(start, end, subject);
            props.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman/entities/mob/Chicken");
            PythonInterpreter.initialize(System.getProperties(), props, new String[0]);

            this.interp = new PythonInterpreter();

            interp.exec("from ChickenMovementSkript import pyscript");
            PyObject func = interp.get("pyscript");
            PyObject result = func.__call__(new PyList(squares));
            
            System.out.println("java " + result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public void setInputs(String start, String end, String sub) {
        this.start = start;
        this.end = end;
        this.subject = sub;

    }

    public String[] getInputs(String start, String end, String sub) {
        String[] arr = { this.start, this.end, this.subject };

        return arr;

    }

    @Override
    protected String move() {
        /* pyhton script here -> increment timer when scared */
        List<String> squares = new LinkedList<>();
        squares.add("W");
        squares.add("W");
        squares.add("W");
        squares.add("W");
        squares.add("W");
        squares.add("W");
        squares.add("W");
        squares.add("W");
        squares.add("W");
        return executeMovementSkript(squares);
    }

    public String chooseWalkingPath() {
        return move();
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

    /**
     * Lays egg and restarts the timer
     * private void layEgg() {
     * this.layEggTimer.layEgg();
     * }
     */

    /**
     * starts the timer
     * private void startTimer() {
     * this.layEggTimer.start();
     * }
     */

    /**
     * adds a delay to the timer, so that the egg is layed later
     * private void addTimeToTimerWhenScared() {
     * this.layEggTimer.setDelayIncrease(ADDITIONAL_TIME_WHEN_SCARED);
     * }
     */

    public void start() {
        initialDelay = getRandomDelayInSeconds();
        scheduleTask(initialDelay + delayIncrease);
    }

    private void scheduleTask(long delay) {
        scheduler.schedule(this::layEgg, delay, TimeUnit.SECONDS);
    }

    public void layEgg() {
        MapService.layEgg(eggIndexX, eggIndexZ, generateEgg());
        stop();
        start();
    }

    private Egg generateEgg() {
        Random random = new Random();
        return new Egg((10 + random.nextInt(51)) * 10); // kcal between 100 and 600
    }

    private long getRandomDelayInSeconds() {
        return MIN_DELAY + (int) (Math.random() * ((MAX_DELAY - MIN_DELAY) + 1));
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    public void setDelayIncrease(long delayIncrease) {
        this.delayIncrease = delayIncrease;
    }

    public void setEggIndexX(int eggIndexX) {
        this.eggIndexX = eggIndexX;
    }

    public void setEggIndexZ(int eggIndexZ) {
        this.eggIndexZ = eggIndexZ;
    }

    public boolean getBlockingPath() {
        return this.blockingPath;
    }

    public Thickness getThickness() {
        return this.thickness;
    }

    public Square getCurrentPosition() {
        return this.getCurrentPosition();
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

}
