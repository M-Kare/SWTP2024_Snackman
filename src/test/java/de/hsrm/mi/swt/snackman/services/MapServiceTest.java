package de.hsrm.mi.swt.snackman.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
class MapServiceTest {

	@Autowired
    private MapService mapService;

    @Test
	void newMazeGeneratedWhenNewInstanceOfMapService(){
		List<String> mazeBeforeMapService = new LinkedList<String>();
		List<String> mazeAfterMapService = new LinkedList<String>();

		try {
			mazeBeforeMapService = Files.readAllLines(Paths.get("./Maze.txt"));		
		} catch (IOException e) {
			e.printStackTrace();
		}
		mapService.generateNewMaze();

		try {
			mazeAfterMapService = Files.readAllLines(Paths.get("./Maze.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Assertions.assertNotEquals(mazeAfterMapService, mazeBeforeMapService,"No new maze generated");
		
	}
    
}
