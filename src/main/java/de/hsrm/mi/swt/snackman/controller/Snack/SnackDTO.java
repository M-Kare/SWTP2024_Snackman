package de.hsrm.mi.swt.snackman.controller.Snack;

import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;
import de.hsrm.mi.swt.snackman.entities.Snack.SnackType;

public record SnackDTO(Position position, SnackType snackType) {

    public static SnackDTO fromSnack(Snack s) {
        return new SnackDTO(s.getPosition(), s.getSnackType());
    }
}



