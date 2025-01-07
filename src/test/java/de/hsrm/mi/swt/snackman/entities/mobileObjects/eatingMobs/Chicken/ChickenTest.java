package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.services.MapService;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.Timer;

@SpringBootTest
class ChickenTest {

    @Autowired
    private MapService mapService;

    @Test
    void testLayEgg_ChickenThicknessAndKcalReset() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Chicken chicken = new Chicken(square, mapService);
        chicken.setKcal(3000);
        chicken.setThickness(Thickness.HEAVY);

        chicken.layEgg();

        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());
        Assertions.assertEquals(0, chicken.getKcal());
    }

    @Test
    void testStartNewTimer_ReplacesExistingTimer() throws NoSuchFieldException, IllegalAccessException {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Chicken chicken = new Chicken(square, mapService);

        // Set up an initial timer
        Timer initialTimer = new Timer();
        Field timerField = Chicken.class.getDeclaredField("eggLayingTimer");
        timerField.setAccessible(true);
        timerField.set(chicken, initialTimer);

        chicken.startNewTimer();

        Timer newTimer = (Timer) timerField.get(chicken);

        // Assert that the new timer is not the same as the initial timer
        Assertions.assertNotSame(initialTimer, newTimer);
        Assertions.assertNotNull(newTimer);
    }

    @ParameterizedTest
    @CsvSource({
            "true, false",
            "false, false"
    })
    void testStartNewTimer_ScaredStateAffectsDelay(boolean initialScaredState, boolean expectedScaredState) {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Chicken chicken = new Chicken(square, mapService);

        System.out.println("Initial scared state: " + initialScaredState);

        // Set the initial scared state
        chicken.setScared(initialScaredState);

        // Capture the time before starting the timer
        long startTime = System.currentTimeMillis();
        System.out.println("Start time: " + startTime);

        chicken.startNewTimer();

        long endTime = System.currentTimeMillis();
        System.out.println("End time: " + endTime);

        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " ms");

        // Check if the delay was applied when the chicken was initially scared
        if (initialScaredState) {
            System.out.println("Checking scared condition...");
            Assertions.assertTrue(elapsedTime >= 40000,
                    "Delay should be at least 40 seconds when scared. Actual: " + elapsedTime + " ms");
        } else {
            System.out.println("Checking not scared condition...");
            Assertions.assertTrue(elapsedTime >= 30000 && elapsedTime <= 60000,
                    "Delay should be between 30 and 60 seconds when not scared. Actual: " + elapsedTime + " ms");
        }

        System.out.println("Final scared state: " + chicken.isScared());
        Assertions.assertEquals(expectedScaredState, chicken.isScared(),
                "Expected scared state: " + expectedScaredState + ", Actual: " + chicken.isScared());
    }


    @Test
    void chickenGetsFatWhenComsumincSnacks(){
        Snack snack = new Snack(SnackType.STRAWBERRY);
        
        Square square = new Square(snack, 0, 0);
        mapService.setSquare(square, 0, 0);

        Chicken chicken = new Chicken(square, mapService);
        
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());

        snack.setSnackType(SnackType.ORANGE);

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.SLIGHTLY_THICK, chicken.getThickness());

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.MEDIUM, chicken.getThickness());

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.HEAVY, chicken.getThickness());


    }
}