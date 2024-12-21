package de.hsrm.mi.swt.snackman.entities.mob.Ghost;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhost;
import org.junit.jupiter.api.Test;
import org.python.util.PythonInterpreter;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for Ghost movement logic with Jython and Java integration.
 * This class ensures that the 'Ghost' Java class correctly interacts with
 * the 'GhostMovementSkript.py' Python logic.
 */
public class ScriptGhostIntegrationTest {

    /**
     * Tests if the Ghost's 'executeMovementSkript' method correctly identifies
     * and chooses the correct empty square (" ") to move to.
     */
    @Test
    public void testExecuteMovementSkript() {
        ScriptGhost ghost = new ScriptGhost();
        ghost.initJython();

        List<String> visibleEnvironment = new ArrayList<>();
        visibleEnvironment.add("W");
        visibleEnvironment.add("W");
        visibleEnvironment.add("W");
        visibleEnvironment.add("L");
        visibleEnvironment.add("W");
        visibleEnvironment.add("C");
        visibleEnvironment.add("W");
        visibleEnvironment.add("S");
        visibleEnvironment.add("1");

        int result = ghost.executeMovementSkript(visibleEnvironment);

        assertEquals(2, result,
                "The Gost should move towards the chicken.");
    }

    /**
     * Verifies that the ghost can interact with the Python script directly,
     * using a Jython interpreter and chooses the correct empty square (" ").
     */
    @Test
    public void testGhostMovement() {
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
            pyInterp.exec("import sys");
            pyInterp.exec("sys.path.append('src/main/java/de/hsrm/mi/swt/snackman')");

            pyInterp.exec("from GhostMovementSkript import choose_next_square");
            pyInterp.exec("result = choose_next_square(['W', 'W', 'W', 'L', 'W', 'C', 'W', 'L', 0])");

            int result = Integer.parseInt(String.valueOf(pyInterp.get("result")));
            
            int expectedResult = 2;
            assertEquals(expectedResult, result,
                    "The Python script should correctly determine the next move (' ').");
        }
    }
}