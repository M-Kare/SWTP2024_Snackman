package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnackMan extends EatingMob {
    private boolean isJumping = false;
    private double velocityY = 0.0;

    private final Logger log = LoggerFactory.getLogger(SnackMan.class);

    public SnackMan(MapService mapService) {
        this(mapService, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS);
    }

    public SnackMan(MapService mapService, int speed, double radius) {
        super(mapService, speed, radius);
    }

    public SnackMan(MapService mapService, int speed, double radius, double posX, double posY, double posZ) {
        super(mapService, speed, radius, posX, posY, posZ);
    }

    public void gainKcal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gainKcal'");
    }

    @Override
    public void loseKcal() {
        // Calorsies reduced by 300 if Ghost hit
        setKcal(getKcal()-GameConfig.GHOST_DAMAGE);
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

    @Override
    public void move(double x, double y, double z) {
        super.move(x, y, z);
        for (Mob mob : getCurrentSquare().getMobs()){
            if(mob instanceof Ghost)loseKcal();
        }
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
