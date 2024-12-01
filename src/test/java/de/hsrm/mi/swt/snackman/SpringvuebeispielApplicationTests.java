package de.hsrm.mi.swt.snackman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import de.hsrm.mi.swt.snackman.services.MapService;

@SpringBootTest
class SnackmanApplicationTests {

	public static void main(String[] args){
		mazeExists();
		mazeHasContent();
		mazeHasDefindesCharacters();
		newMazeGeneratedWhenNewInstanceOfMapService();
	}

	@Test
	void contextLoads() {
	}

	@Test
	static void mazeExists(){
		String filename = "./Maze.txt";
        Path filePath = Path.of(filename);
        Assertions.assertTrue(Files.exists(filePath), "Die Datei existiert nicht!");

	}

	@Test
	static void mazeHasContent(){
		List<String> maze = new LinkedList<String>();
		try {
			maze = Files.readAllLines(Paths.get("./Maze.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Assertions.assertTrue(maze.size() > 0, "Die Maze.txt ist leer");
		System.out.println();
	}

	@Test
	static void mazeHasDefindesCharacters(){
		List<String> maze = new LinkedList<String>();
		
		try {
			maze = Files.readAllLines(Paths.get("./Maze.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i = 0; i <= maze.size()- 1; i++){
			String line = maze.get(i);
			for(char c : line.toCharArray()){
				Assertions.assertTrue(c == ' ' || c == '#' || c == 'o' || c == '0' || c == 'S' || c == 'G' || c == 'C', "Ungültiges Zeichen :" + c + " im Maze in Zeile : " + i);
			}
		}

	}

	@Test
	static void newMazeGeneratedWhenNewInstanceOfMapService(){
		List<String> mazeBeforeMapService = new LinkedList<String>();
		List<String> mazeAfterMapService = new LinkedList<String>();

		try {
			mazeBeforeMapService = Files.readAllLines(Paths.get("./Maze.txt"));		
		} catch (IOException e) {
			e.printStackTrace();
		}
		MapService mapService = new MapService();

		try {
			mazeAfterMapService = Files.readAllLines(Paths.get("./Maze.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Assertions.assertNotEquals(mazeAfterMapService, mazeBeforeMapService,"No new maze generated");
		
	}
}
