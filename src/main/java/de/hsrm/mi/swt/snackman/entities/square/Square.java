package de.hsrm.mi.swt.snackman.entities.square;

import de.hsrm.mi.swt.snackman.entities.mapObject.MapObject;

import java.util.List;

public class Square {

    public static final int DEFAULT_SIDE_LENGTH = 1;

    private List<MapObject> mapObjects;

    public Square(List<MapObject> mapObjects) {
        this.mapObjects = mapObjects;
    }

    public List<MapObject> getMapObjects() {
        return mapObjects;
    }
}
