package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.controller.Snack.SnackDTO;
import de.hsrm.mi.swt.snackman.entities.Square.Square;

import java.util.List;

public record SquareDTO(List<SnackDTO> snacks, Position position, double sideLength) {

    public static SquareDTO fromSquare(Square s) {
        var snackDTOs = s.getSnacks().stream().map(SnackDTO::fromSnack).toList();

        return new SquareDTO(snackDTOs, s.getPosition(), s.getDEFAULT_SIDE_LENGTH());
    }

}
