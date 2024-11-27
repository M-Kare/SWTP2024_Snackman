package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import org.junit.jupiter.api.Test;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the 'ChickenMovementSkript.py' logic accessed by Jython.
 * This class validates specific movement scenarios for the Chicken.
 */
public class ChickenMovementTest {

    /**
     * Tests the basic functionality of the 'choose_next_square' function
     * when the Chicken is surrounded by open spaces and walls.
     */
    @Test
    public void testChickenMovement() {
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
            pyInterp.exec("import sys");
            pyInterp.exec("sys.path.append('src/main/java/de/hsrm/mi/swt/snackman/entities/mob/Chicken')");

            pyInterp.exec("from ChickenMovementSkript import choose_next_square");
            pyInterp.exec("result = choose_next_square(['W', 'L', 'W', 'L', 'W', 'L', 'W', 'L', 0])");

            String result = pyInterp.get("result").toString();
            String expectedResult = "[' ', 'L', 'L', 'L', 0]";
            assertEquals(expectedResult, result,
                    "The Python script should correctly determine the next move.");
        }
    }

    /**
     * Tests if the Chicken chooses a random direction when all squares are empty ('L').
     */
    @Test
    public void testChickenWithRandomSelection() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            visibleEnvironment.add("L");
        }

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        assertNotNull(result.get(result.size() - 1),
                "The Chicken should choose a random direction if all squares are empty.");
    }
}