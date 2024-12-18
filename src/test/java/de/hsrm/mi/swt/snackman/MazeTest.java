package de.hsrm.mi.swt.snackman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.python.util.PythonInterpreter;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MazeTest {
    
    private static final String MAZE_SCRIPT_PATH = "./src/main/java/de/hsrm/mi/swt/snackman/Maze.py";
    private static final String MAZE_FILE_PATH = "./Maze.txt";

    @BeforeEach
    void executePythonScript() {
        Path mazeFilePath = Paths.get(MAZE_FILE_PATH);

        // Delete existing Maze.txt if it exists (clean state)
        if (Files.exists(mazeFilePath)) {
            try {
                Files.delete(mazeFilePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete existing Maze.txt", e);
            }
        }

        // Execute the Python script
        try (PythonInterpreter interpreter = new PythonInterpreter()) {
            interpreter.execfile(MAZE_SCRIPT_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Python script: " + MAZE_SCRIPT_PATH, e);
        }
    }

    @Test
    void mazeExists() {
        Path filePath = Paths.get(MAZE_FILE_PATH);
        Assertions.assertTrue(Files.exists(filePath), "The Maze.txt file does not exist!");
    }

    @Test
    void mazeHasContent() {
        List<String> maze = readMazeFile();

        Assertions.assertFalse(maze.isEmpty(), "The Maze.txt file is empty!");
    }

    @Test
    void mazeHasDefinedCharacters() {
        List<String> maze = readMazeFile();

        for (int i = 0; i < maze.size(); i++) {
            String line = maze.get(i);
            for (char c : line.toCharArray()) {
                Assertions.assertTrue(
                    c == ' ' || c == '#' || c == 'o' || c == '0' || c == 'S' || c == 'G' || c == 'C',
                    "Invalid character: '" + c + "' in maze at line " + i
                );
            }
        }
    }

    // Helper method to read the Maze.txt file
    private List<String> readMazeFile() {
        try {
            return Files.readAllLines(Paths.get(MAZE_FILE_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Maze.txt", e);
        }
    }
}