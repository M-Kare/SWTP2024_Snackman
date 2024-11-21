package de.hsrm.mi.swt.snackman.entities.Snack;

import de.hsrm.mi.swt.snackman.Types.Position;

/**
 * Class that defines Snack, that can be eaten by Snackman
 */
public class Snack {
    private final double HEIGHT = 1.0;
    private Position position;
    private SnackType snackType;
    private int calories;

    public Snack(SnackType snackType) {
        this.snackType = snackType;
        this.calories = snackType.getCalories();
    }

    public double getHEIGHT() {
        return HEIGHT;
    }

    public SnackType getSnackType() {
        return snackType;
    }

    public void setSnackType(SnackType snackType) {
        this.snackType = snackType;
    }

    public int getCalories() {
        return calories;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
