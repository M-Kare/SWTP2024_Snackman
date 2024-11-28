package de.hsrm.mi.swt.snackman.entities.mapObject;

/**
 * abstract class for general MapObjects – are then divided into eatable and walls
 */
public abstract class MapObject {

private TEXTURE texture;

    /**
     * Constructs a new MapObject with the specified texture
     *
     * @param texture texture to be applied to the object
     */
    public MapObject(TEXTURE texture) {
        this.texture = texture;
    }
}
