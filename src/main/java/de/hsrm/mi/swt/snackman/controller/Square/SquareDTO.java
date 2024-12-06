package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.controller.Snack.SnackDTO;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken.Chicken;

import java.util.List;
import java.util.stream.Collectors;

public record SquareDTO(long id, int indexX, int indexZ, MapObjectType type, SnackDTO snack, List<ChickenDTO> chickens) {

    public static SquareDTO fromSquare(Square s){
        SnackDTO snackDTO = (s.getSnack() != null) ? SnackDTO.fromSnack(s.getSnack()) : null;

        List<ChickenDTO> chickenDTOs = s.getMobs().stream()
                .filter(mob -> mob instanceof Chicken)
                .map(chicken -> ChickenDTO.fromChicken((Chicken) chicken))
                .toList();

        return new SquareDTO(s.getId(), s.getIndexX(), s.getIndexZ(), s.getType(), snackDTO, chickenDTOs);
    }
}
