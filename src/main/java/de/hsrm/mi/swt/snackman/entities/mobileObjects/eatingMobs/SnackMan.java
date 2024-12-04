package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.services.MapService;

public class SnackMan extends EatingMob {

    public SnackMan(MapService mapService){
        super(mapService);
    }

    public SnackMan(MapService mapService, double posX, double posY, double posZ){
        super(mapService, posX, posY, posZ);
    }

    @Override
    public void gainKcal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gainKcal'");
    }

    @Override
    public void loseKcal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loseKcal'");
    }
    
    public void jump(){

    }

    private void jumpOverChicken(){

    }

    private void jumpToSeeMap(){

    }

    private void jumpOverWall(){

    }

    public void collectItems(){

    }


}
