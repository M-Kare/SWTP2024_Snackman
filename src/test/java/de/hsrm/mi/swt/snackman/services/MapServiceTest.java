package de.hsrm.mi.swt.snackman.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MapServiceTest {

    private MapService mapService;

    @TempDir
    Path tempDir; // Creates a temporary directory for each test

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary maze file for testing
        Path mazePath = tempDir.resolve("test-maze.txt");
        Files.writeString(mazePath, """
                ###
                # #
                ###
                """);

        // Initialize MapService and set the file path to the temporary maze file
        mapService = new MapService();
        mapService.setFilePath(mazePath.toAbsolutePath().toString());
        mapService.setMazeData(mapService.readMazeFromFile(mapService.getFilePath())); // Read the maze data
    }

    @Test
    void testMazeInitialization() {
        char[][] maze = mapService.getMazeAsArray();
        assertNotNull(maze);
        assertEquals(3, maze.length); // Check number of rows
        assertEquals(3, maze[0].length); // Check number of columns
    }

    @Test
    void testMazeContent() {
        char[][] maze = mapService.getMazeAsArray();
        assertEquals('#', maze[0][0]); // Check specific content
        assertEquals(' ', maze[1][1]); // Check specific content
        assertEquals('#', maze[2][2]); // Check specific content
    }

    @Test
    void testInvalidFilePath() {
        // Create an instance of MapService
        mapService = new MapService();

        // Set an invalid file path and expect a RuntimeException when trying to read it
        mapService.setFilePath("non-existent-file.txt");

        // Attempt to read from the invalid file path and assert that a RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mapService.setMazeData(mapService.readMazeFromFile(mapService.getFilePath()));
        });

        // Update this line to match the actual exception message
        assertEquals("Fehler beim Lesen der Labyrinth-Datei", exception.getMessage());
    }
}