package de.hsrm.mi.swt.snackman.entities.MapObject;

import de.hsrm.mi.swt.snackman.entities.mapObject.TEXTURE;
/**
 * abstract class for general MapObjects – are then divided into eatable and walls
 */
public abstract class MapObject {

private TEXTURE texture;

    /**
     * Returns the type of the MapObject:
     *
     * W = Wand
     * L = Leer
     * S = Snack
     * G = Geist
     *
     * @return the type
     */
    public abstract String getSymbol();

    /**
     * Constructs a new MapObject with the specified texture
     *
     * @param texture texture to be applied to the object
     */
    public MapObject(TEXTURE texture) {
        this.texture = texture;
    }
}
