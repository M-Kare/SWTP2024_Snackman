package de.hsrm.mi.swt.snackman.entities.lobby;

//TODO javadoc
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
