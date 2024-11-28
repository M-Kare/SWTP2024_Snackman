package de.hsrm.mi.swt.snackman.entities.mob;

/**
 * A mob which can consume snacks
 */
public abstract class EatingMob extends Mob{

    protected int kcal;

    private void gainKcal(int eatenKcal) {
        this.kcal += eatenKcal;
    }

    private void loseKcal(int consumedKcal) {
        this.kcal -= consumedKcal;
    }

    public int getKcal() {
        return this.kcal; 
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }
    
}
