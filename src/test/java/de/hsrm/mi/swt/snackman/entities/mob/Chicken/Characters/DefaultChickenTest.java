package de.hsrm.mi.swt.snackman.entities.mob.Chicken.Characters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;

public class DefaultChickenTest {
    
    @Test
    public void chickenMovementTest(){
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("W", "L", "W", "L", "W", "L", "W", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        // The chicken should move to an empty space (" ").
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }

    @Test
    public void testChickenAvoidsGhost() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("L", "G", "L", "L", "L", "L", "L", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        // The chicken should move to an empty space (' ').
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should avoid ghosts ('G') and move to a safe square (' ').");
    }

    @Test
    public void testChickenDoesntSeeSnackMan() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("L", "G", "SM", "L", "L", "L", "L", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        // The chicken should move to an empty space (' ').
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should avoid ghosts ('G') and move to a safe square (' ').");
    }

    @Test
    public void testChickenMovesToSnack() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("L", "S", "L", "L", "L", "L", "L", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the snack ('S'), but the result should be the empty space (' ').");
    }

    @Test
    public void testChickenChoosesBetweenMultipleSnacks() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("S", "L", "L", "S", "L", "L", "L", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        // The chicken should choose the empty space (' ') for the selected snack ('S').
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should choose one of the snacks ('S') but the result should be ' '.");
    }

    @Test
    public void testChickenInComplexEnvironment() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("G", "L", "S", "L", "W", "S", "G", "W", "0");

        List<String> result = chicken.act(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should avoid ghosts, prioritize snacks, and move to an empty square (' ').");
    }

    @Test
    public void testChickenWithRandomSelection() {
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("L", "L", "L", "L", "L", "L", "L", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should choose a random empty space (' ') when all squares are empty.");
    }
}
