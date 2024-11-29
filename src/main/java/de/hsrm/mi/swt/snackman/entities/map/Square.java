package de.hsrm.mi.swt.snackman.entities.map;

import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;

import java.util.ArrayList;
import java.util.List;


//TODO add Javadoc
public class Square {

    private int indexX, indexZ;

    private MapObjectType type;

    private List<Snack> snacks;

    public Square(int indexX, int indexY) {
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

    public void addSnack(Snack snack) {
        //Only add Snack when it's actually a floor
        if(type == MapObjectType.FLOOR) {
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
}
