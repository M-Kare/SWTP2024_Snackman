package de.hsrm.mi.swt.snackman.entities.map;

public class Map {

    private char[][] mapData;

    public Map(char[][] mapData) {
        this.mapData = mapData;
    }

    public char[][] getMapData() {
        return mapData;
    }
}
