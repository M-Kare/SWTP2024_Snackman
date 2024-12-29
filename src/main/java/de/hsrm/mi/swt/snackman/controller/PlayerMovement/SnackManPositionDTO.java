package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;

public record SnackManPositionDTO(double posX, double posY, double posZ, boolean isSprinting, int sprintTimeLeft, boolean isInCooldown) {

    public static SnackManPositionDTO toSnackManDTO(SnackMan snackman){
        return new SnackManPositionDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ(), snackman.isSprinting(), snackman.getSprintTimeLeft(), snackman.isInCooldown());
    }
}
