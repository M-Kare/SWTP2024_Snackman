package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.services.MapService;

public class Ghost extends Mob {

    public Ghost(MapService mapService){
        super(mapService);
    }

    public Ghost(MapService mapService, double posX, double posY, double posZ){
        super(mapService, posX, posY, posZ);
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
