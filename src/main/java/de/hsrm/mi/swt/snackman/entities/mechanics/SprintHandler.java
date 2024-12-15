package de.hsrm.mi.swt.snackman.entities.mechanics;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles sprinting mechanics, including sprint duration, cooldown management, 
 * and timers for both states. This class ensures that sprinting cannot occur 
 * indefinitely and introduces a cooldown period after sprinting is stopped or 
 * the sprint time is exhausted.
 */
public class SprintHandler {
    private static final int MAX_SPRINT_TIME_SEC = 5;

    private int sprintTimeLeft = MAX_SPRINT_TIME_SEC;
    private int cooldownTimeLeft = 0;

    private boolean isSprinting = false;
    private boolean isCooldown = false;

    private Timer sprintTimer = new Timer(true);
    private Timer cooldownTimer = new Timer(true);

    private static final Logger log = LoggerFactory.getLogger(SprintHandler.class);

    /**
     * Starts sprinting if no cooldown is active and the player is not already sprinting.
     * Initializes a timer to decrement the remaining sprint time.
     */
    public void startSprint() {
        if (isCooldown) {
            log.info("Cannot start sprint: Cooldown active.");
            return;
        }

        if (isSprinting) {
            log.info("Already sprinting.");
            return;
        }

        isSprinting = true;
        log.info("Sprint started");

        // Stoppe den Cooldown-Timer, falls aktiv
        cooldownTimer.cancel();
        cooldownTimer = new Timer(true);

        // Starte den Sprint-Timer
        sprintTimer.cancel();
        sprintTimer = new Timer(true);
        sprintTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (sprintTimeLeft > 0) {
                    sprintTimeLeft--;
                    log.info("Sprinting... Time left: {} seconds", sprintTimeLeft);
                } else {
                    log.info("Sprint time exhausted.");
                    stopSprint();
                    startCooldown(MAX_SPRINT_TIME_SEC * 2); // Cooldown of 2x sprint time
                }
            }
        }, 0, 1000); // Execute every second
    }

    /**
     * Stops sprinting, calculates the cooldown duration based on the elapsed sprint time, 
     * and starts the cooldown timer.
     */
    public void stopSprint() {
        if (!isSprinting) {
            log.info("Sprint already stopped.");
            return;
        }

        int cooldownDuration = (MAX_SPRINT_TIME_SEC - sprintTimeLeft) * 2;

        isSprinting = false;
        sprintTimer.cancel();
        log.info("Sprint stopped. Starting cooldown of {} seconds.", cooldownDuration);
        startCooldown(cooldownDuration);
    }

    /**
     * Starts the cooldown period after sprinting. Prevents sprinting during the cooldown.
     *
     * @param cooldownDuration Duration of the cooldown in seconds.
     */
    public void startCooldown(int cooldownDuration) {
        if (isCooldown) {
            log.info("Cooldown already active.");
            return;
        }

        isCooldown = true;
        cooldownTimeLeft = cooldownDuration;
        log.info("Cooldown started for {} seconds.", cooldownTimeLeft);

        cooldownTimer.cancel();
        cooldownTimer = new Timer(true);
        cooldownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (cooldownTimeLeft > 0) {
                    cooldownTimeLeft--;
                    log.info("Cooldown... Time left: {} seconds", cooldownTimeLeft);
                } else {
                    isCooldown = false;
                    sprintTimeLeft = MAX_SPRINT_TIME_SEC;
                    cooldownTimer.cancel();
                    log.info("Cooldown ended, sprinting available again.");
                }
            }
        }, 0, 1000);
    }

    public boolean canSprint() {
        boolean canSprint = !isCooldown && sprintTimeLeft > 0;
        log.debug("Can sprint: {}", canSprint);
        return canSprint;
    }

    public boolean isInCooldown() {
        log.debug("Is in cooldown: {}", isCooldown);
        return isCooldown;
    }

    public int getSprintTimeLeft() {
        return sprintTimeLeft;
    }

    public int getCooldownTimeLeft() {
        return cooldownTimeLeft;
    }
}