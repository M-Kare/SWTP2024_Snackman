package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;

public class Ghost extends Mob {

    private  static long idCounter = 0;
    private long id ;
    private int posX;
    private int posY;
    private int posZ;

        public Ghost(Square square, MapService mapService, int speed, double radius){
        super(mapService );
        this.posX = square.getIndexX();
        this.posZ = square.getIndexZ();
        this.posY = GameConfig.GHOST_HIGHT;
        id = generateId();
        square.addMob(this);
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
        return id;
    }

    @Override
    public String toString(){
            return "Ghost{"+
                    " ghostPositionX=" + posX +
                    ", ghostPositionY=" + posY +
                    ", ghostPositionZ=" + posZ ;

    }

}
