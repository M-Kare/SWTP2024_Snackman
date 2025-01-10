package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import de.hsrm.mi.swt.snackman.SnackmanApplication;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.services.MapService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;

@SpringBootTest
class ChickenTest {

    @Autowired
    private MapService mapService;

    private static final Path workFolder = Paths.get("./extensions").toAbsolutePath();

    @BeforeAll
    static void fileSetUp() {
        try{
            tearDownAfter();  
        }catch(Exception e){
            System.out.println("No file to delete");
        }   
        SnackmanApplication.checkAndCopyResources();
    }

    @AfterAll
    static void tearDownAfter() throws IOException {
        if (Files.exists(workFolder)) {
            FileSystemUtils.deleteRecursively(workFolder.toFile());
        }
    }

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
    void testLayEgg_ChickenThicknessAndKcalReset_caseIfChickenHasNoKcal() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Chicken chicken = new Chicken(square, mapService);

        chicken.layEgg();

        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());
        Assertions.assertEquals(0, chicken.getKcal());
        Assertions.assertTrue(chicken.wasTimerRestarted());
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
    void testStartNewTimer_ScaredStateAffectsDelay(boolean initialScaredState, boolean expectedScaredState) throws InterruptedException {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Chicken chicken = new Chicken(square, mapService);
        chicken.setKcal(2800);

        System.out.println("Initial scared state: " + initialScaredState);
        chicken.setScared(initialScaredState);

        long startTime = System.currentTimeMillis();
        System.out.println("Start time: " + startTime);

        chicken.startNewTimer();

        // Wait for the egg to be laid (kcal becomes 0) or timeout after 70 seconds
        long timeout = 70000; // 70 seconds
        long elapsedTime = 0;
        while (chicken.getKcal() > 0 && elapsedTime < timeout) {
            Thread.sleep(100);
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        System.out.println("Elapsed time: " + elapsedTime + " ms");
        System.out.println("Final kcal: " + chicken.getKcal());

        if (initialScaredState) {
            System.out.println("Checking scared condition...");
            Assertions.assertTrue(elapsedTime >= 30000 && elapsedTime <= 70000,
                    "Delay should be between 30 and 70 seconds when scared. Actual: " + elapsedTime + " ms");
        } else {
            System.out.println("Checking not scared condition...");
            Assertions.assertTrue(elapsedTime >= 30000 && elapsedTime <= 60000,
                    "Delay should be between 30 and 60 seconds when not scared. Actual: " + elapsedTime + " ms");
        }

        Assertions.assertEquals(0, chicken.getKcal(), "kcal should be 0 after laying an egg");

        System.out.println("Final scared state: " + chicken.isScared());
        Assertions.assertEquals(expectedScaredState, chicken.isScared(),
                "Expected scared state: " + expectedScaredState + ", Actual: " + chicken.isScared());
    }


    @Test
    void chickenGetsFatWhenComsumincSnacks() {
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