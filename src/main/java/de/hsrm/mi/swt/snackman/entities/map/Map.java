package de.hsrm.mi.swt.snackman.entities.map;

/**
 * Represents a game map
 */
public class Map {

    // representing map data with [x][z] coordinates
    private char[][] mapData;

    /**
     * Constructs a new Map with the given map data
     *
     * @param mapData A char array representing the initial state of the map
     */
    public Map(char[][] mapData) {
        this.mapData = mapData;
    }

    public char[][] getMapData() {
        return mapData;
    }

    public void setMapData(char[][] mapData) {
        this.mapData = mapData;
    }
}
