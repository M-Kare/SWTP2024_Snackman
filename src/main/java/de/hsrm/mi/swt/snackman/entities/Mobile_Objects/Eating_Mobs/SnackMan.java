package de.hsrm.mi.swt.snackman.entities.Mobile_Objects.Eating_Mobs;

public class SnackMan implements EatingMob {
    private int squareIndexX;
    private int squareIndexZ;
    private double kcalConsumed;
    private int posY;
    private final int POSY = 2;

    public SnackMan(int squareIndexX, int squareIndexZ){
        this.squareIndexX = squareIndexX;
        this.squareIndexZ = squareIndexZ;
        kcalConsumed = 0;
        posY = POSY;
    }

    @Override
    public void move() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'move'");
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

    public void jumpOverChicken(){

    }

    public void jumpToSeeMap(){

    }

    public void jumpOverWall(){

    }

    public void collectItems(){

    }
    
}
