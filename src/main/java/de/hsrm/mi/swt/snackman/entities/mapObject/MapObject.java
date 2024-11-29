package de.hsrm.mi.swt.snackman.entities.mapObject;

/**
 * abstract class for general MapObjects – are then divided into eatable and walls
 */
public abstract class MapObject {
    private MapObjectType type;

    /**
     * Constructs a new MapObject with the specified texture
     */
    public MapObject(MapObjectType type) {
        this.type = type;
    }

    public MapObjectType getType() {
        return type;
    }
}

