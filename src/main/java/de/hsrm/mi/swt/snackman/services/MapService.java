package de.hsrm.mi.swt.snackman.services;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhost;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhostDifficulty;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Spawnpoint;
import de.hsrm.mi.swt.snackman.entities.map.SpawnpointMobType;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.messaging.MessageLoop.MessageLoop;

/**
 * Service class for managing the game map
 * This class is responsible for loading and providing access to the game map data
 */
@Service
public class MapService {

    private final ReadMazeService readMazeService;
    private final MessageLoop messageLoop;
    Logger log = LoggerFactory.getLogger(MapService.class);

    /**
     * Constructs a new MapService
     * Initializes the maze data by reading from a file and creates a Map object
     */
    @Autowired
    public MapService(ReadMazeService readMazeService, @Lazy MessageLoop messageLoop) {
        this.readMazeService = readMazeService;
        this.messageLoop = messageLoop;
    }

    public GameMap createNewGameMap(String lobbyId, String filePath) {
        readMazeService.generateNewMaze();
        char[][] mazeData = readMazeService.readMazeFromFile(filePath);
        return convertMazeDataGameMap(lobbyId, mazeData);
    }

    public GameMap createNewGameMap(String lobbyId) {
        return createNewGameMap(lobbyId, "./extensions/map/Maze.txt");
    }

    /**
     * Converts the char array maze data into MapObjects and populates the game map
     *
     * @param mazeData the char array representing the maze
     */
    public GameMap convertMazeDataGameMap(String lobbyId, char[][] mazeData) {
        Square[][] squaresBuildingMap = new Square[mazeData.length][mazeData[0].length];

        for (int x = 0; x < mazeData.length; x++) {
            for (int z = 0; z < mazeData[0].length; z++) {
                try {
                    Square squareToAdd = createSquare(lobbyId, mazeData[x][z], x, z);

                    squaresBuildingMap[x][z] = squareToAdd;
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage());
                }
            }
        }

