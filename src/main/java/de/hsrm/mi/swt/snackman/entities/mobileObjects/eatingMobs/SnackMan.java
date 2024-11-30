package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;

public class SnackMan extends EatingMob {
    
    private double posX;
    private double posY;
    private double posZ;
    private double dirY;
    private double radius;
    private Quaterniond quat;

    public SnackMan(double x, double z){
        super();

        posY = GameConfig.SNACKMAN_GROUND_LEVEL;
        posX = x;
        posZ = z;
        dirY = 0;
        radius = GameConfig.SNACKMAN_RADIUS;
        quat = new Quaterniond();
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public double getDirY() {
        return dirY;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void move(boolean f, boolean b, boolean l, boolean r, double delta) {
        int moveDirZ = (f ? 1 : 0) - (b ? 1 : 0);
        int moveDirX = (r ? 1 : 0) - (l ? 1 : 0);

        Vector3d move = new Vector3d();

        if (f || b) {
            move.z -= moveDirZ * delta * 3;
        }
        if (l || r) {
            move.x += moveDirX * delta * 3;
        }
        move.rotate(quat);
        posX += move.x;
        posZ += move.z;
    }

    public void setQuaternion(double qX, double qY, double qZ, double qW) {
        quat.x = qX;
        quat.y = qY;
        quat.z = qZ;
        quat.w = qW;
    }

    private void calcMapIndex(double x, double z){
        int squareIndexX = (int)(x / GameConfig.SQUARE_SIZE);
        int squareIndexZ = (int)(z / GameConfig.SQUARE_SIZE);
        //setSquare(setMap.getSquare(squareIndexX, squareIndexZ));
    }
    
    public void setDirY(double angleY){
        dirY = angleY;
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
    
    public void jump(){

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
