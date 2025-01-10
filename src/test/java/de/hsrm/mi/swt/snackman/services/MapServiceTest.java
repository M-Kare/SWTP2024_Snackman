package de.hsrm.mi.swt.snackman.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.FileSystemUtils;

import de.hsrm.mi.swt.snackman.SnackmanApplication;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Direction;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.messaging.EventType;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageEvent;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;

@SpringBootTest
class MapServiceTest {

	@Autowired
    private MapService mapService;

    @MockBean
    private FrontendMessageService frontendMessageService;

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
    void testMapServiceInitialization() {

        // Ensure mapService is properly initialized
        assertNotNull(mapService);

        // Add assertions to verify that mazeData and gameMap are properly set up
        GameMap gameMap = mapService.getGameMap();
        assertNotNull(gameMap);
    }


	@Test
	void testMazeDataToGameMapConversion(){
		char[][] mockMazeData = new char[][] {
            {'#', '#', '#'},
            {'#', '.', '#'},
            {'#', '#', '#'}
        };
		GameMap gameMap = mapService.convertMazeDataGameMap(mockMazeData);

		assertEquals(3, gameMap.getGameMap().length, "Game map should have 3 rows.");
        assertEquals(3, gameMap.getGameMap()[0].length, "Game map should have 3 columns.");
	}

	@Test
    void testGetSquaresVisibleForChicken() {
		char[][] mockMazeData = new char[][]{
                {'W', 'W', 'W', 'L', 'W'},
                {'W', 'L', 'L', 'L', 'L'},
                {'L', 'L', 'H', 'L', 'L'},
                {'L', 'L', 'L', 'L', 'L'},
                {'L', 'W', 'L', 'W', 'L'}
        };
        GameMap gameMap = mapService.convertMazeDataGameMap(mockMazeData);
        Square currentSquare = gameMap.getGameMap()[1][1]; // Assuming it's a floor square
        List<String> visibleSquares = mapService.getSquaresVisibleForChicken(currentSquare, Direction.ONE_NORTH);

        assertNotNull(visibleSquares, "Visible squares list should not be null.");
        assertEquals(26, visibleSquares.size(), "There should be 9 visible squares.");
    }

	    @Test
    void testAddRandomSnackToSquare() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        mapService.addRandomSnackToSquare(square);

        assertNotNull(square.getSnack(), "A snack should be added to the square.");
    }

    @Test
    void testGetSnackMan() {
        SnackMan snackMan = mapService.getSnackMan();
        assertNotNull(snackMan, "SnackMan should be initialized.");
    }

    @Test
    void testAddEggToSquare_EggAddedToSquare() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = new Snack(SnackType.EGG);

        mapService.addEggToSquare(square, egg);

        Assertions.assertNotNull(square.getSnack());
        Assertions.assertEquals(SnackType.EGG, square.getSnack().getSnackType());
    }

    @Test
    void testAddEggToSquare_EggAddedToSquare_CaseEggIsNull() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = null;

        Assertions.assertThrows(NullPointerException.class, () -> {
            mapService.addEggToSquare(square, egg);
        });

        Assertions.assertNull(square.getSnack());
    }

    @Test
    void testAddEggToSquare_EventTypeCorrect() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = new Snack(SnackType.EGG);
        // ArgumentCaptor is used to capture the actual arguments passed to a method when it is called on a mocked
        // object, allowing you to verify and test the parameters that were used in the method call
        ArgumentCaptor<FrontendMessageEvent> eventCaptor = ArgumentCaptor.forClass(FrontendMessageEvent.class);

        mapService.addEggToSquare(square, egg);

        Mockito.verify(frontendMessageService).sendEvent(eventCaptor.capture());
        FrontendMessageEvent capturedEvent = eventCaptor.getValue();
        Assertions.assertEquals(EventType.SNACK, capturedEvent.eventType());
    }

    @Test
    void testAddEggToSquare_SquareInEventCorrect() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = new Snack(SnackType.EGG);
        ArgumentCaptor<FrontendMessageEvent> eventCaptor = ArgumentCaptor.forClass(FrontendMessageEvent.class);

        mapService.addEggToSquare(square, egg);

        Mockito.verify(frontendMessageService).sendEvent(eventCaptor.capture());
        FrontendMessageEvent capturedEvent = eventCaptor.getValue();
        Assertions.assertNotNull(capturedEvent.square());

        Assertions.assertEquals(MapObjectType.FLOOR, capturedEvent.square().getType());
        Assertions.assertEquals(0, capturedEvent.square().getIndexX());
        Assertions.assertEquals(0, capturedEvent.square().getIndexZ());
    }

    @Test
    void testAddEggToSquare_SnackInEventCorrect() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = new Snack(SnackType.EGG);
        ArgumentCaptor<FrontendMessageEvent> eventCaptor = ArgumentCaptor.forClass(FrontendMessageEvent.class);

        mapService.addEggToSquare(square, egg);

        Mockito.verify(frontendMessageService).sendEvent(eventCaptor.capture());
        FrontendMessageEvent capturedEvent = eventCaptor.getValue();
        Assertions.assertNotNull(capturedEvent.square().getSnack());
        Assertions.assertEquals(SnackType.EGG, capturedEvent.square().getSnack().getSnackType());
    }

    @Test
    void testAddRandomSnackToSquare_WithoutEggs() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);

        mapService.addRandomSnackToSquare(square);

        Assertions.assertNotNull(square.getSnack());
        Assertions.assertNotEquals(SnackType.EGG, square.getSnack().getSnackType());
    }
}