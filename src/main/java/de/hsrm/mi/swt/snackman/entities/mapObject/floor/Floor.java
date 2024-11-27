package de.hsrm.mi.swt.snackman.entities.MapObject.floor;

import de.hsrm.mi.swt.snackman.entities.MapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.mapObject.TEXTURE;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true) // note fields of the superclass MapObject (because abstract)
@Getter
@Setter
public class Floor extends MapObject {

    public Floor() {
        super(TEXTURE.FLOOR);
    }

    @Override
    public String getSymbol() {
        return "L";
    }
}
