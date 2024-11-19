package de.hsrm.mi.swt.snackman.entities.map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class MapObject {

    private int posX;

    private int posY;

    private int posZ;

    private TEXTURE texture;

    public MapObject(int posX, int posY, int posZ, TEXTURE texture) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.texture = texture;
    }
}
