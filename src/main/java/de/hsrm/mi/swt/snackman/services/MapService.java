package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.map.Map;
import de.hsrm.mi.swt.snackman.entities.map.Wall;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MapService {

    private Map map;

    private char[][] mazeData;

    @PostConstruct
    public void init() {
        this.mazeData = readMazeFromFile("mini-maze.txt");
    }

    public MapService() {
        this.map = new Map(mazeData);
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

    public List<Wall> getWalls() {
        List<Wall> walls = new ArrayList<>();
        char[][] mazeData = map.getMapData();
        for (int y = 0; y < mazeData.length; y++) {
            for (int x = 0; x < mazeData[y].length; x++) {
                if (mazeData[y][x] == '#') {
                    walls.add(new Wall(x, y, 0, 1, 1, 1));
                }
            }
        }
        return walls;
    }

    public char[][] getMazeAsArray() {
        return this.mazeData;
    }
}
