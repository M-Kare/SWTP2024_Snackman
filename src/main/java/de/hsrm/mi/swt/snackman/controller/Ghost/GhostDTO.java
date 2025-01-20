package de.hsrm.mi.swt.snackman.controller.Ghost;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;

public record GhostDTO(long id, double posX, double posY, double posZ, double radius, int speed) {

    public static GhostDTO fromGhost(Ghost ghost) {
        return new GhostDTO(ghost.getId(), ghost.getPosX(), ghost.getPosY(), ghost.getPosZ(), ghost.getRadius(), GameConfig.GHOST_SPEED);
    }

    @Override
    public String toString() {
        return "GhostDTO{" +
                "id=" + id +
                ", posX=" + posX +
                ", posY=" + posY +
                ", posZ=" + posZ +
                ", radius=" + radius +
                ", speed=" + speed +
                '}';
    }
}
