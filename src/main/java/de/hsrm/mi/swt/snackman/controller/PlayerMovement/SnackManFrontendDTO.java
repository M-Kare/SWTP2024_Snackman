package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

public record SnackManFrontendDTO(boolean forward, boolean backward, boolean left, boolean right, double qX, double qY, double qZ, double qW, double delta, boolean jump, boolean doubleJump, boolean sprinting) {
    
}
