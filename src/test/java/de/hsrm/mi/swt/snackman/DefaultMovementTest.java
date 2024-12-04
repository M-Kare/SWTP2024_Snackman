package de.hsrm.mi.swt.snackman;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;

@ExtendWith(MockitoExtension.class)
class DefaultMovementTest {
    
    @Mock
    MapService  mockMapService;
    private SnackMan snackman;

    @BeforeEach
    public void setup(){
        Square[][] emptyMap = { {new Square(0,0), new Square(0,1), new Square(0,2)},
                                {new Square(2,0), new Square(2,1), new Square(2,2)}, 
                                {new Square(2,0), new Square(2,1), new Square(2,2)} };
        GameMap gameMap = new GameMap(emptyMap);
        Mockito.when(mockMapService.getGameMap()).thenReturn(gameMap);

        snackman = new SnackMan(mockMapService);
    }


    @Test
    // Test if x/z map square index is correctly calculated
    void calculateMapSquareIndexFromPos(){
        double posX = 2.7813;
        assertEquals(2/GameConfig.SQUARE_SIZE, snackman.calcMapIndexOfCoordinate(posX));
    }



    @Test
    // Test if Snackman is moving forward (no rotation) when forward is true
    void moveForwardStandard(){
        double startPosX = snackman.getPosX();
        double startPosY = snackman.getPosY();
        double startPosZ = snackman.getPosZ();
        snackman.move(true, false, false, false, 0.1);
        assertTrue(startPosX == snackman.getPosX());
        assertTrue(startPosY == snackman.getPosY());
        assertTrue(startPosZ > snackman.getPosZ());
    }

    @Test
    // Test if Snackman is moving backward (no rotation) when forward is true
    void moveBackwardStandard(){
        double startPosX = snackman.getPosX();
        double startPosY = snackman.getPosY();
        double startPosZ = snackman.getPosZ();
        snackman.move(false, true, false, false, 0.1);
        assertTrue(startPosX == snackman.getPosX());
        assertTrue(startPosY == snackman.getPosY());
        assertTrue(startPosZ < snackman.getPosZ());
    }

    @Test
    // Test if Snackman is moving left(no rotation) when forward is true
    void moveLeftStandard(){
        double startPosX = snackman.getPosX();
        double startPosY = snackman.getPosY();
        double startPosZ = snackman.getPosZ();
        snackman.move(false, false, true, false, 0.1);
        assertTrue(startPosX > snackman.getPosX());
        assertTrue(startPosY == snackman.getPosY());
        assertTrue(startPosZ == snackman.getPosZ());
    }

    @Test
    // Test if Snackman is moving right (no rotation) when forward is true
    void moveRightStandard(){
        double startPosX = snackman.getPosX();
        double startPosY = snackman.getPosY();
        double startPosZ = snackman.getPosZ();
        snackman.move(false, false, false, true, 0.1);
        assertTrue(startPosX < snackman.getPosX());
        assertTrue(startPosY == snackman.getPosY());
        assertTrue(startPosZ == snackman.getPosZ());
    }

    @Test
    // Test if Snackman Forward/Backward movement is correct after rotating
    void rotateThenMoveNewForward(){
        double startPosX = snackman.getPosX();
        double startPosY = snackman.getPosY();
        double startPosZ = snackman.getPosZ();
        Quaterniond rotation = new Quaterniond();
        rotation.rotateAxis(Math.toRadians(90) ,new Vector3d(0,1,0));
        snackman.setQuaternion(rotation.x,rotation.y, rotation.z, rotation.w);
        snackman.move(true, false, false, false, 0.1);
        assertTrue(startPosX > snackman.getPosX());
        assertTrue(startPosY == snackman.getPosY());
        assertTrue(startPosZ == snackman.getPosZ());
    }

    @Test
    // Test if Snackman Left/Right movement is correct after rotating
    void rotateThenMoveNewLeft(){
        double startPosX = snackman.getPosX();
        double startPosY = snackman.getPosY();
        double startPosZ = snackman.getPosZ();
        Quaterniond rotation = new Quaterniond();
        rotation.rotateAxis(Math.toRadians(90) ,new Vector3d(0,1,0));
        snackman.setQuaternion(rotation.x,rotation.y, rotation.z, rotation.w);
        snackman.move(false, false, true, false, 0.1);
        assertTrue(startPosX == snackman.getPosX());
        assertTrue(startPosY == snackman.getPosY());
        assertTrue(startPosZ < snackman.getPosZ());
    }
    
    @Test
    // Tests if moving out of the map results in a respawn
    //TODO: Geht davon aus das Spawn in der Mitte der Map -> tatsächlichen Spawnpunkt nehmen, sobald MapService/GameMap SnackMan Spawnpunkt kennt
    void outOfMapEqualsRespawn(){
        int mapSize = mockMapService.getGameMap().getGameMap().length * GameConfig.SQUARE_SIZE;
        double spawnPosX = snackman.getPosX();
        double spawnPosY = snackman.getPosY();
        double spawnPosZ = snackman.getPosZ();
        snackman.move(snackman.getPosX()+mapSize, snackman.getPosY(), snackman.getPosZ());
        assertEquals(spawnPosX, snackman.getPosX());
        assertEquals(spawnPosY, snackman.getPosY());
        assertEquals(spawnPosZ, snackman.getPosZ());
    }
}
