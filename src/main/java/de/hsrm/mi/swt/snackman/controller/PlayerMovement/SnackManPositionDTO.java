package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;

public record SnackManPositionDTO(double posX, double posY, double posZ, String uuid) {

    public static SnackManPositionDTO toSnackManDTO(SnackMan snackman, String uuid){
        return new SnackManPositionDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ(), uuid);
    }
}
