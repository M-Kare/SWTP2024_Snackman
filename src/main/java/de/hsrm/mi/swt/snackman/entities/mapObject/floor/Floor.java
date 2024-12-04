package de.hsrm.mi.swt.snackman.entities.mapObject.floor;

import de.hsrm.mi.swt.snackman.entities.mapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.mapObject.TEXTURE;

public class Floor extends MapObject {

    public Floor() {
        super(TEXTURE.FLOOR);
    }

    @Override
    public String getSymbol() {
        return "L";
    }


}
