package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The direction the chicken could move into
 * North: move up in maze into direction of z
 * South: move down in maze into direction of x
 * this is what the coordinates look like
 ****************************** z *******************************
 *           (-1, -1)          (-1, 0)          (-1, 1)         *
 *         NORTHWEST           NORTH           NORTHEAST        *
 *              \                 |                 /           *
 *               \                |                /            *
 *                \               |               /             *
 *      (-1) ----- WEST ---- (0, 0) ---- EAST ------ (+1)       x
 *                /               |               \             *
 *               /                |                \            *
 *              /                 |                 \           *
 *         SOUTHWEST           SOUTH           SOUTHEAST        *
 *          (1, -1)            (1, 0)            (1, 1)         *
 *
 * 
 * 2N2W | 2NW  |  2N  | 2NO  | 2N2O  |
 *  N2W |  NW  |   N  |  NO  |  N2O  |
 *   2W |   W  |      |   O  |   2O  |
 *  S2W |  SW  |   S  |  SO  |  S2O  |
 * 2S2W | 2SW  |  2S  | 2SO  | 2S2O  |
 * --------------------------------------------------------------
 */
public enum Direction {
    NORTH_WEST("", -1, -1), NORTH("0", -1, 0), NORTH_EAST("", -1, 1), EAST("1", 0, 1), SOUTH_EAST("", 1, 1), SOUTH("2", 1, 0), SOUTH_WEST("", 1, -1), WEST("3", 0, -1),
    
    TWO_NORTH_TWO_WEST("", -2, -2), TWO_NORTH_ONE_WEST("", -2, -1), TWO_NORTH("", -2, 0), TWO_NORTH_ONE_EAST("", -2, 1), TWO_NORTH_TWO_EAST("", -2, 2),
    ONE_NORTH_TWO_WEST("", -1, -2), ONE_NORTH_ONE_WEST("", -1, -1), ONE_NORTH("", -1, 0), ONE_NORTH_ONE_EAST("", -1, 1), ONE_NORTH_TWO_EAST("", -1, 2),
    TWO_WEST("", 0, -2), ONE_WEST("", 0, -1), CHICKEN("", 0, 0), ONE_EAST("", 0, 1), TWO_EAST("", 0, 2),
    ONE_SOUTH_TWO_WEST("", 1, -2), ONE_SOUTH_ONE_WEST("", 1, -1), ONE_SOUTH("", 1, 0), ONE_SOUTH_ONE_EAST("", 1, 1), ONE_SOUTH_TWO_EAST("", 1, 2),
    TWO_SOUTH_TWO_WEST("", 2, -2), TWO_SOUTH_ONE_WEST("", 2, -1), TWO_SOUTH("", 2, 0), TWO_SOUTH_ONE_EAST("", 2, 1), TWO_SOUTH_TWO_EAST("", 2, 2);

    private final String indexOfList;
    private final int deltaX;
    private final int deltaZ;
    private static final Logger log = LoggerFactory.getLogger(Direction.class);

    Direction(String indexOfList, int deltaX, int deltaZ) {
        this.indexOfList = indexOfList;
        this.deltaX = deltaX;
        this.deltaZ = deltaZ;
    }

    /**
     * Return the Direction as an index used in the chicken movement python script
     *
     * @param indexOfList index of the list where the chicken want to move to
     * @return the direction the chicken wants to move towards
     */
    public static Direction getDirection(String indexOfList) {
        log.debug("Index of List: {}", indexOfList);
        return switch (indexOfList) {
            case "0" -> Direction.NORTH;
            case "1" -> Direction.EAST;
            case "2" -> Direction.SOUTH;
            case "3" -> Direction.WEST;
            case null, default -> throw new IndexOutOfBoundsException("Chicken is walking into a not defined direction.");
        };
    }

    /**
     * Gives back the new square-position of the chicken
     *
     * @param x,        z the current position of the chicken
     * @param direction in which the chicken decided to go
     * @return the square which is laying in the direction of the currentPosition
     */
    public synchronized Square getNewPosition(MapService mapService, int x, int z, Direction direction) {
        Square currentChickenPosition = mapService.getSquareAtIndexXZ(x, z);

        return mapService.getSquareAtIndexXZ(
                currentChickenPosition.getIndexX() + direction.deltaX,
                currentChickenPosition.getIndexZ() + direction.deltaZ
        );
    }

    public Square getNorthSquare(MapService mapService, Square currentPosition) {
        return mapService.getSquareAtIndexXZ(currentPosition.getIndexX() + NORTH.deltaX, currentPosition.getIndexZ() + NORTH.deltaZ);
    }

    public Square getEastSquare(MapService mapService, Square currentPosition) {
        return mapService.getSquareAtIndexXZ(currentPosition.getIndexX() + EAST.deltaX, currentPosition.getIndexZ() + EAST.deltaZ);
    }

    public Square getSouthSquare(MapService mapService, Square currentPosition) {
        return mapService.getSquareAtIndexXZ(currentPosition.getIndexX() + SOUTH.deltaX, currentPosition.getIndexZ() + SOUTH.deltaZ);
    }

    public Square getWestSquare(MapService mapService, Square currentPosition) {
        return mapService.getSquareAtIndexXZ(currentPosition.getIndexX() + WEST.deltaX, currentPosition.getIndexZ() + WEST.deltaZ);
    }

    public Square getNorthWestSquare(MapService mapService, Square currentPosition) {
        return mapService.getSquareAtIndexXZ(currentPosition.getIndexX() + NORTH_WEST.deltaX, currentPosition.getIndexZ() + NORTH_WEST.deltaZ);
    }

    public Square getNorthEastSquare(MapService mapService, Square currentPosition) {
        return mapService.getSquareAtIndexXZ(currentPosition.getIndexX() + NORTH_EAST.deltaX, currentPosition.getIndexZ() + NORTH_EAST.deltaZ);
    }

    public Square getSouthEastSquare(MapService mapService, Square currentPosition) {
        return mapService.getSquareAtIndexXZ(currentPosition.getIndexX() + SOUTH_EAST.deltaX, currentPosition.getIndexZ() + SOUTH_EAST.deltaZ);
    }

    public Square getSouthWestSquare(MapService mapService, Square currentPosition) {
        return mapService.getSquareAtIndexXZ(currentPosition.getIndexX() + SOUTH_WEST.deltaX, currentPosition.getIndexZ() + SOUTH_WEST.deltaZ);
    }

    @Override
    public String toString() {
        return indexOfList;
    }
}
