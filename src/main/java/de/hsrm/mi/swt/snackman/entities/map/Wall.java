package de.hsrm.mi.swt.snackman.entities.map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a wall in the game map
 * This class extends MapObject and adds properties specific to walls
 */
@EqualsAndHashCode(callSuper = true) // note fields of the superclass MapObject (because abstract)
@Getter
@Setter
public class Wall extends MapObject {

    private int width;

    private int height;
    
    private int depth;

    /**
     * Constructs a new Wall with the specified position and dimensions
     *
     * @param posX   x-coordinate of the wall on the map
     * @param posY   y-coordinate of the wall on the map
     * @param posZ   z-coordinate of the wall on the map
     * @param width  width of the wall
     * @param height height of the wall
     * @param depth  depth of the wall
     */
    public Wall(int posX, int posY, int posZ, int width, int height, int depth) {
        super(posX, posY, posZ, TEXTURE.WALL);
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
}
