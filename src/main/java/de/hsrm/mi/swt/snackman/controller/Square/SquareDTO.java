package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.controller.Snack.SnackDTO;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;

import java.util.ArrayList;
import java.util.List;

public record SquareDTO(long id, int indexX, int indexZ, MapObjectType type, List<SnackDTO> snacks) {

    public static SquareDTO fromSquare(Square s){
        List<SnackDTO> snacks = new ArrayList<>();

        for(Snack snack : s.getSnacks()){
            SnackDTO snackDTO = SnackDTO.fromSnack(snack);
            snacks.add(snackDTO);
        }

        return new SquareDTO(s.getId(), s.getIndexX(), s.getIndexZ(), s.getType(), snacks);
    }


}
