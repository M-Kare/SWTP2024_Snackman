package de.hsrm.mi.swt.snackman.services;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class MapServiceTest {

    @Autowired
    private FrontendMessageService frontendMessageService;

    @Autowired
    private ReadMazeService readMazeService;

    private MapService mapService;

    @BeforeEach
    public void setUp() {
        mapService = new MapService(frontendMessageService, readMazeService);
    }

    @Test
    void testCreatingMapFromFile_success() throws IOException {
        File mazeFile = new File("test_maze.txt");
        FileWriter writer = new FileWriter(mazeFile);
        writer.write("####\n");
        writer.write("#  #\n");
        writer.write("####\n");
        writer.close();

        mapService = new MapService(frontendMessageService, readMazeService,"test_maze.txt");

        MapObjectType[][] expectedMap = {
                {MapObjectType.WALL, MapObjectType.WALL, MapObjectType.WALL, MapObjectType.WALL}, // First row
                {MapObjectType.WALL, MapObjectType.FLOOR, MapObjectType.FLOOR, MapObjectType.WALL}, // Second row
                {MapObjectType.WALL, MapObjectType.WALL, MapObjectType.WALL, MapObjectType.WALL}  // Third row
        };

        GameMap gameMap = mapService.getGameMap();
        Square[][] map = gameMap.getGameMap();

        assertNotNull(map, "Map should not be null");

        for (int row = 0; row < expectedMap.length; row++) {
            for (int col = 0; col < expectedMap[row].length; col++) {
                assertEquals(
                        expectedMap[row][col],
                        map[row][col].getType(),
                        String.format("Mismatch at row %d, column %d", row, col)
                );

                // Check if a snack is available when floor
                if (map[row][col].getType() == MapObjectType.FLOOR) {
                    Snack snack = map[row][col].getSnack();
                    assertNotNull(snack,
                            String.format("Snack should not be null at row %d, column %d", row, col));
                }
            }
        }

        // Delete file after testing
        mazeFile.delete();
    }
}
