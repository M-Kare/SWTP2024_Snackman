package de.hsrm.mi.swt.snackman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SnackmanApplicationTests {

	public static void main(String[] args){
		//SpringApplication.run(SnackmanApplicationTests.class, args);
		mazeExists();
		mazeHasContent();
		mazeHasDefindesCharacters();
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(int i = 0; i <= maze.size()- 1; i++){
			String line = maze.get(i);
			for(char c : line.toCharArray()){
				Assertions.assertTrue(c == ' ' || c == '#' || c == 'o' || c == '0' || c == 'S' || c == 'G' || c == 'C', "Ungültiges Zeichen :" + c + " im Maze in Zeile : " + i);
			}
		}

	}
}
