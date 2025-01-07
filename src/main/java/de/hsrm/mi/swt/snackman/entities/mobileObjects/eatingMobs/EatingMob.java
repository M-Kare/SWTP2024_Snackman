package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A mob which can consume snacks
 */
public abstract class EatingMob extends Mob {
    private int kcal;

    private int MAXKCAL = 0;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public EatingMob(GameMap gameMap, double speed, double radius) {
        super(gameMap, speed, radius);
        if ((this) instanceof SnackMan) {
            MAXKCAL = 3000;
        }
    }

    public EatingMob(GameMap gameMap, double speed, double radius, double posX, double posY, double posZ) {
        super(gameMap, speed, radius, posX, posY, posZ);
    }

    public EatingMob(GameMap gameMap) {
        super(gameMap);
    }

    public void setKcal(int value) {
        int oldKcal = kcal;
        kcal = value;
        propertyChangeSupport.firePropertyChange("currentCalories", oldKcal, this.kcal);
    }

    public int getKcal() {
        return kcal;
    }

    public int getMAXKCAL() {
        return MAXKCAL;
    }

    protected void gainKcal(int addingKcal) throws Exception {
        if ((this.kcal + addingKcal) >= 0) {
            this.kcal += addingKcal;
        } else {
            throw new Exception("Kcal cannot be below zero!");
        }
    }

    public void loseKcal(int loseKcal) throws Exception {
        if ((this.kcal - loseKcal) >= 0) {
            this.kcal -= loseKcal;
        } else {
            throw new Exception("Kcal cannot be below zero!");
        }
    }

    @Override
    public void move(boolean f, boolean b, boolean l, boolean r, double delta) {
        super.move(f, b, l, r, delta);

        if (getCurrentSquare().getSnack().getSnackType() != SnackType.EMPTY)
            consumeSnackOnSquare(getCurrentSquare());
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one than remove it from the square.
     *
     * @param square to eat the snack from
     */
    public void consumeSnackOnSquare(Square square) {
        Snack snackOnSquare = square.getSnack();

        if (snackOnSquare.getSnackType() != SnackType.EMPTY) {
            int oldCalories = this.kcal;

            if ((kcal + snackOnSquare.getCalories()) >= MAXKCAL) {
                setKcal(MAXKCAL);
            } else {
                setKcal( kcal += snackOnSquare.getCalories() );
            }

            //set snack to null after consuming it
            square.setSnack(new Snack(SnackType.EMPTY));

            if ((this) instanceof SnackMan) {
                propertyChangeSupport.firePropertyChange("currentCalories", oldCalories, kcal);
            }
        }
    }

    // Listener hinzufügen
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
