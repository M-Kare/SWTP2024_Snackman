package de.hsrm.mi.swt.snackman.entities.lobby;

import java.util.*;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
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
    private Timer gameTimer;
    private long playingTime = GameConfig.PLAYING_TIME;
    private long timePlayed = 0;
    private boolean isGameFinished = false;
    private ROLE winningRole;
    private long gameStartTime;
    private long endTime;

    public Lobby(String lobbyId, String name, PlayerClient adminClient, GameMap gameMap) {
        this.lobbyId = lobbyId;
        this.gameMap = gameMap;
        this.name = name;
        this.adminClient = adminClient;
        this.isGameStarted = false;
        this.members = new ArrayList<>();
        this.members.add(adminClient);
        this.clientMobMap = new TreeMap<>();
        initTimer();
    }

    /**
     * initializes the timer responsible for making sure
     * a match only lasts 5 minutes
     */
    private void initTimer() {
        this.gameTimer = new Timer();
    }

    /**
     * Starts a new timer for playing the game.
     * After 5 minutes, the game is automatically stopped.
     */
    private void startNewGameTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        gameTimer = new Timer();

        TimerTask task = new TimerTask() {
            public void run() {
                endGame(ROLE.GHOST);
            }
        };

        gameTimer.schedule(task, playingTime);
    }

    public void startGame() {
        setGameStarted(true);
        startNewGameTimer();
        this.gameStartTime = System.currentTimeMillis();
    }

    private void endGame(ROLE winningRole) {
        this.isGameFinished = true;
        this.endTime = System.currentTimeMillis();
        this.timePlayed = (endTime - this.gameStartTime) / 1000;
        // TODO if > 5 min dann auf 5 minuten setzen
        this.winningRole = winningRole;
    }

    public SortedMap<String, Mob> getClientMobMap() {
        return clientMobMap;
    }

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

    public String getLobbyId() {
        return lobbyId;
    }

    public long getTimeSinceLastSnackSpawn() {
        return timeSinceLastSnackSpawn;
    }

    public void setTimeSinceLastSnackSpawn(long time) {
        timeSinceLastSnackSpawn = time;
    }
}
