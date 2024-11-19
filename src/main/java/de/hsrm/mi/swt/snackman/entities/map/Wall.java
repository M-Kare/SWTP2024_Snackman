package de.hsrm.mi.swt.snackman.entities.map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Wall extends MapObject {

    private int width;

    private int height;
    
    private int depth;

    public Wall(int posX, int posY, int posZ, int width, int height, int depth) {
        super(posX, posY, posZ, TEXTURE.WALL);
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
}
