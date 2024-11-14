package de.hsrm.mi.swt.snackman.entities.Snack;

/**
 * Class that defines Snack, that can be eaten by Snackman
 */
public class Snack {
    private int id;
    private SnackType snackType;
    private int calories;

    public Snack(int id, SnackType snackType) {
        this.id = id;
        this.snackType = snackType;
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
}
