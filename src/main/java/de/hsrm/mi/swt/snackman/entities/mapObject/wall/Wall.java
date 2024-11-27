package de.hsrm.mi.swt.snackman.entities.MapObject.wall;

import de.hsrm.mi.swt.snackman.entities.MapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.mapObject.TEXTURE;
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
