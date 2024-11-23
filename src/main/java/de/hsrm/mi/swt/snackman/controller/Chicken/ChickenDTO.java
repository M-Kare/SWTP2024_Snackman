package de.hsrm.mi.swt.snackman.controller.Chicken;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mob.Thickness;
import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Chicken;

public record ChickenDTO(Thickness thickness, Square square) {

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