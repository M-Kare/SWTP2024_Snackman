package de.hsrm.mi.swt.snackman.services;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.controller.Square.ChickenDTO;
import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Spawnpoint;
import de.hsrm.mi.swt.snackman.entities.map.SpawnpointMobType;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.messaging.ChangeType;
import de.hsrm.mi.swt.snackman.messaging.EventType;
import de.hsrm.mi.swt.snackman.messaging.FrontendChickenMessageEvent;
import de.hsrm.mi.swt.snackman.messaging.FrontendLobbyMessageService;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageEvent;

/**
 * Service class for managing the game map
 * This class is responsible for loading and providing access to the game map
 * data
 */
@Service
public class MapService {

    private final ReadMazeService readMazeService;
    private final FrontendLobbyMessageService frontendLobbyMessageService;

    Logger log = LoggerFactory.getLogger(MapService.class);
    // TODO Should be a HashMap of Ghosts

    /**
     * Constructs a new MapService
     * Initializes the maze data by reading from a file and creates a Map object
     */
    @Autowired
    public MapService(ReadMazeService readMazeService, FrontendLobbyMessageService frontendLobbyMessageService) {
        this.readMazeService = readMazeService;
        this.frontendLobbyMessageService = frontendLobbyMessageService;

        // TODO create Snackmans in lobby
        // snackman = new SnackMan(this, GameConfig.SNACKMAN_SPEED,
        // GameConfig.SNACKMAN_RADIUS);
        // snackmans = new HashMap<>();
    }

    public GameMap createNewGameMap(String lobbyId, String filePath) {
        readMazeService.generateNewMaze();
        char[][] mazeData = readMazeService.readMazeFromFile(filePath);
        return convertMazeDataGameMap(lobbyId, mazeData);
    }

    public GameMap createNewGameMap(String lobbyId) {
        return createNewGameMap(lobbyId, "Maze.txt");
    }

    /**
     * Converts the char array maze data into MapObjects and populates the game map
     *
     * @param mazeData the char array representing the maze
     */
    private GameMap convertMazeDataGameMap(String lobbyId,
            char[][] mazeData) {
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

    // TODO Maze.py map größe als Argumente herein reichen statt in der python-file
    // selbst zu hinterlegen

    /**
     * Creates a Square by given indexes
     *
     * @param symbol from char array
     * @param x      index
     * @param z      index
     * @return a created Square
     */
    private Square createSquare(String lobbyId, char symbol, int x, int z) {
        Square square;

        switch (symbol) {
            case '#': {
                square = new Square(MapObjectType.WALL, x, z);
                break;
            }
            case ' ': {
                square = new Square(MapObjectType.FLOOR, x, z);
                addRandomSnackToSquare(square);

                square.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                    if (evt.getPropertyName().equals("square")) {
                        FrontendMessageEvent messageEvent = new FrontendMessageEvent(EventType.SNACK,
                                ChangeType.UPDATE, lobbyId,
                                (Square) evt.getNewValue());

                        frontendLobbyMessageService.sendEvent(messageEvent);
                    }
                });
                break;
            }
            case 'C':

                log.debug("Initialising chicken");
                square = new Square(x, z, new Spawnpoint(SpawnpointMobType.CHICKEN));

                break;
            case 'G':
                log.debug("Initialising ghost");

                square = new Square(x, z, new Spawnpoint(SpawnpointMobType.GHOST));
                break;
            case 'S':
                log.debug("Initialising snackman");

                square = new Square(x, z, new Spawnpoint(SpawnpointMobType.SNACKMAN));
                break;
            default: {
                square = new Square(MapObjectType.FLOOR, x, z);
            }
        }

        return square;
    }

    /**
     * Adds a random generated snack inside a square of type FLOOR
     *
     * @param square to put snack in
     */
    private void addRandomSnackToSquare(Square square) {
        if (square.getType() == MapObjectType.FLOOR) {
            SnackType randomSnackType = SnackType.getRandomSnack();

            square.setSnack(new Snack(randomSnackType));
        }
    }

    /**
     * Goes trough the map and checks if it's a spawnpoint and sets a Mob
     *
     * @param gameMap
     * @param lobbyId
     */
    public void spawnMobs(GameMap gameMap, Lobby lobby) {
        List<Square> ghostSpawnSquares = new ArrayList<>();
        List<Square> snackmanSpawnSquares = new ArrayList<>();

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
                                    FrontendChickenMessageEvent messageEvent = new FrontendChickenMessageEvent(
                                            EventType.CHICKEN,
                                            ChangeType.UPDATE, lobby.getLobbyId(),
                                            ChickenDTO.fromChicken((Chicken) evt.getNewValue()));

                                    frontendLobbyMessageService.sendChickenEvent(messageEvent);
                                }
                            });
                            break;
                        case SpawnpointMobType.GHOST:
                            ghostSpawnSquares.add(currentSquare);
                            break;
                        case SpawnpointMobType.SNACKMAN:
                            snackmanSpawnSquares.add(currentSquare);
                            break;
                    }

                }

            }
        }

        int ghostSpawnIndex = 0;
        int snackmanSpawnIndex = 0;
        Square temp;

        for (PlayerClient client : lobby.getMembers()) {
            switch (client.getRole()) {
                // TODO change to spawn ghost instead of snackman
                case GHOST:
                    if (ghostSpawnIndex >= ghostSpawnSquares.size()) {
                        ghostSpawnIndex = 0;
                    }
                    temp = ghostSpawnSquares.get(ghostSpawnIndex);
                    lobby.getClientMobMap().put(client.getPlayerId(),
                            new SnackMan(lobby.getGameMap(), calcCenterPositionFromMapIndex(temp.getIndexX()),
                                    GameConfig.SNACKMAN_GROUND_LEVEL,
                                    calcCenterPositionFromMapIndex(temp.getIndexZ())));
                    ghostSpawnIndex++;
                    break;
                case SNACKMAN:
                    if (snackmanSpawnIndex >= snackmanSpawnSquares.size()) {
                        snackmanSpawnIndex = 0;
                    }
                    temp = snackmanSpawnSquares.get(snackmanSpawnIndex);
                    lobby.getClientMobMap().put(client.getPlayerId(),
                            new SnackMan(lobby.getGameMap(), calcCenterPositionFromMapIndex(temp.getIndexX()),
                                    GameConfig.SNACKMAN_GROUND_LEVEL,
                                    calcCenterPositionFromMapIndex(temp.getIndexZ())));
                    snackmanSpawnIndex++;
                    break;
            }
        }
    }

    public double calcCenterPositionFromMapIndex(int index) {
        return (index * GameConfig.SQUARE_SIZE) + (GameConfig.SQUARE_SIZE / 2);
    }
}
