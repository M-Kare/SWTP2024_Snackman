package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

// Identifikation welche Figur/Charakter über die UUID
public record PlayerToBackendDTO(boolean forward, boolean backward, boolean left, boolean right, double qX, double qY
        , double qZ, double qW, double delta, boolean jump, boolean doubleJump, String uuid) {
    
}
