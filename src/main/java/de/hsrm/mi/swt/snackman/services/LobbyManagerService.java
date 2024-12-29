package de.hsrm.mi.swt.snackman.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;

/**
 * Service for managing lobbies and clients in the application.
 */
@Service
public class LobbyManagerService {

    private final MapService mapService;
    private final Map<String, Lobby> lobbies = new HashMap<>();
    private final Map<String, PlayerClient> clients = new HashMap<>();

    @Autowired
    public LobbyManagerService(MapService mapService) {
        this.mapService = mapService;
    }

    /**
     * Create a new client
     *
     * @param name the name of the client
     * @return the client
     */
    public PlayerClient createNewClient(String name) {
        String uuid = UUID.randomUUID().toString();
        PlayerClient newClient = new PlayerClient(uuid, name);
        this.clients.put(uuid, newClient);

        return newClient;
    }

    /**
     * Creates a new lobby and adds it to the list. Initilizes the GameMap without Mobs.
     *
     * @param name Name of the lobby
     * @return The lobby created
     * @throws LobbyAlreadyExistsException
     */
    public Lobby createLobby(String name, PlayerClient admin) throws LobbyAlreadyExistsException {
        if (lobbies.values().stream().anyMatch(lobby -> lobby.getName().equals(name))) {
            throw new LobbyAlreadyExistsException("Lobby name already exists");
        }

        //TODO change to SessionId
        var uuid = UUID.randomUUID().toString();
        GameMap gameMap = mapService.createNewGameMap(uuid);

        Lobby lobby = new Lobby(uuid, name, admin, false, gameMap);

        admin.setRole(ROLE.SNACKMAN);

        lobbies.put(lobby.getLobbyId(), lobby);
        return lobby;
    }

    /**
     * Returns the list of all lobbies.
     *
     * @return list of lobbies
     */
    public List<Lobby> getAllLobbies() {
        return lobbies.values().stream().toList();
    }

    /**
     * Adds a player to a lobby.
     *
     * @param lobbyId  ID of the lobby
     * @param playerId ID of the player
     * @return The updated lobby
     * @throws GameAlreadyStartedException if the game has already been started
     */
    public Lobby joinLobby(String lobbyId, String playerId) throws GameAlreadyStartedException {
        Lobby lobby = findLobbyByLobbyId(lobbyId);

        if (lobby.isGameStarted()) {
            throw new GameAlreadyStartedException("Game already started");
        }

        PlayerClient newJoiningClient = findClientByClientId(playerId);
        newJoiningClient.setRole(ROLE.GHOST);
        lobby.getMembers().add(newJoiningClient);
        return lobby;
    }

    /**
     * Removes a player from a lobby.
     *
     * @param lobbyId  ID of the lobby
     * @param playerId ID of the player
     */
    public void leaveLobby(String lobbyId, String playerId) {
        Lobby lobby = findLobbyByLobbyId(lobbyId);
        lobby.getMembers().removeIf(client -> client.getPlayerId().equals(playerId));


        if (lobby.getAdminClientId().equals(playerId) || lobby.getMembers().isEmpty()) {
            lobbies.remove(lobby.getLobbyId());
        }
    }

    /**
     * Starts the game in the specified lobby.
     *
     * @param lobbyId ID of the lobby
     */
    public void startGame(String lobbyId) {
        Lobby lobby = findLobbyByLobbyId(lobbyId);

        if (lobby.getMembers().size() < 2) {
            throw new IllegalStateException("Not enough players to start the game");
        }

        mapService.spawnMobs(lobby.getGameMap(), lobby);
        lobby.setGameStarted(true);
    }

    /**
     * Searches the lobby for its UUID
     *
     * @param lobbyID UUID of the lobby
     * @return the lobby
     */
    public Lobby findLobbyByLobbyId(String lobbyID) {
        Lobby lobby = lobbies.get(lobbyID);
        if (lobby == null) {
            throw new NoSuchElementException();
        } else {
            return lobby;
        }
    }

    /**
     * Searches the client for their UUID
     *
     * @param clientID UUID of the client
     * @return the client
     */
    public PlayerClient findClientByClientId(String clientID) {
        PlayerClient client = clients.get(clientID);
        if (client == null) {
            throw new NoSuchElementException();
        } else {
            return client;
        }
    }

    public List<String> getAllClients() {
        List<String> playerIds = new ArrayList<>();
        for (PlayerClient client : clients.values()) {
            playerIds.add(client.getPlayerId());
        }
        return playerIds;
    }

    public GameMap getGameMapByLobbyId(String lobbyId) {
        return lobbies.get(lobbyId).getGameMap();
    }
}
