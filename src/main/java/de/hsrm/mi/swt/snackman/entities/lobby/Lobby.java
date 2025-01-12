package de.hsrm.mi.swt.snackman.entities.lobby;

import java.util.*;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private long timePlayed = 0;
    private boolean isGameFinished = false;
    private ROLE winningRole;
    private long gameStartTime;
    private long endTime;
    private final Logger log = LoggerFactory.getLogger(Lobby.class);

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

        gameTimer.schedule(task, GameConfig.PLAYING_TIME);
    }

    /**
     * Starts the game by starting the playing timer
     * and saving when the game started.
     */
    public void startGame() {
        setGameStarted();
        startNewGameTimer();
        this.gameStartTime = System.currentTimeMillis();
    }

    /**
     * Ends the game by ending the playing time timer
     * and determining who won the game.
     * @param winningRole the role winning the game
     */
    public void endGame(ROLE winningRole) {
        log.info("The role {} has won the game.", winningRole);
        this.isGameFinished = true;
        this.endTime = System.currentTimeMillis();
        this.timePlayed = (endTime - this.gameStartTime) / 1000;
        if(this.timePlayed > (GameConfig.PLAYING_TIME / 1000))
            this.timePlayed = GameConfig.PLAYING_TIME / 1000;
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

    public void setGameStarted() {
        this.isGameStarted = true;
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

    @Override
    public String toString() {
        return "Lobby{" +
                "lobbyId='" + lobbyId + '\'' +
                ", name='" + name + '\'' +
                ", adminClient=" + adminClient +
                ", isGameStarted=" + isGameStarted +
                ", members=" + members +
                ", gameMap=" + gameMap +
                ", clientMobMap=" + clientMobMap +
                ", timeSinceLastSnackSpawn=" + timeSinceLastSnackSpawn +
                '}';
    }
}
