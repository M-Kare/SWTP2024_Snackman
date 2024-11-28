package de.hsrm.mi.swt.snackman.entities.MapObject.wall;

import de.hsrm.mi.swt.snackman.entities.MapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.MapObject.TEXTURE;

/**
 * Represents a wall in the game map
 * This class extends MapObject and adds properties specific to walls
 */
public class Wall extends MapObject {

    public static final int DEFAULT_HEIGHT = 5;

    /**
     * Constructs a new Wall
     */
    public Wall() {
        super(TEXTURE.WALL);
    }

    @Override
    public String getSymbol() {
        return "W";
    }
}
