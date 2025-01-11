package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Ghost extends Mob {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    // TODO change constructors
    public Ghost(Square currentSquare, double x, double z, GameMap gameMap) {
        super(gameMap, GameConfig.GHOST_SPEED, GameConfig.GHOST_RADIUS, x, GameConfig.SNACKMAN_GROUND_LEVEL, z);

        currentSquare.addMob(this);
    }

    public void scare(Square position) {
        //      for (Mob mob ; getCurrentSquare().getMobs())
        for (Mob mob : this.getGameMap().getSquareAtIndexXZ(position.getIndexX(), position.getIndexZ()).getMobs()) {
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
        Square oldSquare = this.getGameMap().getSquareAtIndexXZ(calcMapIndexOfCoordinate(super.getPosX()), calcMapIndexOfCoordinate(super.getPosZ()));
        super.move(f, b, l, r, delta);
        Square newSquare = this.getGameMap().getSquareAtIndexXZ(calcMapIndexOfCoordinate(super.getPosX()), calcMapIndexOfCoordinate(super.getPosZ()));
        //propertyChangeSupport.firePropertyChange("ghost", null, this); // ghost ändern
        scare(newSquare);
        if (!oldSquare.equals(newSquare)) {
            oldSquare.removeMob(this);
            newSquare.addMob(this);
        }

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
}