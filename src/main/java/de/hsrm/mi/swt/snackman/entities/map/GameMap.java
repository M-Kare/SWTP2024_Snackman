package de.hsrm.mi.swt.snackman.entities.map;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;

/**
 * Represents a game map
 */
public class GameMap {
    private final int DEFAULT_SQUARE_SIDE_LENGTH = GameConfig.SQUARE_SIZE;

    private final int DEFAULT_WALL_HEIGHT = GameConfig.SQUARE_HEIGHT;

    //Schachbrettform //TODO in englsich
    private Square[][] gameMap;

    /**
     * Constructs a new Map with the given map data
     *
     * @param map A square array representing the map
     */
    public GameMap(Square[][] map) {
        this.gameMap = map;
    }

    public int getDEFAULT_SQUARE_SIDE_LENGTH() {
        return DEFAULT_SQUARE_SIDE_LENGTH;
    }

    public int getDEFAULT_WALL_HEIGHT() {
        return DEFAULT_WALL_HEIGHT;
    }

    public Square[][] getGameMap() {
        return gameMap;
    }

    public Square getSquareAtIndexXZ(int x, int z) {
        return gameMap[x][z];
    }
}
