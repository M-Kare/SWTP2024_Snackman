package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;

public class Ghost extends Mob {

    public Ghost(GameMap gameMap, int speed, double radius){
        super(gameMap, speed, radius);
    }

    public Ghost(GameMap gameMap, int speed, double radius, double posX, double posY, double posZ){
        super(gameMap, speed, radius, posX, posY, posZ);
    }

    public void collectItems(){

    }

    public void scare(){

    }

    private void scareChicken(){

    }

    private void scareSnackMan(){

    }
}
