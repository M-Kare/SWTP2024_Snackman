package de.hsrm.mi.swt.snackman.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.hsrm.mi.swt.snackman.entities.lobby.Client;
import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;

@Service
public class LobbyManagerService {
      private final List<Lobby> lobbies;
      private final List<Client> clients;

      public LobbyManagerService() {
            this.lobbies = new ArrayList<>();
            clients = new ArrayList<>();
      }

      /**
       * Erstelle einen neuen Client
       * 
       * @param name der Name des Clients
       * @return den Client
       */
      public Client createNewClient(String name) {
            String uuid = UUID.randomUUID().toString();
            Client newClient = new Client(uuid, name);
            this.clients.add(newClient);

            return newClient;
      }

      /**
       * Erstellt eine neue Lobby und fügt sie zur Liste hinzu.
       * 
       * @param name    Name der Lobby
       * @param adminID ID des Lobby-Erstellers
       * @return Die erstellte Lobby
       * @throws LobbyAlreadyExistsException
       */
      public Lobby createLobby(String name, Client admin) throws LobbyAlreadyExistsException {
            if (lobbies.stream().anyMatch(lobby -> lobby.getName().equals(name))) {
                  throw new LobbyAlreadyExistsException("Lobby name already exists");
            }

            // Lobby erstellen
            Lobby lobby = new Lobby(name, admin.getPlayerId(), false);
            lobby.getMembers().add(admin);

            admin.setRole(ROLE.SNACKMAN);

            lobbies.add(lobby);
            return lobby;
      }

      /**
       * Gibt die Liste aller Lobbys zurück.
       * 
       * @return Liste der Lobbys
       */
      public List<Lobby> getAllLobbies() {
            return this.lobbies;
      }

      /**
       * Fügt einen Spieler einer Lobby hinzu.
       * 
       * @param lobbyId  ID der Lobby
       * @param playerId ID des Spielers
       * @return Die aktualisierte Lobby
       * @throws GameAlreadyStartedException
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
       * Entfernt einen Spieler aus einer Lobby.
       * 
       * @param lobbyId  ID der Lobby
       * @param playerId ID des Spielers
       */
      public void leaveLobby(String lobbyId, String playerId) {
            Lobby lobby = findLobbyByUUID(lobbyId);

            // Entferne den Spieler
            lobby.getMembers().removeIf(client -> client.getPlayerId().equals(playerId));

            // Lobby entfernen, wenn der Ersteller sie verlässt oder sie leer ist
            if (lobby.getAdminClientId().equals(playerId) || lobby.getMembers().isEmpty()) {
                  lobbies.remove(lobby);
            }
      }

      /**
       * Startet das Spiel in der angegebenen Lobby.
       * 
       * @param lobbyId ID der Lobby
       */
      public void startGame(String lobbyId) {
            Lobby lobby = findLobbyByUUID(lobbyId);

            if (lobby.getMembers().size() < 2) {
                  throw new IllegalStateException("Not enough players to start the game");
            }

            lobby.setGameStarted(true);
      }

      /**
       * Sucht die Lobby nach deren UUID
       * 
       * @param lobbyID UUID der Lobby
       * @return the lobby
       */
      public Lobby findLobbyByUUID(String lobbyID) {
            return lobbies.stream()
                        .filter(l -> l.getUuid().equals(lobbyID))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Lobby not found"));
      }

      /**
       * Sucht den Client nach deren UUID
       * 
       * @param clientID UUID des Clients
       * @return the Client
       */
      private Client findClientByUUID(String clientID) {
            return clients.stream()
                        .filter(l -> l.getPlayerId().equals(clientID))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Client not found"));
      }

      /**
       * Retrieves the active client or creates a new client
       * @param name the name of the client
       * @param clientID the uuid of the client
       * @return the client
       */
      public Client getClient (String name, String clientID) {
            return clients.stream()
                  .filter(l -> l.getPlayerId().equals(clientID) && l.getPlayerName().equals(name))
                  .findFirst()
                  .orElse(createNewClient(name));
      }
}
