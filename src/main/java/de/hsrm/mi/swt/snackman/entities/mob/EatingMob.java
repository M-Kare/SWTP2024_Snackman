package de.hsrm.mi.swt.snackman.entities.mob;

public abstract class EatingMob extends Mob{

    private int kcal;

    private void gainKcal(int eatenKcal) {
        this.kcal += eatenKcal;
    }

    private void loseKcal(int consumedKcal) {
        this.kcal -= consumedKcal;
    }
    
}
