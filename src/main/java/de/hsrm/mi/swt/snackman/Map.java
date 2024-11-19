package de.hsrm.mi.swt.snackman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Map {

    List<String> maze = new LinkedList<>();
    char[][] mazeArray;
    int wallSize = 1; //(Höhe, Breite, Tiefe der Wand)erstmal 1, später bitte anpassen

    public Map(){
        int size;

        try {
            maze = Files.readAllLines(Paths.get("mini-maze.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        size = maze.size();
        mazeArray = new char[size][size];

        //erstelle Labyrinth als Array
        for(int j = 0; j < maze.size(); j++){
            String line = maze.get(j);
            for(int i = 0; i < line.length(); i++){
                mazeArray[j][i] = line.charAt(i);
            }
        }
    }

    public char[][] getMazeAsArray(){
        return mazeArray;
    }

    public void printMap(){
        for(String line: maze){
            System.out.println(line);
        }
    }
}
