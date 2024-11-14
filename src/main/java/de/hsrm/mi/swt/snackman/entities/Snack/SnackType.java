package de.hsrm.mi.swt.snackman.entities.Snack;

public enum SnackType {
    CHERRY(100),
    STRAWBERRY(300),
    ORANGE(500);

    private final int calories;
    SnackType(int calories) {
        this.calories = calories;
    }

    public int getCalories() {
        return calories;
    }
}
