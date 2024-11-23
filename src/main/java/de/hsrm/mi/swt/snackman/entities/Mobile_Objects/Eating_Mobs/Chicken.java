package de.hsrm.mi.swt.snackman.entities.Mobile_Objects.Eating_Mobs;

public class Chicken implements EatingMob{
    private int squareIndexX;
    private int squareIndexZ;
    private double kcalConsumed;
    private boolean blockingPath;
    private double minKcalToStartTimer;
    // private Timer timer;
    // private FATNESS fatness;

    public Chicken(){
        
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

    public void chooseWalkingPath(){

    }

    public void updateFatness(){

    }

    public void layEgg(){

    }

    public void startTimer(){

    }

    public void addTimeToTimerWhenScared(){

    }
    
}
