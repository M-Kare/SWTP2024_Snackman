package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Direction;

import java.util.Random;

public enum ScriptGhostDifficulty {
    EASY, DIFFICULT;

    /**
     * Gets a random script ghost difficulty
     * @return random script ghost difficulty
     */
    public static ScriptGhostDifficulty getRandomScriptGhostDifficulty() {
        ScriptGhostDifficulty[] difficulties = {EASY, DIFFICULT};
        Random random = new Random();
        int randomIndex = random.nextInt(difficulties.length);
        return EASY;
    }
}
