package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.controller.Snack.SnackDTO;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record SquareDTO(long id, int indexX, int indexZ, MapObjectType type, List<SnackDTO> snacks) {

    public static SquareDTO fromSquare(Square s){
        List<SnackDTO> snacks = s.getSnacks().stream()
                .map(SnackDTO::fromSnack)
                .collect(Collectors.toList());

        return new SquareDTO(s.getId(), s.getIndexX(), s.getIndexZ(), s.getType(), snacks);
    }


}
