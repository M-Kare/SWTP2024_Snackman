package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;

public record GhostPositionDTO(double posX, double posY, double posZ) {

    public static GhostPositionDTO toGhostDTO(Ghost ghost){
        return new GhostPositionDTO(ghost.getPosX(), ghost.getPosY(), ghost.getPosZ());
    }
}
