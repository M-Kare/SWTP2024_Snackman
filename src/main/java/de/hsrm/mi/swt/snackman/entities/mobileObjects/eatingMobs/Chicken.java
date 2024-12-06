package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.EatingMob;
import de.hsrm.mi.swt.snackman.services.MapService;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a Chicken entity that lays eggs
 */
public class Chicken extends EatingMob {

    private boolean isScared = false;
    private Timer eggLayingTimer;
    private MapService mapService;

    public Chicken() {
        super();
    }

    public Chicken(MapService mapService) {
        this.mapService = mapService;
        initTimer();
    }

    /**
     * initialises the timer for laying eggs
     */
    private void initTimer() {
        this.eggLayingTimer = new Timer();
        startNewTimer();
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
            System.out.println("Scared Chicken-Timer " + delayBecauseIsScared/1000 + " seconds"); // TODO delete
            eggLayingTimer.scheduleAtFixedRate(task, (randomIntervalForLayingANewEgg) + delayBecauseIsScared, randomIntervalForLayingANewEgg);
            this.isScared = false;
        } else {
            this.eggLayingTimer.scheduleAtFixedRate(task, randomIntervalForLayingANewEgg, randomIntervalForLayingANewEgg);
        }
    }

    /**
     * Lays an egg and restarts the timer
     */
    private void layEgg() {
        int defaultCalories = 17; // TODO delete

        Snack egg = new Snack(SnackType.EGG);
        egg.setCalories(defaultCalories);

        System.out.println("Laying Egg with calories: " + defaultCalories); // TODO delete
        startNewTimer();
    }

    /**
     * Sets the chicken to be scared and restarts the timer with a delay
     */
    public void getScared() {
        this.isScared = true;
        startNewTimer();
    }

    @Override
    protected void move() {

    }
}
