package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;

public class Ghost extends Mob {

    private  static long idCounter = 0;
    private long id ;


    public Ghost(MapService mapService, int speed, double radius){
        super(mapService, speed, radius);
    }

    public Ghost(MapService mapService, int speed, double radius, double posX, double posY, double posZ){
        super(mapService, speed, radius, posX, posY, posZ);
        id = generateId();
    }

    public void collectItems(){

    }

    public void scare(){


    }

    private void scareChicken(){

    }

    private void scareSnackMan(SnackMan snackMan){
       snackMan.loseKcal();
    }

    @Override
    public void move(double posX, double posY, double posZ) {
        super.move(posX, posY, posZ);
        for ( Mob mob : getCurrentSquare().getMobs()){
            if ( mob instanceof  SnackMan)scareSnackMan((SnackMan)mob);
        }
    }
    private synchronized static long generateId() {
        return idCounter++;
    }
    public long getId(){
        return id ;
    }
}
