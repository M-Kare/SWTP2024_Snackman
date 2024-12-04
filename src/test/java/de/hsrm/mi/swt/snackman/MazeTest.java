package de.hsrm.mi.swt.snackman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.python.util.PythonInterpreter;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MazeTest {
    
    public MazeTest(){
        String path = System.getProperty("user.dir") + "/src/main/java/de/hsrm/mi/swt/snackman/Maze.py";

        try(PythonInterpreter interpreter = new PythonInterpreter()){
            interpreter.execfile(path);
        }
    }

    @Test
	void mazeExists(){
		String filename = "./Maze.txt";
        Path filePath = Path.of(filename);
        Assertions.assertTrue(Files.exists(filePath), "Die Datei existiert nicht!");

	}

	@Test
	void mazeHasContent(){
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
	void mazeHasDefindesCharacters(){
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
}
