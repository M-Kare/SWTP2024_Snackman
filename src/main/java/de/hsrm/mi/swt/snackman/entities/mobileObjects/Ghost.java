package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Ghost extends Mob {

    // Looking Direction muss noch geaddet werden

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public Ghost(Square currentSquare, MapService mapService) {
        super(mapService, GameConfig.GHOST_SPEED, GameConfig.GHOST_RADIUS, (currentSquare.getIndexX() * GameConfig.SQUARE_SIZE + 0.5 * GameConfig.SQUARE_SIZE), GameConfig.SNACKMAN_GROUND_LEVEL, (currentSquare.getIndexZ() * GameConfig.SQUARE_SIZE + 0.5 * GameConfig.SQUARE_SIZE));

        currentSquare.addMob(this);
    }

    public void scare(Square position) {
        //      for (Mob mob ; getCurrentSquare().getMobs())
        for (Mob mob : mapService.getGameMap().getSquareAtIndexXZ(position.getIndexX(), position.getIndexZ()).getMobs()) {
            if (mob instanceof SnackMan) scareSnackMan((SnackMan) mob);
            else if (mob instanceof Chicken) scareChicken();
        }
    }

    private void scareChicken() {

    }

    private void scareSnackMan(SnackMan snackMan) {
        snackMan.loseKcal();
    }

    @Override
    public void move(boolean f, boolean b, boolean l, boolean r, double delta) {
        Square oldSquare = mapService.getSquareAtIndexXZ(calcMapIndexOfCoordinate(super.getPosX()), calcMapIndexOfCoordinate(super.getPosZ()));
        super.move(f, b, l, r, delta);
        Square newSquare = mapService.getSquareAtIndexXZ(calcMapIndexOfCoordinate(super.getPosX()), calcMapIndexOfCoordinate(super.getPosZ()));
        propertyChangeSupport.firePropertyChange("ghost", null, this); // ghost ändern
        scare(newSquare);
        if (!oldSquare.equals(newSquare)) {
            oldSquare.removeMob(this);
            newSquare.addMob(this);
        }

    }

    @Override
    public String toString() {
        return "Ghost{" + "ghostId= " + super.getId() +
                " ghostPositionX=" + super.getPosX() +
                ", ghostPositionY=" + super.getPosY() +
                ", ghostPositionZ=" + super.getPosZ() + " }";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
}