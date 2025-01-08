package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import org.junit.jupiter.api.Test;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Integration tests for Chicken movement logic with Jython and Java integration.
 * This class ensures that the 'Chicken' Java class correctly interacts with
 * the 'ChickenMovementSkript.py' Python logic.
 */
public class ChickenIntegrationTest {

    /**
     * Tests if the Chicken's 'executeMovementSkript' method correctly identifies
     * and chooses the correct square to move to.
     */
    @Test
    public void testExecuteMovementSkript() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = new ArrayList<>();
        visibleEnvironment.add("W");
        visibleEnvironment.add("S");
        visibleEnvironment.add("W");
        visibleEnvironment.add("L");
        visibleEnvironment.add("W");
        visibleEnvironment.add("L");
        visibleEnvironment.add("W");
        visibleEnvironment.add("L");
        visibleEnvironment.add("1");

        int result = chicken.executeMovementSkript(visibleEnvironment);

        assertEquals(0, result,
                "The Chicken should move to the correct empty square (' ') based on its direction.");
    }

    /**
     * Verifies that the Chicken can interact with the Python script directly,
     * using a Jython interpreter and chooses the correct square.
     */
    @Test
    public void testChickenMovement() {
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
            pyInterp.exec("import sys");
            pyInterp.exec("sys.path.append('src/main/java/de/hsrm/mi/swt/snackman')");

            pyInterp.exec("from ChickenMovementSkript import choose_next_square");
            pyInterp.exec("result = choose_next_square(['W', 'L', 'W', 'L', 'W', 'L', 'W', 'L', 0])");

            int result = Integer.parseInt(pyInterp.get("result").toString());
            
            assertEquals(0, result,
                    "The Python script should correctly determine the next move (' ').");
        }
    }
}