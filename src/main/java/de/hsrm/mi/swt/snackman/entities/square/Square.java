package de.hsrm.mi.swt.snackman.entities.square;

import de.hsrm.mi.swt.snackman.entities.MapObject.EatableItem;
import de.hsrm.mi.swt.snackman.entities.MapObject.MapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Square {

    public static final int DEFAULT_SIDE_LENGTH = 1;

    private List<MapObject> mapObjects;

    private int indexX;

    private int indexZ;

    /**
     * initializes the square with the
     *
     * @param indexX x of the square in the map
     * @param indexZ y of the square in the map
     */
    public Square(int indexX, int indexZ) {
        this.indexX = indexX;
        this.indexZ = indexZ;
        mapObjects = new ArrayList<>();
    }

    public void addMapObject(MapObject mapObject) {
        this.mapObjects.add(mapObject);
    }

    public synchronized boolean hasSnack() {
        return this.mapObjects.stream().anyMatch(mapObject -> mapObject.getSymbol().equals("S"));
    }

    /**
     *
     * @return the dominant type of MapObject
     */
    public String getPrimaryType() {
        if (this.mapObjects.stream().anyMatch(mapObject -> mapObject.getSymbol().equals("W")))
            return "W";
        else if (this.mapObjects.stream().anyMatch(mapObject -> mapObject.getSymbol().equals("G")))
            return "G";
        else if (this.mapObjects.stream().anyMatch(mapObject -> mapObject.getSymbol().equals("S")))
            return "S";
        else
            return "L";
    }

    public int getKcal() {
        int kcal = 0;
        for (MapObject mapObject : this.mapObjects) {
            if (mapObject instanceof EatableItem) {
                kcal += ((EatableItem) mapObject).getKcal();
            }
        }
        return kcal;
    }

    public void deleteAllSnacks() {
        List<EatableItem> snacksToRemove = mapObjects.stream()
                .filter(mapObject -> mapObject instanceof EatableItem)
                .map(mapObject -> (EatableItem) mapObject)
                .collect(Collectors.toList());

        mapObjects.removeAll(snacksToRemove);
    }

    public int getDEFAULT_SIDE_LENGTH() {
        return DEFAULT_SIDE_LENGTH;
    }

    public int getIndexX() {
        return indexX;
    }

    public int getIndexZ() {
        return indexZ;
    }

    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    public void setIndexZ(int indexZ) {
        this.indexZ = indexZ;
    }

    public Square(List<MapObject> mapObjects) {
        this.mapObjects = mapObjects;
    }

    public List<MapObject> getMapObjects() {
        return mapObjects;
    }

    public void setMapObjects(List<MapObject> mapObjects) {
        this.mapObjects = mapObjects;
    }
}
