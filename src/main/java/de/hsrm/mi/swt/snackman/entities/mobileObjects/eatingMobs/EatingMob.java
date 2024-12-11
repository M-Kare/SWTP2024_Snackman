package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;
import de.hsrm.mi.swt.snackman.services.MapService;

public abstract class EatingMob extends Mob{
    private int kcal;

    public EatingMob(MapService mapService, int speed, double radius){
        super(mapService, speed, radius);
    }

    public EatingMob(MapService mapService, int speed, double radius, double posX, double posY, double posZ){
        super(mapService, speed, radius, posX, posY, posZ);
    }

    public void setKcal(int value){
        kcal = value;
    }

    public int getKcal(){
        return kcal;
    }

    abstract public void gainKcal();
    abstract public void loseKcal();
}
