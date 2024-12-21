package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;

public class SnackMan extends EatingMob {
    private final Logger log = LoggerFactory.getLogger(SnackMan.class);
    private boolean isJumping = false;
    private double velocityY = 0.0;

    public SnackMan(GameMap gameMap) {
        //TODO add Snackman id to match client
        this(gameMap, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS);
    }

    public SnackMan(GameMap gameMap, double posX, double posY, double posZ) {
        this(gameMap, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS, posX, posY, posZ);
    }

    public SnackMan(GameMap gameMap, int speed, double radius) {
        super(gameMap, speed, radius);
    }

    public SnackMan(GameMap gameMap, int speed, double radius, double posX, double posY, double posZ) {
        super(gameMap, speed, radius, posX, posY, posZ);
    }

    public void gainKcal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gainKcal'");
    }

    @Override
    public void loseKcal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loseKcal'");
    }

    //JUMPING
    public void jump() {
        if (!isJumping) {
            if (getKcal() >= 100) {
                this.velocityY = GameConfig.JUMP_STRENGTH;
                this.isJumping = true;
                setKcal(getKcal() - 100);
            }
        }
    }

    public void doubleJump() {
        if (isJumping) {
            if (getKcal() >= 100) {
                this.velocityY += GameConfig.DOUBLEJUMP_STRENGTH;
                setKcal(getKcal() - 100);
            }
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

    private void jumpOverChicken() {

    }

    private void jumpToSeeMap() {

    }

    private void jumpOverWall() {

    }

    public void collectItems() {

    }

    public int getCurrentCalories() {
        return super.getKcal();
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     *
     * @param square to eat the snack from
     */
    public void consumeSnackOnSquare(Square square) {
        Snack snackOnSquare = square.getSnack();

        if (snackOnSquare != null) {
            try {
                super.gainKcal(snackOnSquare.getCalories());
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            //set snack to null after consuming it
            square.setSnack(null);
        }
    }
}
