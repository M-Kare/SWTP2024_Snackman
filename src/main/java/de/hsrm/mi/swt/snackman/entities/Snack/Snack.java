package de.hsrm.mi.swt.snackman.entities.Snack;

import de.hsrm.mi.swt.snackman.Types.Position;

/**
 * Class that defines Snack, that can be eaten by Snackman
 */
public class Snack {
    private int id;
    private Position position;
    private SnackType snackType;
    private int calories;

    public Snack(int id, SnackType snackType, Position position) {
        this.id = id;
        this.snackType = snackType;
        this.position = position;
        this.calories = snackType.getCalories();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public Position getPosition() { return position; }

    public void setPosition(Position position) { this.position = position; }
}
