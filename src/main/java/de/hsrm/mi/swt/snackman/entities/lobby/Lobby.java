package de.hsrm.mi.swt.snackman.entities.lobby;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;

import java.util.*;

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
    private Map<String, Mob> clientMobMap;


    public Lobby(String lobbyId, String name, PlayerClient adminClient, boolean isGameStarted, GameMap gameMap) {
        this.lobbyId = lobbyId;
        this.gameMap = gameMap;
        this.name = name;
        this.adminClient = adminClient;
        this.isGameStarted = isGameStarted;
        this.members = new ArrayList<>();
        this.members.add(adminClient);
        this.clientMobMap = new HashMap<>();
    }

    public Map<String, Mob> getClientMobMap() { return clientMobMap; };

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

    public String getLobbyId() {
        return lobbyId;
    }
}
