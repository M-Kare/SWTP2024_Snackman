package de.hsrm.mi.swt.snackman.entities.mapObject.snack;

public enum SnackType {
    CHERRY(100),
    STRAWBERRY(300),
    ORANGE(500),
    APPLE(700);

    private final int calories;
    SnackType(int calories) {
        this.calories = calories;
    }

    public int getCalories() {
        return calories;
    }
}
