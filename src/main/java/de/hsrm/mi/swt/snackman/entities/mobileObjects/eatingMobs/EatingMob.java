package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;
import de.hsrm.mi.swt.snackman.services.MapService;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class EatingMob extends Mob {
    private int kcal;

    private int MAXKCAL = 0;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


    public EatingMob(MapService mapService, int speed, double radius) {
        super(mapService, speed, radius);
        if ((this) instanceof SnackMan) {
            MAXKCAL = 3000;
        }
    }

    public EatingMob(MapService mapService, int speed, double radius, double posX, double posY, double posZ) {
        super(mapService, speed, radius, posX, posY, posZ);
    }

    public EatingMob(MapService mapService) {
        super(mapService);
    }

    protected void setKcal(int value) {
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

    public void gainKcal(int addingKcal) throws Exception {
        if ((this.kcal + addingKcal) > 0) {
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

        if (getCurrentSquare().getSnack() != null)
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

        if (snackOnSquare != null) {
            int oldCalories = this.kcal;

            if ((kcal + snackOnSquare.getCalories()) >= MAXKCAL) {
                setKcal(MAXKCAL);
            } else {
                setKcal( kcal += snackOnSquare.getCalories() );
            }

            square.setSnack(null);

            if ((this) instanceof SnackMan) {
                System.out.println("Snackman fired calories");
                propertyChangeSupport.firePropertyChange("currentCalories", oldCalories, kcal);
                System.out.println("Snackman fired calories");
            }
        }

    }

    // Listener hinzufügen
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

}
