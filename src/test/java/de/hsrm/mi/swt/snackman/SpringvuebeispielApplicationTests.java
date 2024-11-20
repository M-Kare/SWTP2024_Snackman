package de.hsrm.mi.swt.snackman;

import java.io.IOException;
import java.nio.file.Files;
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
		
	}

	@Test
	void contextLoads() {
	}

	@Test
	static void mazeExists(){
		List<String> maze = new LinkedList<String>();
		try {
			maze = Files.readAllLines(Paths.get("./Maze.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Assertions.assertTrue(maze.size() > 0, "Die Maze.txt ist leer");
		System.out.println();
	}
}
