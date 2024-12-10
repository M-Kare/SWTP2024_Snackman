package de.hsrm.mi.swt.snackman.entities.mob.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.mob.Mob;

/**
 * A mob which can consume snacks
 */
public abstract class EatingMob extends Mob {

    protected int kcal;

    public int getKcal() {
        return this.kcal; 
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }
    
}
