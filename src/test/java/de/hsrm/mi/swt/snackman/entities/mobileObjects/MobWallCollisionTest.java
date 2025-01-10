package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

import de.hsrm.mi.swt.snackman.SnackmanApplication;
import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;

@ExtendWith(MockitoExtension.class)
class MobWallCollisionTest {
    
    @Mock
    MapService  mockMapService;
    private SnackMan snackman;

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
    public void setup(){
        Square[][] testMap1 = { {new Square(0,0), new Square(0,1), new Square(MapObjectType.WALL,0,2)},
                                {new Square(MapObjectType.WALL,1,0), new Square(1,1), new Square(1,2)}, 
                                {new Square(2,0), new Square(MapObjectType.WALL,2,1), new Square(2,2)} };
        GameMap gameMap = new GameMap(testMap1);
        Mockito.when(mockMapService.getGameMap()).thenReturn(gameMap);

        snackman = new SnackMan(mockMapService, 10, 0.3);
    }

    @Test
    void checkCollisionNoWallLeft(){
        double xNew = snackman.getPosX() - (GameConfig.SQUARE_SIZE/2) + (snackman.getRadius()/2);
        double zNew = snackman.getPosZ();
        assertEquals(0, snackman.checkWallCollision(xNew, zNew));
    }

    @Test
    void checkCollisionWallVertical(){
        double xNew = snackman.getPosX();
        double zNew = snackman.getPosZ() - (GameConfig.SQUARE_SIZE/2) + (snackman.getRadius()/2);
        assertEquals(2, snackman.checkWallCollision(xNew, zNew));
    }

    @Test
    void checkCollisionWallHorizontal(){
        double xNew = snackman.getPosX() + (GameConfig.SQUARE_SIZE/2) - (snackman.getRadius()/2);
        double zNew = snackman.getPosZ();
        assertEquals(1, snackman.checkWallCollision(xNew, zNew));
    }

    @Test
    void checkCollisionWallDiagonal(){
        double xNew = snackman.getPosX() - (GameConfig.SQUARE_SIZE/2) + (snackman.getRadius()/2);
        double zNew = snackman.getPosZ() + (GameConfig.SQUARE_SIZE/2) - (snackman.getRadius()/2);;
        assertEquals(3, snackman.checkWallCollision(xNew, zNew));
    }

    @Test
    void checkCollisionWallRightTop(){
        double xNew = snackman.getPosX() + (GameConfig.SQUARE_SIZE/2) - (snackman.getRadius()/2);
        double zNew = snackman.getPosZ() - (GameConfig.SQUARE_SIZE/2) + (snackman.getRadius()/2);;
        assertEquals(3, snackman.checkWallCollision(xNew, zNew));
    }
}
