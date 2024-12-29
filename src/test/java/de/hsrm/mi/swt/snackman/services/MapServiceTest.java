package de.hsrm.mi.swt.snackman.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Spawnpoint;
import de.hsrm.mi.swt.snackman.entities.map.SpawnpointMobType;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;

@SpringBootTest
public class MapServiceTest {

    @Autowired
    private MapService mapService;

    public MapServiceTest(){

    }

    @Test
	void newMazeGeneratedWhenNewInstanceOfMapService(){
		List<String> mazeBeforeMapService = new LinkedList<>();
		List<String> mazeAfterMapService = new LinkedList<>();

		try {
			mazeBeforeMapService = Files.readAllLines(Paths.get("./Maze.txt"));		
		} catch (IOException e) {
			e.printStackTrace();
		}
		mapService.createNewGameMap("1");

		try {
			mazeAfterMapService = Files.readAllLines(Paths.get("./Maze.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Assertions.assertNotEquals(mazeAfterMapService, mazeBeforeMapService,"No new maze generated");
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
