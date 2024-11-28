package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import org.junit.jupiter.api.Test;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    /**
     * Tests the scenario where the Chicken must avoid a ghost ('G') in one direction.
     * The Chicken should choose a square that is not a ghost.
     */
    @Test
    public void testChickenAvoidsGhost() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = new ArrayList<>();
        visibleEnvironment.add("L");
        visibleEnvironment.add("G");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        assertNotEquals("G", result.get(result.size() - 1),
                "The Chicken should avoid moving into a square with a ghost.");
    }

    /**
     * Tests the scenario where a snack ('S') is directly available.
     * The Chicken should prioritize moving to the snack.
     */
    @Test
    public void testChickenMovesToSnack() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = new ArrayList<>();
        visibleEnvironment.add("L");
        visibleEnvironment.add("S");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        assertEquals("S", result.get(result.size() - 1),
                "The Chicken should move to the snack when one is available.");
    }

    /**
     * Tests the scenario where the Chicken must decide between multiple snacks ('S').
     * The Chicken should choose the first snack.
     */
    @Test
    public void testChickenChoosesBetweenMultipleSnacks() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = new ArrayList<>();
        visibleEnvironment.add("S");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("S");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");
        visibleEnvironment.add("L");

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        assertEquals("S", result.get(result.size() - 1),
                "The Chicken should choose one of the available snacks.");
    }

    /**
     * Tests a complex scenario where the Chicken is surrounded by snacks, ghosts, and walls.
     * The Chicken should prioritize avoiding ghosts while targeting snacks.
     */
    @Test
    public void testChickenInComplexEnvironment() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = new ArrayList<>();
        visibleEnvironment.add("G");
        visibleEnvironment.add("L");
        visibleEnvironment.add("S");
        visibleEnvironment.add("L");
        visibleEnvironment.add("W");
        visibleEnvironment.add("S");
        visibleEnvironment.add("G");
        visibleEnvironment.add("W");

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        assertEquals("S", result.get(result.size() - 1),
                "The Chicken should avoid ghosts, prioritize snacks, and navigate walls correctly.");
    }
}