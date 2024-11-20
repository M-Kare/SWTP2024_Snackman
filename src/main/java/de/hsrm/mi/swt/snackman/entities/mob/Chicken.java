package de.hsrm.mi.swt.snackman.entities.mob;

import java.util.Timer;

public class Chicken extends EatingMob {

    private boolean blockingPath = false;
    private Thickness thickness = Thickness.THIN;
    private Timer layEggTimer;

    private Chicken() {
        this.layEggTimer = new Timer();
    }

    @Override
    protected void move() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'move'");
    }

    private void chooseWalkingPath() {

    }

    private void incrementThickness() {
        switch (this.thickness) {
            case Thickness.THIN:
                this.thickness = Thickness.SLIGHTLY_THICK;
                break;
            case Thickness.SLIGHTLY_THICK:
                this.thickness = Thickness.MEDIUM;
                break;
            case Thickness.MEDIUM:
                this.thickness = Thickness.HEAVY;
                break;
            case Thickness.HEAVY:
                this.thickness = Thickness.VERY_HEAVY;
                blockingPath = true;
                break;
            case Thickness.VERY_HEAVY:
                this.thickness = Thickness.THIN;
                blockingPath = false;
                break;
        }
    }

    private void layEgg() {

    }

    private void startTimer() {

    }

    private void addTimeToTimerWhenScared() {

    }

}
