package de.hsrm.mi.swt.snackman.entities.MapObject.floor;

import de.hsrm.mi.swt.snackman.entities.MapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.MapObject.TEXTURE;

public class Floor extends MapObject {

    public Floor() {
        super(TEXTURE.FLOOR);
    }

    @Override
    public String getSymbol() {
        return "L";
    }


}
