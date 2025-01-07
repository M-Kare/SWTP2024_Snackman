package de.hsrm.mi.swt.snackman.entities.mob.Chicken.Characters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;

public class TalaChickenTest2 {

    @Test
    public void talaChickenMovementTest2(){
        Chicken chicken = new Chicken("TalaChickenMovementSkript_new");

        List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W",
                                                    "SM", "L", "L", "L", "L",
                                                    "L", "W", "H", "W", "L",
                                                    "L", "W", "L", "W", "L",
                                                    "L", "W", "L", "W", "L");

        List<String> result = chicken.act(visibleEnvironment);

        //int chosenDirectionIndex = Integer.parseInt(result.get(12));
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }
}
