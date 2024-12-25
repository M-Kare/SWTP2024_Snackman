package de.hsrm.mi.swt.snackman.entities.mapObject.snack;

import java.util.Random;

public enum SnackType {
    CHERRY(100),
    STRAWBERRY(300),
    ORANGE(500),
    APPLE(700),
    EGG(100);

    private final int calories;
    SnackType(int calories) {
        this.calories = calories;
    }

    public int getCalories() {
        return calories;
    }

    /**
     * Gets a random snack
     * @return random snack
     */
    public static SnackType getRandomSnack() {
        SnackType[] snacks = values();
        Random random = new Random();
        int randomIndex = random.nextInt(snacks.length);
        return snacks[randomIndex];
    }
}
