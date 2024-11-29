package de.hsrm.mi.swt.snackman.entities.mapObject.snack;

import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObject;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;

/**
 * Class that defines Snack, that can be eaten by Snackman
 */
public class Snack extends MapObject {
    //TODO Is position realy needed?
    private Position position;
    private SnackType snackType;
    private int calories;

    public Snack(SnackType snackType) {
        super(MapObjectType.SNACK);
        this.snackType = snackType;
        this.calories = snackType.getCalories();
    }

    public SnackType getSnackType() {
        return snackType;
    }

    public void setSnackType(SnackType snackType) {
        this.snackType = snackType;
    }

    public int getCalories() {
        return calories;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
