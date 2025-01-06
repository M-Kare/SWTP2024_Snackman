package de.hsrm.mi.swt.snackman.configuration;

public class GameConfig {

    //MAP
    public static final int SQUARE_SIZE = 2;
    public static final int SQUARE_HEIGHT = 1;
    
    // SNACKMAN
    public static final int SNACKMAN_GROUND_LEVEL = 2;
    public static final double SNACKMAN_RADIUS = 0.3;
    public static final int SNACKMAN_SPEED = 10;

    //SNACKMAN JUMPING PARAM
    public static final double JUMP_STRENGTH = 8;
    public static final double DOUBLEJUMP_STRENGTH = JUMP_STRENGTH * 0.1;
    public static final double GRAVITY = -20;

    //LOBBY
    public static final int TIME_FOR_SNACKS_TO_RESPAWN = 20000;
    public static final double SNACK_SPAWN_RATE = 0.3;
}
