package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import org.junit.jupiter.api.Test;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for Chicken movement logic with Jython and Java integration.
 * This class ensures that the 'Chicken' Java class correctly interacts with
 * the 'ChickenMovementSkript.py' Python logic.
 */
public class ChickenIntegrationTest {

    /**
     * Tests if the Chicken's 'executeMovementSkript' method correctly identifies
     * and chooses a move towards a Snack ('S').
     */
    @Test
    public void testExecuteMovementSkript() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = new ArrayList<>();
        visibleEnvironment.add("W");
        visibleEnvironment.add("S");
        visibleEnvironment.add("W");
        visibleEnvironment.add("L");
        visibleEnvironment.add("W");
        visibleEnvironment.add("L");
        visibleEnvironment.add("W");
        visibleEnvironment.add("L");

        // Call the Python script via the Chicken's executeMovementSkript method
        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        // Verify the Chicken moves towards the Snack ('S')
        assertEquals("S", result.get(result.size() - 1),
                "The Chicken should move to S.");
    }

    /**
     * Verifies that the Chicken can interact with the Python script directly,
     * using a Jython interpreter.
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
}