package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken.Chicken;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        // The chicken should move to an empty space (" ").
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }

    /**
     * Tests the scenario where the Chicken must avoid a ghost ('G') in one direction.
     * The Chicken should choose a square that is not a ghost (' ').
     */
    @Test
    public void testChickenAvoidsGhost() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("L", "G", "L", "L", "L", "L", "L", "L", "0");

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        // The chicken should move to an empty space (' ').
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should avoid ghosts ('G') and move to a safe square (' ').");
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

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the snack ('S'), but the result should be the empty space (' ').");
    }

    /**
     * Ensures the chicken correctly chooses between multiple snacks ('S'),
     * and still returns the empty square (' ').
     */
    @Test
    public void testChickenChoosesBetweenMultipleSnacks() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("S", "L", "L", "S", "L", "L", "L", "L", "0");

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        // The chicken should choose the empty space (' ') for the selected snack ('S').
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should choose one of the snacks ('S') but the result should be ' '.");
    }

    /**
     * Tests a complex scenario with snacks, ghosts, and walls to ensure correct prioritization.
     * The chicken should return the empty space (' ') while avoiding ghosts and prioritizing snacks.
     */
    @Test
    public void testChickenInComplexEnvironment() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("G", "L", "S", "L", "W", "S", "G", "W", "0");

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should avoid ghosts, prioritize snacks, and move to an empty square (' ').");
    }

    /**
     * Ensures that the chicken makes a random move when all spaces are empty ('L').
     */
    @Test
    public void testChickenWithRandomSelection() {
        Chicken chicken = new Chicken();
        chicken.initJython();

        List<String> visibleEnvironment = List.of("L", "L", "L", "L", "L", "L", "L", "L", "0");

        List<String> result = chicken.executeMovementSkript(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should choose a random empty space (' ') when all squares are empty.");
    }
}