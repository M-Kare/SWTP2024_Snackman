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

    public Ghost(Square currentSquare, double x, double z, GameMap gameMap) {
        super(gameMap, GameConfig.GHOST_SPEED, GameConfig.GHOST_RADIUS, x, GameConfig.SNACKMAN_GROUND_LEVEL, z);

        currentSquare.addMob(this);
    }

    public void scare(Square position, GameMap gameMap) {
        for (Mob mob : gameMap.getSquareAtIndexXZ(position.getIndexX(), position.getIndexZ()).getMobs()) {
            if (mob instanceof Chicken) scareChicken();
        }
    }

    private void scareChicken() {
        // TODO VIVIEN!!
    }

    public void scareSnackMan(SnackMan snackMan) {
        snackMan.loseKcal();
    }

    @Override
    public void move(boolean f, boolean b, boolean l, boolean r, double delta, GameMap gameMap) {
        Square oldSquare = gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(super.getPosX()), calcMapIndexOfCoordinate(super.getPosZ()));
        super.move(f, b, l, r, delta, gameMap);
        Square newSquare = gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(super.getPosX()), calcMapIndexOfCoordinate(super.getPosZ()));
        scare(newSquare, gameMap);
        if (!oldSquare.equals(newSquare)) {
            oldSquare.removeMob(this);
            newSquare.addMob(this);
        }

    }

    /**
     * Adds a {@link PropertyChangeListener} to this object.
     * The listener will be notified whenever a bound property changes.
     *
     * @param listener the {@link PropertyChangeListener} to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
}