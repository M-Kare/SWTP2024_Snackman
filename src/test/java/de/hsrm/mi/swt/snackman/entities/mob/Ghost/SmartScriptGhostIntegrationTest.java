package de.hsrm.mi.swt.snackman.entities.mob.Ghost;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhost;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhostDifficulty;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.python.util.PythonInterpreter;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for Ghost movement logic with Jython and Java integration.
 * This class ensures that the 'Ghost' Java class correctly interacts with
 * the 'SmartGhostMovementSkript.py' Python logic.
 */
public class SmartScriptGhostIntegrationTest {

    @Mock
    private MapService mapService;
    private ScriptGhost scriptGhost;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scriptGhost = new ScriptGhost(mapService, ScriptGhostDifficulty.DIFFICULT);
    }

    /**
     * Tests if the Ghost's 'executeMovementSkript' method can be executed
     */
    @Test
    public void testExecuteMovementSkript() {
        scriptGhost.initJython();

        List<List<String>> lab = Arrays.asList(
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W"),
                Arrays.asList("W", "M", "L", "L", "W", "L", "L", "L", "L", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "G", "W", "L", "W", "W", "W", "W", "W", "W", "W", "W", "L", "W", "W"),
                Arrays.asList("W", "L", "L", "L", "W", "L", "L", "L", "W", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W")
        );

        int result = scriptGhost.executeMovementSkriptDifficult(lab);

        assertTrue(List.of(0,1,2,3).contains(result));
    }

    /**
     * Verifies that the ghost can interact with the Python script directly,
     * using a Jython interpreter and returning an integer.
     */
    @Test
    public void testGhostMovement() {
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
            pyInterp.exec("import sys");
            pyInterp.exec("sys.path.append('src/main/java/de/hsrm/mi/swt/snackman')");

            pyInterp.exec("from SmartGhostMovementSkript import choose_next_move");
            pyInterp.exec("result = choose_next_move([\n" +
                    "    ['W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W'],\n" +
                    "    ['W', 'M', 'L', 'L', 'W', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'W'],\n" +
                    "    ['W', 'G', 'W', 'L', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'W', 'L', 'W', 'W'],\n" +
                    "    ['W', 'L', 'L', 'L', 'W', 'L', 'L', 'L', 'W', 'L', 'L', 'L', 'L', 'L', 'W'],\n" +
                    "    ['W', 'L', 'W', 'L', 'W', 'W', 'W', 'W', 'W', 'L', 'W', 'W', 'W', 'W', 'W'],\n" +
                    "    ['W', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'W', 'L', 'L', 'L', 'W', 'L', 'W'],\n" +
                    "    ['W', 'L', 'L', 'W', 'W', 'L', 'L', 'L', 'L', 'L', 'L', 'W', 'W', 'W', 'W']\n" +
                    "])");

            int result = Integer.parseInt(String.valueOf(pyInterp.get("result")));
            
            assertTrue(List.of(0,1,2,3).contains(result));
        }
    }
}