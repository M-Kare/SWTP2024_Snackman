package de.hsrm.mi.swt.snackman.entities.map;

/**
 * Represents a game map
 */
public class Map {

    // representing the map which have squares with [x][z] coordinates
    private Square[][] maze;

    /**
     * Constructs a new Map with the given map data
     *
     * @param maze A square array representing the map
     */
    public Map(Square[][] maze) {
        this.maze = maze;
    }

}
