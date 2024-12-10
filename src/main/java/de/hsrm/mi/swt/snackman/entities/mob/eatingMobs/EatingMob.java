package de.hsrm.mi.swt.snackman.entities.mob.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.mob.Mob;

/**
 * A mob which can consume snacks
 */
public abstract class EatingMob extends Mob {

    private int kcal;

    protected void gainKcal(int eatenKcal) {
        this.kcal += eatenKcal;
    }

    protected void loseKcal(int consumedKcal) {
        this.kcal -= consumedKcal;
    }

    protected int getKcal() {
        return this.kcal; 
    }

    protected void setKcal(int kcal) {
        this.kcal = kcal;
    }
    
}
