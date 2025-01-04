package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Ghost extends Mob {



    // Looking Direction muss noch geaddet werden

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public Ghost(Square currentSquare, MapService mapService, int speed, double radius) {
        //super(mapService, speed, radius, currentSquare.getIndexX()*GameConfig.SQUARE_SIZE, GameConfig.GHOST_GROUND_LEVEL, currentSquare.getIndexZ()*GameConfig.SQUARE_SIZE );
        //super(mapService, speed, radius, currentSquare.getIndexX(), GameConfig.GHOST_GROUND_LEVEL, currentSquare.getIndexZ());
        super();
        super.setQuat(new Quaterniond());

        super.mapService = mapService;
        setSpawn(new Vector3d(currentSquare.getIndexX(), GameConfig.GHOST_GROUND_LEVEL, currentSquare.getIndexZ() ));
        super.setPosition(super.getSpawn());
        setPosY(GameConfig.GHOST_GROUND_LEVEL);
        setPosX(currentSquare.getIndexX());
        setPosZ(currentSquare.getIndexZ());
        super.id = generateId();
        super.setPosY(GameConfig.SNACKMAN_GROUND_LEVEL);
        super.setPosX(currentSquare.getIndexX());
        super.setPosZ(currentSquare.getIndexZ());
        currentSquare.addMob(this);

    }



    public void collectItems() {

    }

    public void scare(Square position) {
        //      for (Mob mob ; getCurrentSquare().getMobs())
        for (Mob mob : mapService.getGameMap().getSquareAtIndexXZ(position.getIndexX(),  position.getIndexZ()).getMobs() ) {
            if (mob instanceof SnackMan) scareSnackMan((SnackMan) mob);
            else if (mob instanceof Chicken)scareChicken();
        }
    }

    private void scareChicken() {

    }

    private void scareSnackMan(SnackMan snackMan) {
        snackMan.loseKcal();
    }

    @Override
    public void move(double posX, double posY, double posZ) {
        Square oldSquare = mapService.getSquareAtIndexXZ(calcMapIndexOfCoordinate(posX), calcMapIndexOfCoordinate(posZ));
        Square newSquare = mapService.getSquareAtIndexXZ((int) posX, (int) posZ);

        if (!oldSquare.equals(newSquare)) {
            oldSquare.removeMob(this);
            newSquare.addMob(this);
            propertyChangeSupport.firePropertyChange("ghost", null, this); // ghost ändern
        }

        super.move(posX, posY, posZ);
        this.setPosX(posX);                                 // --------------------oder muss hier super.
        this.setPosZ(posZ);                                 // --------------------oder muss hier super.

        super.setPositionWithIndexXZ(posX, posZ);

        scare(newSquare);
    }

    @Override
    public String toString() {
        return "Ghost{" + "ghostId= " +  super.getId()+
                " ghostPositionX=" + super.getPosX() +
                ", ghostPositionY=" + super.getPosY() +
                ", ghostPositionZ=" + super.getPosZ() + " }" ;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
}



/*
 * Geist darstellung muss gefixt werden
 * stomp infos ans frontend überprüfen was gesendet woird
 * Ghost id senden aus Frontend
 *
 *
 *
 * Game map klasse geist wird mit einem null square objekt init
 * */