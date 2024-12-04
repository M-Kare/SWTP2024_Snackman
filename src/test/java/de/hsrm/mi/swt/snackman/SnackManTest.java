package de.hsrm.mi.swt.snackman;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;
import de.hsrm.mi.swt.snackman.services.ReadMazeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SnackManTest {

    private SnackMan snackMan;

    @BeforeEach
    public void setUp() {
        snackMan = new SnackMan(new MapService(new ReadMazeService()));
    }

    @Test
    void testConsumeSnack() {
        Square firstSquare = new Square(new Snack(SnackType.APPLE), 0, 0);
        Square secondSquare = new Square(new Snack(SnackType.ORANGE), 0, 1);

        snackMan.consumeSnackOnSquare(firstSquare);
        assertEquals(snackMan.getCurrentCalories(), SnackType.APPLE.getCalories(), "After consuming an Apple the " +
                "calories of snackman should increase.");
        assertNull(firstSquare.getSnack(), "After consuming an snack on the squarem the square should not have a" +
                "snack anymore.");

        snackMan.consumeSnackOnSquare(secondSquare);
        assertEquals(snackMan.getCurrentCalories(), SnackType.APPLE.getCalories() + SnackType.ORANGE.getCalories(), "After " +
                "consuming an Apple and a Orange the calories of Snackman should be the sum of both snacks.");
        assertNull(secondSquare.getSnack(), "After consuming an snack on the squarem the square should not have a" +
                "snack anymore.");
    }
}
