package de.hsrm.mi.swt.snackman.entities.map;


import java.util.List;

/**
 * Represents a game map
 */
public class GameMap {
    private final int DEFAULT_SQUARE_SIDE_LENGTH = 1;

    private final int DEFAULT_WALL_HEIGHT = 5;

    private List<Square> gameMap;


    /**
     * Constructs a new Map with the given map data
     *
     * @param map A square array representing the map
     */
    public GameMap(List<Square> map) {
        this.gameMap = map;
    }


    public int getDEFAULT_SQUARE_SIDE_LENGTH() {
        return DEFAULT_SQUARE_SIDE_LENGTH;
    }

    public int getDEFAULT_WALL_HEIGHT() {
        return DEFAULT_WALL_HEIGHT;
    }

    public List<Square> getGameMap() {
        return gameMap;
    }
}
