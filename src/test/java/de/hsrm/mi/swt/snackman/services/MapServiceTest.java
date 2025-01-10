package de.hsrm.mi.swt.snackman.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.SortedMap;

import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Direction;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;
import de.hsrm.mi.swt.snackman.entities.map.Spawnpoint;
import de.hsrm.mi.swt.snackman.entities.map.SpawnpointMobType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;

@SpringBootTest
class MapServiceTest {

    @Autowired
    private MapService mapService;


    @Test
    void testMapServiceInitialization() {

        // Ensure mapService is properly initialized
        assertNotNull(mapService);

        // Add assertions to verify that mazeData and gameMap are properly set up
        GameMap gameMap = mapService.createNewGameMap("1");
        assertNotNull(gameMap);
    }


    @Test
    void testMazeDataToGameMapConversion() {
        char[][] mockMazeData = new char[][]{
                {'#', '#', '#'},
                {'#', '.', '#'},
                {'#', '#', '#'}
        };
        GameMap gameMap = mapService.convertMazeDataGameMap("1", mockMazeData);

        assertEquals(3, gameMap.getGameMapSquares().length, "Game map should have 3 rows.");
        assertEquals(3, gameMap.getGameMapSquares()[0].length, "Game map should have 3 columns.");
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
        GameMap gameMap = mapService.convertMazeDataGameMap("1", mockMazeData);
        Square currentSquare = gameMap.getGameMapSquares()[1][1]; // Assuming it's a floor square
        Chicken chicken = new Chicken(currentSquare, gameMap);

        List<String> visibleSquares = chicken.getSquaresVisibleForChicken(gameMap, currentSquare, Direction.ONE_NORTH);

        assertNotNull(visibleSquares, "Visible squares list should not be null.");
        assertEquals(26, visibleSquares.size(), "There should be 26 visible squares.");
    }

    @Test
    void testAddRandomSnackToSquare() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        mapService.addRandomSnackToSquare(square);

        assertNotNull(square.getSnack(), "A snack should be added to the square.");
    }


    @Test
    void testAddEggToSquare_EggAddedToSquare() {
        char[][] mockMazeData = new char[][]{
                {'W', 'W', 'W', 'L', 'W'},
                {'W', 'L', 'L', 'L', 'L'},
                {'L', 'L', 'H', 'L', 'L'},
                {'L', 'L', 'L', 'L', 'L'},
                {'L', 'W', 'L', 'W', 'L'}
        };
        GameMap gameMap = mapService.convertMazeDataGameMap("1", mockMazeData);
        Square currentSquare = gameMap.getGameMapSquares()[1][1]; // Assuming it's a floor square
        Chicken chicken = new Chicken(currentSquare, gameMap);

        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = new Snack(SnackType.EGG);

        chicken.addEggToSquare(square, egg);

        Assertions.assertNotNull(square.getSnack());
        Assertions.assertEquals(SnackType.EGG, square.getSnack().getSnackType());
    }


    @Test
    void testAddEggToSquare_EggAddedToSquare_CaseEggIsNull() {
        char[][] mockMazeData = new char[][]{
                {'W', 'W', 'W', 'L', 'W'},
                {'W', 'L', 'L', 'L', 'L'},
                {'L', 'L', 'H', 'L', 'L'},
                {'L', 'L', 'L', 'L', 'L'},
                {'L', 'W', 'L', 'W', 'L'}
        };
        GameMap gameMap = mapService.convertMazeDataGameMap("1", mockMazeData);
        Square currentSquare = gameMap.getGameMapSquares()[1][1]; // Assuming it's a floor square
        Chicken chicken = new Chicken(currentSquare, gameMap);

        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = null;

        Assertions.assertThrows(NullPointerException.class, () -> {
            chicken.addEggToSquare(square, egg);
        });

        Assertions.assertNull(square.getSnack());
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
    public void spawnLocationTest() {
        Square[][] testMap = {{new Square(0, 0, new Spawnpoint(SpawnpointMobType.GHOST)), new Square(0, 1, new Spawnpoint(SpawnpointMobType.GHOST)), new Square(0, 2)},
                {new Square(1, 0), new Square(1, 1, new Spawnpoint(SpawnpointMobType.SNACKMAN)), new Square(1, 2)},
                {new Square(2, 0), new Square(2, 1, new Spawnpoint(SpawnpointMobType.SNACKMAN)), new Square(2, 2)}};
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
        Lobby testLobby = new Lobby("1", "testLobby", testClient01, gameMap);
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