package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.entities.mob.Thickness;
import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken.Chicken;

public record ChickenDTO(Thickness thickness) {
    public static ChickenDTO fromChicken(Chicken s){
        return new ChickenDTO(s.getThickness());
    }
}
