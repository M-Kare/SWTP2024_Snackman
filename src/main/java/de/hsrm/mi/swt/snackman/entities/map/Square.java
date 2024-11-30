package de.hsrm.mi.swt.snackman.entities.map;

import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that represents a Square. A Square is part of the game map. Multiple squares representing a game map.
 */
public class Square {

    //It's static because the idCounter is the same for all Squares.
    private static long idCounter = 0;

    private long id;

    private int indexX, indexZ;

    private MapObjectType type;

    private List<Snack> snacks;

    public Square(int indexX, int indexY) {
        id = generateId();
        type = MapObjectType.FLOOR;
        snacks = new ArrayList<>();
        this.indexX = indexX;
        this.indexZ = indexY;
    }

    public Square(MapObjectType type, int indexX, int indexZ) {
        this(indexX, indexZ);
        this.type = type;
    }

    public Square(List<Snack> snacks, int indexX, int indexZ) {
        this(indexX, indexZ);
        this.snacks = snacks;
    }

    /**
     * Method to generate the next id of a new Square. It is synchronized because of thread-safety.
     * @return the next incremented id
     */
    private synchronized static long generateId() {
        return idCounter++;
    }

    public void addSnack(Snack snack) {
        //Only add Snack when it's actually a floor
        if (type == MapObjectType.FLOOR) {
            snacks.add(snack);
        }
    }

    public int getIndexX() {
        return indexX;
    }

    public int getIndexZ() {
        return indexZ;
    }

    public MapObjectType getType() {
        return type;
    }

    public List<Snack> getSnacks() {
        return snacks;
    }

    public long getId() {
        return id;
    }
}
