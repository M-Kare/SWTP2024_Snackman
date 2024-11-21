package de.hsrm.mi.swt.snackman.controller.Snack;

import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;
import de.hsrm.mi.swt.snackman.entities.Snack.SnackType;

public record SnackDTO(double height, Position position, SnackType snacktype) {

    public static SnackDTO fromSnack(Snack s){
        return new SnackDTO(s.getHEIGHT(), s.getPosition(), s.getSnackType());
    }
}
