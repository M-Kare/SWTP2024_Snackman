package de.hsrm.mi.swt.snackman.entities.Mobile_Objects.Eating_Mobs;

import de.hsrm.mi.swt.snackman.entities.Mobile_Objects.Mob;

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
