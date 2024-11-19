package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.map.Map;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MapService {

    private Map map;

    private char[][] mazeData;

    public MapService() {
        this.mazeData = readMazeFromFile("mini-maze.txt");
        this.map = new Map(this.mazeData);
    }

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
