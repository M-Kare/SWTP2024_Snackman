package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.controller.Snack.SnackDTO;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;
import de.hsrm.mi.swt.snackman.entities.Square.Square;

import java.util.List;

import static de.hsrm.mi.swt.snackman.controller.Snack.SnackDTO.fromSnack;

public record SquareDTO(List<SnackDTO> snacks, Position position, double height) {

    public static SquareDTO fromSquare(Square s){
        var snackDTOs = s.getSnacks().stream().map(SnackDTO::fromSnack).toList();

        return new SquareDTO(snackDTOs, s.getPosition(), s.getHEIGHT());
    }

}
