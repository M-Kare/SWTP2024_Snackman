package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

public record SnackManDTO(double posX, double posY, double posZ, double dirY) {

    public static SnackManDTO toSnackManDTO(SnackMan snackman){
        return new SnackManDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ(), snackman.getDirY());
    }
}
