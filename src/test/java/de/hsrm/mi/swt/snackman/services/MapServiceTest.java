package de.hsrm.mi.swt.snackman.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Direction;

@SpringBootTest
class MapServiceTest {

	@Autowired
    private MapService mapService;

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
		char[][] mockMazeData = new char[][] {
            {'#', '#', '#'},
            {'#', '.', '#'},
            {'#', '#', '#'}
        };
        GameMap gameMap = mapService.convertMazeDataGameMap(mockMazeData);
        Square currentSquare = gameMap.getGameMap()[1][1]; // Assuming it's a floor square
        List<String> visibleSquares = mapService.getSquaresVisibleForChicken(currentSquare, Direction.NORTH);

        assertNotNull(visibleSquares, "Visible squares list should not be null.");
        assertEquals(9, visibleSquares.size(), "There should be 9 visible squares.");
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
}
