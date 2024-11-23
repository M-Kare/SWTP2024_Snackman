package de.hsrm.mi.swt.snackman.entities.mob;

import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.hsrm.mi.swt.snackman.entities.MapObject.Egg;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.services.MapService;

public class Chicken extends EatingMob {

    private boolean blockingPath = false;
    private Thickness thickness = Thickness.THIN;
    //private ChickenTimer layEggTimer;
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

    private Chicken(Square currentPosition) {
        //this.layEggTimer = new ChickenTimer();
        this.currentPosition = currentPosition;
        //startTimer();
    }

    public Chicken() {

    }

    @Override
    protected void move() {
        chooseWalkingPath();
    }

    private void chooseWalkingPath() {
        /* pyhton script here -> increment timer when scared */
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
     private void layEgg() {
        this.layEggTimer.layEgg();
    }
    */

    /**
     * starts the timer
     private void startTimer() {
        this.layEggTimer.start();
    }
    */

    /**
     * adds a delay to the timer, so that the egg is layed later
     private void addTimeToTimerWhenScared() {
        this.layEggTimer.setDelayIncrease(ADDITIONAL_TIME_WHEN_SCARED);
    }
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
