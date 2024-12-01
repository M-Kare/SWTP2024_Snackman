package de.hsrm.mi.swt.snackman.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.snackman.entities.lobby.Client;
import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;

/**
 * Service for managing lobbies and clients in the application.
 */
@Service
public class LobbyManagerService {
      private final List<Lobby> lobbies = new ArrayList<>();
      private final List<Client> clients = new ArrayList<>();

      /**
       * Create a new client
       * 
       * @param name the name of the client
       * @return the client
       */
      public Client createNewClient(String name) {
            String uuid = UUID.randomUUID().toString();
            Client newClient = new Client(uuid, name);
            this.clients.add(newClient);

            return newClient;
      }

      /**
       * Creates a new lobby and adds it to the list.
       * 
       * @param name    Name of the lobby
       * @param adminID ID of the lobby creator
       * @return The lobby created
       * @throws LobbyAlreadyExistsException
       */
      public Lobby createLobby(String name, Client admin) throws LobbyAlreadyExistsException {
            if (lobbies.stream().anyMatch(lobby -> lobby.getName().equals(name))) {
                  throw new LobbyAlreadyExistsException("Lobby name already exists");
            }

            Lobby lobby = new Lobby(name, admin, false);

            admin.setRole(ROLE.SNACKMAN);

            lobbies.add(lobby);
            return lobby;
      }

      /**
       * Returns the list of all lobbies.
       * 
       * @return list of lobbies
       */
      public List<Lobby> getAllLobbies() {
            return this.lobbies;
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
            Lobby lobby = findLobbyByUUID(lobbyId);

            if (lobby.isGameStarted()) {
                  throw new GameAlreadyStartedException("Game already started");
            }

            Client newJoiningClient = findClientByUUID(playerId);
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
            Lobby lobby = findLobbyByUUID(lobbyId);
            lobby.getMembers().removeIf(client -> client.getPlayerId().equals(playerId));

            if (lobby.getAdminClientId().equals(playerId) || lobby.getMembers().isEmpty()) {
                  lobbies.remove(lobby);
            }
      }

      /**
       * Starts the game in the specified lobby.
       * 
       * @param lobbyId ID of the lobby
       */
      public void startGame(String lobbyId) {
            Lobby lobby = findLobbyByUUID(lobbyId);

            if (lobby.getMembers().size() < 2) {
                  throw new IllegalStateException("Not enough players to start the game");
            }

            lobby.setGameStarted(true);
      }

      /**
       * Searches the lobby for its UUID
       * 
       * @param lobbyID UUID of the lobby
       * @return the lobby
       */
      public Lobby findLobbyByUUID(String lobbyID) {
            return lobbies.stream()
                        .filter(l -> l.getUuid().equals(lobbyID))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Lobby not found"));
      }

      /**
       * Searches the client for their UUID
       * 
       * @param clientID UUID of the client
       * @return the client
       */
      private Client findClientByUUID(String clientID) {
            return clients.stream()
                        .filter(l -> l.getPlayerId().equals(clientID))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Client not found"));
      }

      /**
       * Retrieves the active client or creates a new client
       * 
       * @param name     the name of the client
       * @param clientID the uuid of the client
       * @return the client
       */
      public Client getClient(String name, String clientID) {
            return clients.stream()
                        .filter(l -> l.getPlayerId().equals(clientID) && l.getPlayerName().equals(name))
                        .findFirst()
                        .orElse(createNewClient(name));
      }
}
