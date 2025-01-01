package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;
import de.hsrm.mi.swt.snackman.services.MapService;
import de.hsrm.mi.swt.snackman.services.ReadMazeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SnackManTest {

    @Autowired
    private FrontendMessageService frontendMessageService;

    @Autowired
    private ReadMazeService readMazeService;


    private SnackMan snackMan;

    @BeforeEach
    public void setUp() {
        MapService mapService = new MapService(frontendMessageService, readMazeService);
        snackMan = new SnackMan(mapService, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS);
        snackMan.setKcal(0);
    }

    @Test
    void testConsumeSnack() {
        Square firstSquare = new Square(new Snack(SnackType.APPLE), 0, 0);
        Square secondSquare = new Square(new Snack(SnackType.ORANGE), 0, 1);

        snackMan.consumeSnackOnSquare(firstSquare);
        assertEquals(snackMan.getKcal(), SnackType.APPLE.getCalories());
        assertNull(firstSquare.getSnack());

        snackMan.consumeSnackOnSquare(secondSquare);
        assertEquals(snackMan.getKcal(), SnackType.APPLE.getCalories() + SnackType.ORANGE.getCalories());
        assertNull(secondSquare.getSnack());
    }

    @Test
    void testMaxCalories() {
        Square square1  = new Square(new Snack(SnackType.ORANGE), 0, 0);
        Square square2 = new Square(new Snack(SnackType.ORANGE), 0, 1);
        Square square3 = new Square(new Snack(SnackType.ORANGE), 0, 2);
        Square square4  = new Square(new Snack(SnackType.ORANGE), 0, 0);
        Square square5 = new Square(new Snack(SnackType.ORANGE), 0, 1);
        Square square6 = new Square(new Snack(SnackType.ORANGE), 0, 3);

        snackMan.consumeSnackOnSquare(square1);
        snackMan.consumeSnackOnSquare(square2);
        snackMan.consumeSnackOnSquare(square3);
        snackMan.consumeSnackOnSquare(square4);
        snackMan.consumeSnackOnSquare(square5);
        snackMan.consumeSnackOnSquare(square6);

        assertEquals(snackMan.getKcal(), snackMan.getMAXKCAL());
        assertNull(square1.getSnack());
        assertNull(square2.getSnack());
        assertNull(square3.getSnack());
        assertNull(square4.getSnack());
        assertNull(square5.getSnack());







    }
}
