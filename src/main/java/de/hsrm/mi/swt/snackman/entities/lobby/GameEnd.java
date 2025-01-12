package de.hsrm.mi.swt.snackman.entities.lobby;

/**
 * Represents the end state of a game session, including role, time played,
 * and the calories the snackman collected during the game.
 */
public class GameEnd {
    private ROLE role;
    private long timePlayed;
    private int kcalCollected;

    public GameEnd(ROLE role, long timePlayed, int kcalCollected) {
        this.role = role;
        this.timePlayed = timePlayed;
        this.kcalCollected = kcalCollected;
    }

    public ROLE getRole() {
        return role;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public int getKcalCollected() {
        return kcalCollected;
    }
}
