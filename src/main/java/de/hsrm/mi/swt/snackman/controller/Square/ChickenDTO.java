package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.entities.mob.Thickness;
import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.Chicken.Direction;

public record ChickenDTO(long id, int posX, int posZ, Thickness thickness, Direction lookingDirection) {
    public static ChickenDTO fromChicken(Chicken s){
        return new ChickenDTO(s.getId(), s.getPosX(), s.getPosZ(), s.getThickness(), s.getLookingDirection());
    }
}
