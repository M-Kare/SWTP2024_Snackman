package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.entities.Snack.Snack;
import de.hsrm.mi.swt.snackman.entities.Square.Square;

import java.util.List;

public record SquareDTO(List<Snack> snacks) {

    public static SquareDTO fromSquare(Square s){
        return new SquareDTO(s.getSnacks());
    }

}
