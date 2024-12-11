package de.hsrm.mi.swt.snackman.entities.mob.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.mob.Mob;

/**
 * A mob which can consume snacks
 */
public abstract class EatingMob extends Mob {

    private int kcal;

    public int getKcal() {
        return this.kcal;
    }

    public void gainKcal(int addingKcal) throws Exception {
        if ((this.kcal + addingKcal) > 0) {
            this.kcal += addingKcal;
        } else {
            throw new Exception("Kcal would now be below zero!");
        }
    }

}
