package de.hsrm.mi.swt.snackman.entities.map;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;

/**
 * Represents a game map
 */
public class GameMap {
    private final int DEFAULT_SQUARE_SIDE_LENGTH = GameConfig.SQUARE_SIZE;

    private final int DEFAULT_WALL_HEIGHT = GameConfig.SQUARE_HEIGHT;

    //Like a chessboard for better handling of collision
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
        if((x < 0 | x >= gameMap.length) | z < 0 | z >= gameMap[0].length){
            return new Square(MapObjectType.WALL, 0, 0); //returns pseudo-Suare Wall, because its out of hameMap
        }
        
        return gameMap[x][z];
    }

    public void setGameMap(Square square, int x, int y){
        gameMap[x][y] = square;
    }
}
