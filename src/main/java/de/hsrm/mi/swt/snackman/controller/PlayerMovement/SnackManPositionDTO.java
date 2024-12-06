package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import de.hsrm.mi.swt.snackman.entities.mob.eatingMobs.SnackMan;

public record SnackManPositionDTO(double posX, double posY, double posZ) {

    public static SnackManPositionDTO toSnackManDTO(SnackMan snackman){
        return new SnackManPositionDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ());
    }
}
