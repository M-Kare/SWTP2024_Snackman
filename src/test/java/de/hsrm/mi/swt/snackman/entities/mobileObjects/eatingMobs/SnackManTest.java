package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import de.hsrm.mi.swt.snackman.SnackmanApplication;
import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mechanics.SprintHandler;
import de.hsrm.mi.swt.snackman.services.MapService;

@SpringBootTest
class SnackManTest {
    @Autowired
    private MapService mapService;

    private SprintHandler sprintHandler;

    private SnackMan snackMan;

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

    @BeforeEach
    public void setUp() {
        Square[][] emptyMap = { {new Square(0,0), new Square(0,1), new Square(0,2)},
                                {new Square(1,0), new Square(1,1), new Square(1,2)}, 
                                {new Square(2,0), new Square(2,1), new Square(2,2)} };
        GameMap gameMap = new GameMap(emptyMap);

        snackMan = new SnackMan(gameMap, 10, 0.3);
        snackMan.setKcal(0);
        snackMan.setPosY(GameConfig.SNACKMAN_GROUND_LEVEL);
        sprintHandler = mock(SprintHandler.class);
        snackMan.setSprintHandler(sprintHandler);
        snackMan.setSpeed(GameConfig.SNACKMAN_SPEED);
    }

    @Test void testConsumeSnack() {
        Square firstSquare = new Square(new Snack(SnackType.APPLE), 0, 0);
        Square secondSquare = new Square(new Snack(SnackType.ORANGE), 0, 1);

        snackMan.consumeSnackOnSquare(firstSquare);
        assertEquals(snackMan.getKcal(), SnackType.APPLE.getCalories(), "Snackman should consume the calories");
        assertEquals(firstSquare.getSnack().getSnackType(), SnackType.EMPTY, "After snacking the Snack the Square " +
                "should be empty.");

        snackMan.consumeSnackOnSquare(secondSquare);
        assertEquals(snackMan.getKcal(), SnackType.APPLE.getCalories() + SnackType.ORANGE.getCalories(), "Snacking " +
                "snacks should increase snackmans kcal");
        assertEquals(secondSquare.getSnack().getSnackType(), SnackType.EMPTY, "After snacking the Snack the " +
                "second Square should be empty");
    }

    @Test void testMaxCalories() {
        Square square1  = new Square(new Snack(SnackType.APPLE), 0, 0);
        square1.getSnack().setCalories(1000000);

        snackMan.consumeSnackOnSquare(square1);

        assertEquals(snackMan.getKcal(), 1000000);
        assertEquals(square1.getSnack().getSnackType(), SnackType.EMPTY);
    }

    @Test void testJump() {
        snackMan.setKcal(200);
        snackMan.jump();

        assertEquals(100, snackMan.getKcal());
        assertEquals(GameConfig.JUMP_STRENGTH, snackMan.getVelocityY());
        assertTrue(snackMan.isJumping());
    }

    @Test void testJumpInsufficientKcal() {
        snackMan.setKcal(50);
        snackMan.jump();
        assertEquals(50, snackMan.getKcal());
        assertEquals(0, snackMan.getVelocityY());
        assertFalse(snackMan.isJumping());
    }

    @Test void testDoubleJump() {
        snackMan.setKcal(300);
        snackMan.jump();
        snackMan.doubleJump();

        assertEquals(100, snackMan.getKcal());
        assertEquals(GameConfig.JUMP_STRENGTH + GameConfig.DOUBLEJUMP_STRENGTH, snackMan.getVelocityY());
    }

    @Test void testDoubleJumpWithoutEnoughKcal() {
        snackMan.setKcal(100);
        snackMan.jump();
        snackMan.doubleJump();

        assertEquals(0, snackMan.getKcal());
        assertEquals(GameConfig.JUMP_STRENGTH, snackMan.getVelocityY());
    }

    @Test void testUpdateJumpPosition() {
        snackMan.setKcal(100);
        snackMan.jump();
        double deltaTime = 0.016;

        assertTrue(snackMan.isJumping());
        assertTrue(snackMan.getVelocityY() > 0);
        snackMan.updateJumpPosition(deltaTime);

        while (snackMan.getPosY() > GameConfig.SNACKMAN_GROUND_LEVEL) {
            snackMan.updateJumpPosition(deltaTime);
        }

        assertEquals(GameConfig.SNACKMAN_GROUND_LEVEL, snackMan.getPosY());
        assertFalse(snackMan.isJumping());
        assertEquals(0, snackMan.getVelocityY());
    }


    @Test void testMoveWhileSprintingCanSprint() {
        when(sprintHandler.canSprint()).thenReturn(true);
        snackMan.setSprinting(true);

        snackMan.move(true, false, false, false, 0.016);
        verify(sprintHandler, times(1)).startSprint();
        assertEquals(GameConfig.SNACKMAN_SPEED * GameConfig.SNACKMAN_SPRINT_MULTIPLIER, snackMan.getSpeed());
    }

    @Test void testMoveWhileSprintingCannotSprint() {
        when(sprintHandler.canSprint()).thenReturn(false);
        snackMan.setSprinting(true);

        snackMan.move(true, false, false, false, 0.016);
        verify(sprintHandler, times(1)).stopSprint();
        assertEquals(GameConfig.SNACKMAN_SPEED, snackMan.getSpeed());
        assertFalse(snackMan.isSprinting());
    }

    @Test void testMoveNotSprinting() {
        snackMan.setSprinting(false);

        snackMan.move(true, false, false, false, 0.016);
        verify(sprintHandler, times(2)).stopSprint();
        assertEquals(GameConfig.SNACKMAN_SPEED, snackMan.getSpeed());
    }

    @Test void testSetSprintingCanSprint() {
        when(sprintHandler.canSprint()).thenReturn(true);

        snackMan.setSprinting(true);
        verify(sprintHandler, times(1)).startSprint();
        assertTrue(snackMan.isSprinting());
    }

    @Test void testSetSprintingCannotSprint() {
        when(sprintHandler.canSprint()).thenReturn(false);

        snackMan.setSprinting(true);
        verify(sprintHandler, never()).startSprint();
        assertFalse(snackMan.isSprinting());
    }

    @Test void testSetSprintingToFalse() {
        snackMan.setSprinting(false);

        verify(sprintHandler, times(1)).stopSprint();
        assertFalse(snackMan.isSprinting());
    }

    @Test void testMoveWithAllDirections() {
        snackMan.move(true, true, true, true, 0.016);
        assertEquals(GameConfig.SNACKMAN_SPEED, snackMan.getSpeed());
        verify(sprintHandler, never()).startSprint();
    }
}
