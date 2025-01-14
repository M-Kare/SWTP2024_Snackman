package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

public record GhostFrontendDTO(long id, boolean forward, boolean backward, boolean left, boolean right, double qX, double qY, double qZ, double qW, double delta) {

    @Override
    public String toString() {
        return "GhostFrontendDTO{" +
                "id= " + id +
                "forward=" + forward +
                ", backward=" + backward +
                ", left=" + left +
                ", right=" + right +
                ", qX=" + qX +
                ", qY=" + qY +
                ", qZ=" + qZ +
                ", qW=" + qW +
                ", delta=" + delta +
                '}';
    }
}
