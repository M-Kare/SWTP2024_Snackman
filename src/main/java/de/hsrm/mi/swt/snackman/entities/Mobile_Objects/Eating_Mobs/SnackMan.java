package de.hsrm.mi.swt.snackman.entities.Mobile_Objects.Eating_Mobs;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;

public class SnackMan extends EatingMob {
    
    private double posX;
    private double posY;
    private double posZ;
    private double dirY;
    private double radius;

    public SnackMan(double x, double z){
        super();

        posY = GameConfig.SNACKMAN_GROUND_LEVEL;
        posX = x;
        posZ = z;
        dirY = 0;
        radius = GameConfig.SNACKMAN_RADIUS;
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

    @Override
    public void move(double x, double y, double z) {
        if(x-radius > -4){
            posX = x;
        }
        posZ = z;
        calcMapIndex(posX, posZ);
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

}
