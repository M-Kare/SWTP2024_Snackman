package de.hsrm.mi.swt.snackman.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;

import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.messaging.EventType;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageEvent;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Direction;
import org.springframework.boot.test.mock.mockito.MockBean;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Spawnpoint;
import de.hsrm.mi.swt.snackman.entities.map.SpawnpointMobType;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;

@SpringBootTest
class MapServiceTest {

	@Autowired
    private MapService mapService;

    @MockBean
    private FrontendMessageService frontendMessageService;

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

    @Test
    /*
     * Map:
     * 	Ghost	Ghost		Empty
     * 	Empty	SnackMan	Empty
     * 	Empty	SnackMan	Empty
     */

    public void spawnLocationTest(){
        Square[][] testMap = { {new Square(0,0, new Spawnpoint(SpawnpointMobType.GHOST)), new Square(0,1, new Spawnpoint(SpawnpointMobType.GHOST)), new Square(0,2)},
                {new Square(1,0), new Square(1,1, new Spawnpoint(SpawnpointMobType.SNACKMAN)), new Square(1,2)},
                {new Square(2,0), new Square(2,1, new Spawnpoint(SpawnpointMobType.SNACKMAN)), new Square(2,2)} };
        GameMap gameMap = new GameMap(testMap);
        PlayerClient testClient01 = new PlayerClient("01", "testClient01");
        PlayerClient testClient02 = new PlayerClient("02", "testClient02");
        PlayerClient testClient03 = new PlayerClient("03", "testClient03");
        PlayerClient testClient04 = new PlayerClient("04", "testClient04");
        PlayerClient testClient05 = new PlayerClient("05", "testClient05");
        PlayerClient testClient06 = new PlayerClient("06", "testClient06");
        testClient01.setRole(ROLE.SNACKMAN);
        testClient02.setRole(ROLE.GHOST);
        testClient03.setRole(ROLE.GHOST);
        testClient04.setRole(ROLE.GHOST);
        testClient05.setRole(ROLE.SNACKMAN);
        testClient06.setRole(ROLE.SNACKMAN);
        Lobby testLobby = new Lobby("1", "testLobby", testClient01, false, gameMap);
        testLobby.getMembers().add(testClient02);
        testLobby.getMembers().add(testClient03);
        testLobby.getMembers().add(testClient04);
        testLobby.getMembers().add(testClient05);
        testLobby.getMembers().add(testClient06);
        mapService.spawnMobs(gameMap, testLobby);
        SortedMap<String, Mob> clientMobs = testLobby.getClientMobMap();

        Assertions.assertTrue(clientMobs.get("01").getCurrentSquare() == testMap[1][1]);
        Assertions.assertTrue(clientMobs.get("02").getCurrentSquare() == testMap[0][0]);
        Assertions.assertTrue(clientMobs.get("03").getCurrentSquare() == testMap[0][1]);
        Assertions.assertTrue(clientMobs.get("04").getCurrentSquare() == testMap[0][0]);
        Assertions.assertTrue(clientMobs.get("05").getCurrentSquare() == testMap[2][1]);
        Assertions.assertTrue(clientMobs.get("06").getCurrentSquare() == testMap[1][1]);
    }

}
}