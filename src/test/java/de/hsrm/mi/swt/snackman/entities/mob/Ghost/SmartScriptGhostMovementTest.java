package de.hsrm.mi.swt.snackman.entities.mob.Ghost;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhost;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhostDifficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for Ghost movement logic with Jython and Java integration.
 * This class ensures that the 'Ghost' Java class correctly interacts with
 * the 'SmartGhostMovementSkript.py' Python logic.
 *
 * todo fix tests
 */
public class SmartScriptGhostMovementTest {

    @Mock
    private GameMap gameMap;
    private ScriptGhost scriptGhost;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scriptGhost = new ScriptGhost(gameMap, ScriptGhostDifficulty.DIFFICULT);
    }

    /**
     * Tests if the ghost moves north as determined by the Python script.
     *
     * Simulates a maze configuration where moving north is the optimal move.
     */
    @Test
    public void testGhostWalkNorth() {
        List<List<String>> lab = Arrays.asList(
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W"),
                Arrays.asList("W", "M", "L", "L", "W", "L", "L", "L", "L", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "G", "W", "L", "W", "W", "W", "W", "W", "W", "W", "W", "L", "W", "W"),
                Arrays.asList("W", "L", "L", "L", "W", "L", "L", "L", "W", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W")
        );

        int result = scriptGhost.executeMovementSkriptDifficult(lab);

        assertEquals(0, result,
                "The Python script should correctly determine the next move which is north.");
    }

    /**
     * Tests if the ghost moves east as determined by the Python script.
     *
     * Simulates a maze configuration where moving east is the optimal move.
     */
    @Test
    public void testGhostWalkEast() {
        List<List<String>> lab = Arrays.asList(
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W"),
                Arrays.asList("W", "G", "M", "L", "W", "L", "L", "L", "L", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "L", "W", "L", "W", "W", "W", "W", "W", "W", "W", "W", "L", "W", "W"),
                Arrays.asList("W", "L", "L", "L", "W", "L", "L", "L", "W", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W")
        );

        int result = scriptGhost.executeMovementSkriptDifficult(lab);

        assertEquals(1, result,
                "The Python script should correctly determine the next move which is east.");
    }

    /**
     * Tests if the ghost moves south as determined by the Python script.
     *
     * Simulates a maze configuration where moving south is the optimal move.
     */
    @Test
    public void testGhostWalkSouth() {
        List<List<String>> lab = Arrays.asList(
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W"),
                Arrays.asList("W", "G", "L", "L", "W", "L", "L", "L", "L", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "M", "W", "L", "W", "W", "W", "W", "W", "W", "W", "W", "L", "W", "W"),
                Arrays.asList("W", "L", "L", "L", "W", "L", "L", "L", "W", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W")
        );

        int result = scriptGhost.executeMovementSkriptDifficult(lab);

        assertEquals(2, result,
                "The Python script should correctly determine the next move which is south.");
    }

    /**
     * Tests if the ghost moves west as determined by the Python script.
     *
     * Simulates a maze configuration where moving west is the optimal move.
     */
    @Test
    public void testGhostWalkWest() {
        List<List<String>> lab = Arrays.asList(
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W"),
                Arrays.asList("W", "M", "G", "L", "W", "L", "L", "L", "L", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "L", "W", "L", "W", "W", "W", "W", "W", "W", "W", "W", "L", "W", "W"),
                Arrays.asList("W", "L", "L", "L", "W", "L", "L", "L", "W", "L", "L", "L", "L", "L", "W"),
                Arrays.asList("W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W", "W")
        );

        int result = scriptGhost.executeMovementSkriptDifficult(lab);

        assertEquals(3, result,
                "The Python script should correctly determine the next move which is west.");
    }

}