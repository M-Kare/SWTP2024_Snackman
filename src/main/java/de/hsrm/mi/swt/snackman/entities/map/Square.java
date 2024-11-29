package de.hsrm.mi.swt.snackman.entities.map;

import de.hsrm.mi.swt.snackman.entities.mapObject.MapObject;

import java.util.ArrayList;
import java.util.List;

public class Square {

    public static final int DEFAULT_SIDE_LENGTH = 1;
    private int indexX, indexZ;
    private List<MapObject> mapObjects;

    public Square(int indexX, int indexY) {
        this.mapObjects = new ArrayList<>();
        this.indexX = indexX;
        this.indexZ = indexY;
    }

    public Square(List<MapObject> mapObjects, int indexX, int indexZ) {
        this(indexX, indexZ);
        this.mapObjects = mapObjects;
    }

    public void addObjectToMap(MapObject mapObject) {
        mapObjects.add(mapObject);
    }

    public List<MapObject> getMapObjects() {
        return mapObjects;
    }

    public int getIndexX() {
        return indexX;
    }

    public int getIndexZ() {
        return indexZ;
    }
}
