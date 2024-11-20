package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.map.Map;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing the game map
 * This class is responsible for loading and providing access to the maze data
 */
@Service
public class MapService {

    // map is currently "unnecessary", it will probably be needed as soon as snacks and other items are added
    private final Map map;

    private char[][] mazeData;

    /**
     * Constructs a new MapService
     * Initializes the maze data by reading from a file and creates a Map object
     */
    public MapService() {
        this.mazeData = readMazeFromFile("mini-maze.txt");
        this.map = new Map(this.mazeData);
    }

    /**
     * Reads maze data from a file and converts it into a char array with [x][z]-coordinates
     *
     * @param filePath the path to the file containing the maze data
     * @return a char array representing the maze
     * @throws RuntimeException if there's an error reading the file
     */
    private char[][] readMazeFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Lesen der Labyrinth-Datei", e);
        }

        int rows = lines.size();
        int cols = lines.getFirst().length();
        char[][] maze = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            maze[i] = lines.get(i).toCharArray();
        }
        return maze;
    }

    public char[][] getMazeAsArray() {
        return this.mazeData;
    }
}
