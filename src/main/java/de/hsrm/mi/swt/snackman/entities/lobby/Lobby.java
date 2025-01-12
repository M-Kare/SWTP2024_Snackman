package de.hsrm.mi.swt.snackman.entities.lobby;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhost;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.messaging.MessageLoop.MessageLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;

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
    private MessageLoop messageLoop;

    public Lobby(String lobbyId, String name, PlayerClient adminClient, GameMap gameMap, MessageLoop messageLoop) {
        this.lobbyId = lobbyId;
        this.gameMap = gameMap;
        this.name = name;
        this.adminClient = adminClient;
        this.isGameStarted = false;
        this.members = new ArrayList<>();
        this.members.add(adminClient);
        this.clientMobMap = new TreeMap<>();
        this.messageLoop = messageLoop;
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
     *
     * @param winningRole the role winning the game
     */
    public void endGame(ROLE winningRole) {
        log.info("The role {} has won the game.", winningRole);
        this.endTime = System.currentTimeMillis();
        this.timePlayed = (endTime - this.gameStartTime) / 1000;
        if (this.timePlayed > (GameConfig.PLAYING_TIME / 1000))
            this.timePlayed = GameConfig.PLAYING_TIME / 1000;
        this.winningRole = winningRole;
        SnackMan snackMan = getSnackman();

        if(snackMan != null) {
            GameEnd gameEnd = new GameEnd(winningRole, this.timePlayed, snackMan.getKcal());
            setGameFinished(true, gameEnd);
        }
    }

    public SnackMan getSnackman() {
        return Arrays.stream(this.gameMap.getGameMapSquares())
                .flatMap(Arrays::stream)
                .flatMap(square -> square.getMobs().stream())
                .filter(mob -> mob instanceof SnackMan)
                .map(mob -> (SnackMan) mob)
                .findFirst()
                .orElse(null);
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
        setTimeSinceLastSnackSpawn(System.currentTimeMillis());
    }

    public void setGameFinished(boolean gameFinished, GameEnd gameEnd) {
        this.isGameFinished = gameFinished;
        messageLoop.addGameEndToQueue(gameEnd, lobbyId);
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
