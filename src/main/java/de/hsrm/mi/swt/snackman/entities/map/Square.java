package de.hsrm.mi.swt.snackman.entities.map;

import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Class that represents a Square. A Square is part of the game map. Multiple squares representing a game map.
 */
public class Square {
    //It's static because the idCounter is the same for all Squares.
    private static long idCounter = 0;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private long id;

    private int indexX, indexZ;

    private MapObjectType type;

    private Snack snack;

    public Square(int indexX, int indexZ) {
        id = generateId();
        type = MapObjectType.FLOOR;
        this.indexX = indexX;
        this.indexZ = indexZ;
    }

    public Square(MapObjectType type, int indexX, int indexZ) {
        this(indexX, indexZ);
        this.type = type;
    }

    public Square(Snack snack, int indexX, int indexZ) {
        this(indexX, indexZ);
        this.snack = snack;
    }

    /**
     * Method to generate the next id of a new Square. It is synchronized because of thread-safety.
     *
     * @return the next incremented id
     */
    private synchronized static long generateId() {
        return idCounter++;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
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

    public Snack getSnack() {
        return snack;
    }

    public void setSnack(Snack snack) {
        //Only add Snack when it's actually a floor
        if (type == MapObjectType.FLOOR) {

            this.snack = snack;
            propertyChangeSupport.firePropertyChange("square", null, this);
        }
    }

    public long getId() {
        return id;
    }

    /**
     *
     * @return the dominant type of MapObject
     */
    public String getPrimaryType() {
        if (type == MapObjectType.WALL) {
            return  "W";
        } else if (type == MapObjectType.FLOOR) {
            if(this.mobs.stream().anyMatch(mob -> mob instanceof Ghost)) return "G";
            //if(this.mobs.stream().anyMatch(mob -> mob instanceof Chicken)) return "C";
            else if(this.snack != null) return "S";
        }
        return "L";
    }

    public List<Mob> getMobs() {
        return mobs;
    }

    public void addMob(Mob mob) {
        this.mobs.add(mob);
    }

    public void removeMob(Mob mob) {
        this.mobs.remove(mob);
    }

    @Override
    public String toString() {
        return "Square{" +
                "indexX=" + indexX +
                ", indexZ=" + indexZ +
                ", type=" + type +
                ", snack=" + snack +
                ", mobs=" + mobs +
                '}';
    }
}
