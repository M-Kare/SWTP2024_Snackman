package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;

public class SnackMan extends EatingMob {
    private final Logger log = LoggerFactory.getLogger(SnackMan.class);
    private boolean isJumping = false;
    private double velocityY = 0.0;
    private boolean isSprinting = false;
    private SprintHandler sprintHandler = new SprintHandler();

    public SnackMan(GameMap gameMap) {
        //TODO add Snackman id to match client
        this(gameMap, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS);
    }

    public SnackMan(GameMap gameMap, double posX, double posY, double posZ) {
        this(gameMap, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS, posX, posY, posZ);
    }

    public SnackMan(GameMap gameMap, double speed, double radius) {
        super(gameMap, speed, radius);
    }

    public SnackMan(GameMap gameMap, int speed, double radius, double posX, double posY, double posZ) {
        super(gameMap, speed, radius, posX, posY, posZ);
    }

    //JUMPING
    public void jump() {
        if (!isJumping && getKcal() >= 100) {
                this.velocityY = GameConfig.JUMP_STRENGTH;
                this.isJumping = true;
                setKcal(getKcal() - 100);
            }

    }

    public void doubleJump() {
        if (isJumping && getKcal() >= 100) {
                this.velocityY += GameConfig.DOUBLEJUMP_STRENGTH;
                setKcal(getKcal() - 100);
            }

    }

    public void updateJumpPosition(double deltaTime) {
        if (isJumping) {
            this.velocityY += GameConfig.GRAVITY * deltaTime;
            setPosY(getPosY() + velocityY * deltaTime);

            if (getPosY() <= GameConfig.SNACKMAN_GROUND_LEVEL) {
                setPosY(GameConfig.SNACKMAN_GROUND_LEVEL);
                this.isJumping = false;
                this.velocityY = 0;
            }
        }
    }

    public int getCurrentCalories() {
        return super.getKcal();
    }

    @Override
    public void move(boolean forward, boolean backward, boolean left, boolean right, double delta) {
        if (isSprinting) {
            if (sprintHandler.canSprint()) {
                setSpeed(GameConfig.SNACKMAN_SPEED * GameConfig.SNACKMAN_SPRINT_MULTIPLIER);
            } else {
                sprintHandler.stopSprint();
                setSpeed(GameConfig.SNACKMAN_SPEED);
            }
        } else {
            sprintHandler.stopSprint();
            setSpeed(GameConfig.SNACKMAN_SPEED);
        }

        super.move(forward, backward, left, right, delta);
    }

    public int getSprintTimeLeft() {
        return sprintHandler.getSprintTimeLeft();
    }

    public boolean isInCooldown() {
        return sprintHandler.isInCooldown();
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.isSprinting = sprinting;

        if (sprinting) {
            if (sprintHandler.canSprint()) {
                sprintHandler.startSprint();
            } else {
                this.isSprinting = false;
            }
        } else {
            sprintHandler.stopSprint();
        }
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     *
     * @param square to eat the snack from
     */
    @Override
    public void consumeSnackOnSquare(Square square) {
        Snack snackOnSquare = square.getSnack();

        if (snackOnSquare.getSnackType() != SnackType.EMPTY) {
            try {
                super.gainKcal(snackOnSquare.getCalories());
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            //set snack to null after consuming it
            square.setSnack(new Snack(SnackType.EMPTY));
        }
    }

    public boolean isJumping() {
        return isJumping;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setSprintHandler(SprintHandler sprintHandler) {
        this.sprintHandler = sprintHandler;
    }
}