        return new GameMap(squaresBuildingMap);
    }

    /**
     * Creates a Square by given indexes
     *
     * @param symbol from char array
     * @param x      index
     * @param z      index
     * @return a created Square
     */
    private Square createSquare(String lobbyId, char symbol, int x, int z) {
        Square square = null;

        switch (symbol) {
            case '#':
                square = new Square(MapObjectType.WALL, x, z);
                break;
            case ' ':
                square = new Square(MapObjectType.FLOOR, x, z);
                double emptyOrNot = Math.random();
                if (emptyOrNot <= GameConfig.SNACK_SPAWN_RATE) {
                    addRandomSnackToSquare(square);
                } else {
                    square.setSnack(new Snack(SnackType.EMPTY));
                }
                break;
            case 'C':

                log.debug("Initialising chicken");
                square = new Square(x, z, new Spawnpoint(SpawnpointMobType.CHICKEN));

                break;
            case 'G':
                log.debug("Initialising ghost");

                square = new Square(x, z, new Spawnpoint(SpawnpointMobType.GHOST));
                break;

                /*

                TODO add ghosts
                            case 'G':
                log.debug("Initialising ghost");
                if (initialisedPlayers < AMOUNT_PLAYERS_FOR_GAME) {
                    log.info("Initialising playerGhost");
                    // init real players here aka Ghost
                    square = new Square(MapObjectType.FLOOR, x, z);
                    Ghost ghost = new Ghost(square, this);
                    initialisedPlayers++;
                    ghost.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                        if (evt.getPropertyName().equals("ghost")) {
                            FrontendGhostMessageEvent messageEvent = new FrontendGhostMessageEvent(EventType.GHOST, ChangeType.UPDATE, (Ghost) evt.getNewValue());
                            frontendMessageService.sendGhostEvent(messageEvent);
                        }
                    });
                    FrontendGhostMessageEvent message = new FrontendGhostMessageEvent(EventType.GHOST, ChangeType.CREATE, ghost);
                    frontendMessageService.sendGhostEvent(message);
                    log.info("New player ghost is: {}", ghost);

                } else {
                    log.info("Initialising scriptGhost");
                    square = new Square(MapObjectType.FLOOR, x, z);
                    ScriptGhost newScriptGhost = new ScriptGhost(this, square, ScriptGhostDifficulty.EASY);
                    Thread ghostThread = new Thread(newScriptGhost);
                    ghostThread.start();

                    newScriptGhost.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                        if (evt.getPropertyName().equals("scriptGhost")) {
                            FrontendScriptGhostMessageEvent messageEvent = new FrontendScriptGhostMessageEvent(EventType.SCRIPT_GHOST,
                                    ChangeType.UPDATE, (ScriptGhost) evt.getNewValue());

                            frontendMessageService.sendScriptGhostEvent(messageEvent);
                        }
                    });
                    log.info("New scriptGhost is: {}", newScriptGhost);
                }
                 */
            case 'S':
                log.debug("Initialising snackman");

                square = new Square(x, z, new Spawnpoint(SpawnpointMobType.SNACKMAN));
                break;
            default:
                square = new Square(MapObjectType.FLOOR, x, z);
        }

        square.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (evt.getPropertyName().equals("square")) {
                log.debug("Square changed {}", evt);
                messageLoop.addSquareToQueue((Square) evt.getNewValue(), lobbyId);
            }
        });

        return square;
    }

    /**
     * Adds a random generated snack inside a square of type FLOOR
     *
     * @param square to put snack in
     */
    public void addRandomSnackToSquare(Square square) {
        if (square.getType() == MapObjectType.FLOOR) {
            SnackType randomSnackType = SnackType.getRandomSnack();
            square.setSnack(new Snack(randomSnackType));
        }
    }

    /**
     * Goes trough the map and checks if it's a spawnpoint and sets a Mob
     *
     * @param gameMap where the mobs should spawn
     * @param lobby   of the mobs
     */
    public void spawnMobs(GameMap gameMap, Lobby lobby) {
        List<Square> ghostSpawnSquares = new ArrayList<>();
        Square snackmanSpawnSquare = null;

        for (int i = 0; i < gameMap.getGameMapSquares().length; i++) {
            for (int j = 0; j < gameMap.getGameMapSquares()[i].length; j++) {
                Square currentSquare = gameMap.getGameMapSquares()[i][j];
                Spawnpoint spawnpoint = currentSquare.getSpawnpoint();
                if (spawnpoint != null) {
                    SpawnpointMobType spawnpointMobType = spawnpoint.spawnpointMobType();
                    switch (spawnpointMobType) {
                        case SpawnpointMobType.CHICKEN:

                            Chicken newChicken = new Chicken(currentSquare, gameMap);

                            Thread chickenThread = new Thread(newChicken);
                            chickenThread.start();

                            newChicken.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                                if (evt.getPropertyName().equals("chicken")) {
                                    messageLoop.addChickenToQueue((Chicken) evt.getNewValue(), lobby.getLobbyId());
                                }
                            });
                            break;
                        case SpawnpointMobType.GHOST:
                            ghostSpawnSquares.add(currentSquare);
                            break;
                        case SpawnpointMobType.SNACKMAN:
                            snackmanSpawnSquare = currentSquare;
                            break;
                    }
                }
            }
        }

        placeMobsOnMap(lobby, ghostSpawnSquares, snackmanSpawnSquare);
    }

    /**
     * @param lobby               where the Mobs should spawn
     * @param ghostSpawnSquares   list of spawnpoints of ghosts
     * @param snackmanSpawnSquare spawnpoint of the snackman
     */
    private void placeMobsOnMap(Lobby lobby, List<Square> ghostSpawnSquares, Square snackmanSpawnSquare) {
        int ghostSpawnIndex = 0;
        Square temp;

        for (PlayerClient client : lobby.getMembers()) {
            switch (client.getRole()) {
                // TODO change to spawn ghost instead of snackman
                case GHOST:
                    log.info("Initialising playerGhost with spawnpoint {}", ghostSpawnSquares.get(ghostSpawnIndex));
                    double x = ghostSpawnSquares.get(ghostSpawnIndex).getIndexX() * GameConfig.SQUARE_SIZE + 0.5 * GameConfig.SQUARE_SIZE;
                    double z = ghostSpawnSquares.get(ghostSpawnIndex).getIndexZ() * GameConfig.SQUARE_SIZE + 0.5 * GameConfig.SQUARE_SIZE;
                    Ghost ghost = new Ghost(ghostSpawnSquares.get(ghostSpawnIndex), x, z, lobby.getGameMap());
                    log.info("New player ghost is: {}", ghost);

                    if (ghostSpawnIndex >= ghostSpawnSquares.size()) {
                        ghostSpawnIndex = 0;
                    }

                    lobby.getClientMobMap().put(client.getPlayerId(), ghost);
                    ghostSpawnIndex++;
                    break;
                case SNACKMAN:
                    log.info("Initialising snackman with spawnpoint {}", snackmanSpawnSquare);
                    SnackMan snackMan = new SnackMan(lobby.getGameMap(), snackmanSpawnSquare, calcCenterPositionFromMapIndex(snackmanSpawnSquare.getIndexX()), GameConfig.SNACKMAN_GROUND_LEVEL, calcCenterPositionFromMapIndex(snackmanSpawnSquare.getIndexZ()));

                    log.info("New player snackman is {}", snackMan);
                    lobby.getClientMobMap().put(client.getPlayerId(), snackMan);
                    break;
            }
        }

        /*int AMOUNT_SCRIPT_GHOSTS = GameConfig.AMOUNT_PLAYERS - lobby.getMembers().size();
        for (int i = 0; i < AMOUNT_SCRIPT_GHOSTS; i++) {
            log.info("Initialising scriptGhost");
            Square square = ghostSpawnSquares.get(ghostSpawnIndex);

            ScriptGhost newScriptGhost = new ScriptGhost(lobby.getGameMap(), square, ScriptGhostDifficulty.EASY);

            Thread ghostThread = new Thread(newScriptGhost);
            //ghostThread.start();
            ghostSpawnIndex++;

            newScriptGhost.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                if (evt.getPropertyName().equals("scriptGhost")) {
                    messageLoop.addScriptGhostToQueue((ScriptGhost) evt.getNewValue(), lobby.getLobbyId());
                }
            });
            log.info("New scriptGhost is: {}", newScriptGhost);
        }
         */

    }

    public double calcCenterPositionFromMapIndex(int index) {
        return (index * GameConfig.SQUARE_SIZE) + (GameConfig.SQUARE_SIZE / 2);
    }

    public void respawnSnacks(GameMap map) {
        for (int i = 0; i < map.getGameMapSquares().length; i++) {
            for (int j = 0; j < map.getGameMapSquares()[0].length; j++) {
                Square square = map.getSquareAtIndexXZ(i, j);
                if (square.getType() == MapObjectType.FLOOR) {
                    double rand = Math.random();
                    if (rand <= GameConfig.SNACK_SPAWN_RATE) {
                        addRandomSnackToSquare(square);
                    } else {
                        square.setSnack(new Snack(SnackType.EMPTY));
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public SnackMan getSnackMan() {
        return null; //snackman;
    }

    /*public Ghost getGhost(long id) {
        Square[][] squares = this.gameMap.getGameMap();
        for (Square square[] : squares) {
            for (Square square1 : square) {
                for (Mob mob :  square1.getMobs()){
                    if (mob.getId() == id  && mob instanceof  Ghost){
                        return (Ghost) mob;
                    }
                }
              //  return (Ghost) square1.getMobs().stream().filter(mob -> mob.getId() == id && mob instanceof Ghost); // kein filter sondern ein find hier sodass man den ersten geist zurück bekommt
            }
        }
        return null;
    }*/

}
