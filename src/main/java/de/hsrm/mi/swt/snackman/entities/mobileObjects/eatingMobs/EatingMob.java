package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;

public abstract class EatingMob extends Mob{
    private int kcal;

    public void setKcal(int value){
        kcal = value;
    }

    public int getKcal(){
        return kcal;
    }

    abstract public void gainKcal();
    abstract public void loseKcal();
}
