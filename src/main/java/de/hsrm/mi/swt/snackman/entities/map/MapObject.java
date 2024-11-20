package de.hsrm.mi.swt.snackman.entities.map;

import lombok.Getter;
import lombok.Setter;

/**
 * abstract class for general MapObjects – are then divided into eatable and walls
 */
@Setter
@Getter
public abstract class MapObject {

    private int posX;

    private int posY;

    private int posZ;

    private TEXTURE texture;

    /**
     * Constructs a new MapObject with the specified position and texture
     *
     * @param posX    x-coordinate of the object on the map
     * @param posY    y-coordinate of the object on the map
     * @param posZ    z-coordinate of the object on the map
     * @param texture texture to be applied to the object
     */
    public MapObject(int posX, int posY, int posZ, TEXTURE texture) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.texture = texture;
    }
}
