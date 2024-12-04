package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.controller.Snack.SnackDTO;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;

public record SquareDTO(long id, int indexX, int indexZ, MapObjectType type, SnackDTO snack) {

    // @todo square dto muss auch liste an mob dtos mitgeben -> kein chickenDTO mehr!
    /*
    public record ChickenDTO(Thickness thickness, int indexX, int indexZ) {

    public static ChickenDTO fromChicken(Chicken chicken) {
        return new ChickenDTO(chicken.getThickness(), chicken.getCurrentPosition());
    }
    public static Chicken toChicken(ChickenDTO dto) {
        Chicken chicken = new Chicken();
        chicken.setThickness(dto.thickness);
        chicken.setCurrentPosition(dto.square);

        return chicken;
    }
}
     */

    public static SquareDTO fromSquare(Square s){
        SnackDTO snackDTO = (s.getSnack() != null) ? SnackDTO.fromSnack(s.getSnack()) : null;

        return new SquareDTO(s.getId(), s.getIndexX(), s.getIndexZ(), s.getType(), snackDTO);
    }
}
