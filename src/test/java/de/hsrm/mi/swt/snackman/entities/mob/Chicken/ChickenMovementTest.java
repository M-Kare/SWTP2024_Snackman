package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("W", "L", "W", "L", "W", "L", "W", "L", "0");

       int result = chicken.executeMovementSkript(visibleEnvironment);

        assertTrue(List.of(0,1,2,3).contains(result));
    }

    /**
     * Tests the scenario where the Chicken must avoid a ghost ('G') in one direction.
     */
    @Test
    public void testChickenAvoidsGhost() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("L", "G", "L", "L", "L", "L", "L", "L", "0");

        int result = chicken.executeMovementSkript(visibleEnvironment);

        assertTrue(List.of(1, 2, 3).contains(result));
    }

    /**
     * Tests the scenario where a snack ('S') is directly available.
     * The Chicken should prioritize moving to the snack.
     */
    @Test
    public void testChickenMovesToSnack() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("L", "S", "L", "L", "L", "L", "L", "L", "0");

        int result = chicken.executeMovementSkript(visibleEnvironment);

        assertEquals(0, result,
                "The Chicken should move to the snack ('S').");
    }

    /**
     * Ensures the chicken correctly chooses between multiple snacks ('S')
     */
    @Test
    public void testChickenChoosesBetweenMultipleSnacks() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("S", "L", "L", "S", "L", "L", "L", "L", "0");

        int result = chicken.executeMovementSkript(visibleEnvironment);

        assertEquals(1, result,
                "The Chicken should choose one of the snacks ('S').");
    }

    /**
     * Tests a complex scenario with snacks, ghosts, and walls to ensure correct prioritization.
     * The chicken should return the index with an empty space while avoiding ghosts and prioritizing snacks.
     */
    @Test
    public void testChickenInComplexEnvironment() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("G", "L", "S", "L", "W", "S", "G", "W", "0");

        int result = chicken.executeMovementSkript(visibleEnvironment);

        assertEquals(2, result,
                "The Chicken should avoid ghosts, prioritize snacks.");
    }

    /**
     * Ensures that the chicken makes a random move when all spaces are empty ('L').
     */
    @Test
    public void testChickenWithRandomSelection() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("L", "L", "L", "L", "L", "L", "L", "L", "0");

        int result = chicken.executeMovementSkript(visibleEnvironment);

        assertTrue(List.of(0, 1, 2, 3).contains(result));
    }
}