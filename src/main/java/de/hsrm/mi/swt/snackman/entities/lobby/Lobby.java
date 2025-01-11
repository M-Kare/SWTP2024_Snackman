package de.hsrm.mi.swt.snackman.entities.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;

/**
 * Represents a lobby where players can gather to play a game together.
 */
public class Lobby {
    private String lobbyId;
    private String name;
    private PlayerClient adminClient;
    private boolean isGameStarted;
    private List<PlayerClient> members;
    private GameMap gameMap;
    private SortedMap<String, Mob> clientMobMap;
    private long timeSinceLastSnackSpawn;
    private boolean usedCustomMap;


    public Lobby(String lobbyId, String name, PlayerClient adminClient, GameMap gameMap) {
        this.lobbyId = lobbyId;
        this.gameMap = gameMap;
        this.name = name;
        this.adminClient = adminClient;
        this.isGameStarted = false;
        this.members = new ArrayList<>();
        this.members.add(adminClient);
        this.clientMobMap = new TreeMap<>();
        this.usedCustomMap = false;
    }

    public SortedMap<String, Mob> getClientMobMap() { return clientMobMap; }

    public String getName() {
        return name;
    }

    public String getAdminClientId() {
        return adminClient.getPlayerId();
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean isGameStarted) {
        this.isGameStarted = isGameStarted;
        if (isGameStarted) {
            setTimeSinceLastSnackSpawn(System.currentTimeMillis());
        }
    }

    public List<PlayerClient> getMembers() {
        return members;
    }

    public PlayerClient getAdminClient() {
        return adminClient;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap newGameMap){
        this.gameMap = newGameMap;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public long getTimeSinceLastSnackSpawn() {
        return timeSinceLastSnackSpawn;
    }

    public void setTimeSinceLastSnackSpawn(long time) {
        timeSinceLastSnackSpawn = time;
    }

    public boolean getUsedCustomMap(){
        return usedCustomMap;
    }

    public void setUsedCustomMap(boolean value){
        this.usedCustomMap = value;
    }
}
