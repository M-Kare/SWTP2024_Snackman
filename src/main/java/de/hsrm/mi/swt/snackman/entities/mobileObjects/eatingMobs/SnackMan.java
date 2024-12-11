package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.services.MapService;

public class SnackMan extends EatingMob {
    private boolean isJumping = false;
    private double velocityY = 0.0;

    public SnackMan(MapService mapService){
        this(mapService, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS);
    }

    public SnackMan(MapService mapService, int speed, double radius){
        super(mapService, speed, radius);    }

    public SnackMan(MapService mapService, int speed, double radius, double posX, double posY, double posZ){
        super(mapService, speed, radius, posX, posY, posZ);
    }

    @Override
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
            this.posY += this.velocityY * deltaTime;

            if (this.posY <= GameConfig.SNACKMAN_GROUND_LEVEL) {
                this.posY = GameConfig.SNACKMAN_GROUND_LEVEL;
                this.isJumping = false;
                this.velocityY = 0;
            }
        }
    }

    private void jumpOverChicken(){

    }

    private void jumpToSeeMap(){

    }

    private void jumpOverWall(){

    }

    public void collectItems(){

    }

    @Override
    public void move(double x, double y, double z) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'move'");
    }
}

