package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import de.hsrm.mi.swt.snackman.SnackmanApplication;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;

@SpringBootTest
class ChickenTest {

    @Autowired
    private MapService mapService;

    private GameMap gameMap;

    private static final Path workFolder = Paths.get("./extensions").toAbsolutePath();

    @BeforeAll
    static void fileSetUp() {
        try {
            tearDownAfter();
        } catch (Exception e) {
            System.out.println("No file to delete");
        }
        SnackmanApplication.checkAndCopyResources();
        assert Files.exists(workFolder.resolve("chicken"));
        assert Files.exists(workFolder.resolve("chicken/ChickenMovementSkript.py"));
    }

    @AfterAll
    static void tearDownAfter() {
        if (Files.exists(workFolder)) {
            FileSystemUtils.deleteRecursively(workFolder.toFile());
        }
    }


    @BeforeEach
    void setUp() {
        if (!Files.exists(workFolder.resolve("chicken/ChickenMovementSkript.py"))) {
            SnackmanApplication.checkAndCopyResources();
        }
        char[][] mockMazeData = new char[][] {
                {'#', '#', '#'},
                {'#', '.', '#'},
                {'#', '#', '#'}
        };
        gameMap = mapService.convertMazeDataGameMap("1", mockMazeData);
    }

    @Test
    void testLayEgg_ChickenThicknessAndKcalReset() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);

        Chicken chicken = new Chicken(square, gameMap);
        chicken.setKcal(3000);
        chicken.setThickness(Thickness.HEAVY);

        chicken.layEgg();

        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());
        Assertions.assertEquals(0, chicken.getKcal());
    }


    @Test
    void testLayEgg_ChickenThicknessAndKcalReset_caseIfChickenHasNoKcal() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Chicken chicken = new Chicken(square, gameMap);

        chicken.layEgg();

        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());
        Assertions.assertEquals(0, chicken.getKcal());
        Assertions.assertTrue(chicken.wasTimerRestarted());
    }

    @Test
    void testStartNewTimer_ReplacesExistingTimer() throws NoSuchFieldException, IllegalAccessException {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Chicken chicken = new Chicken(square, gameMap);

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

    @Test
    void chickenGetsFatWhenComsumincSnacks() {
        Snack snack = new Snack(SnackType.STRAWBERRY);

        Square square = gameMap.getSquareAtIndexXZ(0, 0);
        square.setType(MapObjectType.FLOOR);
        square.setSnack(snack);

        Chicken chicken = new Chicken(square, gameMap);

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