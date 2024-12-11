package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.services.MapService;

public class Ghost extends Mob {

    public Ghost(MapService mapService, int speed, double radius){
        super(mapService, speed, radius);
    }

    public Ghost(MapService mapService, int speed, double radius, double posX, double posY, double posZ){
        super(mapService, speed, radius, posX, posY, posZ);
    }

    public void collectItems(){

    }

    public void scare(){

    }

    private void scareChicken(){

    }

    private void scareSnackMan(){

    }

    @Override
    public void move() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'move'");
    }

    
}
