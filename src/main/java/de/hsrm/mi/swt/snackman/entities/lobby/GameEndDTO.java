package de.hsrm.mi.swt.snackman.entities.lobby;

//TODO javadoc
public record GameEndDTO(ROLE role, long timePlayed, int kcalCollected) {
    public static GameEndDTO fromGameEnd(GameEnd gameEnd){
        return new GameEndDTO(gameEnd.getRole(), gameEnd.getTimePlayed(), gameEnd.getKcalCollected());
    }
}
