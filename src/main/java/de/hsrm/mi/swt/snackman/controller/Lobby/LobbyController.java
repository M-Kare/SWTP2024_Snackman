package de.hsrm.mi.swt.snackman.controller.Lobby;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.services.GameAlreadyStartedException;
import de.hsrm.mi.swt.snackman.services.LobbyAlreadyExistsException;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;

/**
 * The LobbyController handles HTTP requests related to managing game lobbies.
 * It provides endpoints for creating, joining, leaving, and starting lobbies,
 * as well as retrieving a list of all active lobbies. Additionally, it uses
 * STOMP messaging to notify clients of lobby updates in real time.
 */
@Controller
@RequestMapping("/api/lobbies")
public class LobbyController {

      @Autowired
      private LobbyManagerService lobbyManagerService;
      
      @Autowired
      private SimpMessagingTemplate messagingTemplate;
      private final Logger logger = LoggerFactory.getLogger(LobbyController.class);

      /**
       * Creates a new lobby with the specified name and creator UUID.
       * 
       * @param name        the name of the lobby to create
       * @param creatorUuid the UUID of the client creating the lobby
       * @return the newly created {@link Lobby}, or a 409 Conflict status if the
       *         lobby name already exists
       */
      @PostMapping("/create/lobby")
      public ResponseEntity<Lobby> createLobby(@RequestBody Map<String, String> requestBody) {
            String name = requestBody.get("name");
            String creatorUuid = requestBody.get("creatorUuid");

            if (name == null || creatorUuid == null || name.isEmpty() || creatorUuid.isEmpty()) {
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }


            //PlayerClient client = lobbyManagerService.getClient(name, creatorUuid);
            PlayerClient client = lobbyManagerService.findClientByClientId(creatorUuid);

            try {
                  Lobby newLobby = lobbyManagerService.createLobby(name, client);
                  messagingTemplate.convertAndSend("/topic/lobbies", lobbyManagerService.getAllLobbies());
                  logger.info("Creating lobby with name: {} and creatorUuid: {}", name, creatorUuid);
                  
                  return ResponseEntity.ok(newLobby);
            } catch (LobbyAlreadyExistsException e) {
                  logger.error("Error occurred: ", e);
                  return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
      }

      /**
       * Create a new player client with a name
       *
       * @param name  the player's name
       * @return  the newly created {@link PlayerClient} object
       */
      @PostMapping("/create/player")
      public ResponseEntity<PlayerClient> createPlayerClient(@RequestBody String name){
            if(name == null){
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            PlayerClient newPlayerClient = lobbyManagerService.createNewClient(name);
            logger.info("Creating new player with name: {} and playerUuid: {}", newPlayerClient.getPlayerName(), newPlayerClient.getPlayerId());

            return ResponseEntity.ok(newPlayerClient);
      }

      /**
       * Retrieves a list of all active lobbies.
       * 
       * @return a list of all {@link Lobby} objects
       */
      @GetMapping
      @ResponseBody
      public List <Lobby> getAllLobbies() {
            List<Lobby> lobbies = lobbyManagerService.getAllLobbies();
            return lobbies;
      }

      /**
       * Find a Lobby with lobby UUID
       * @param lobbyId Lobby UUID
       * @return {@link Lobby} object
       */
      @GetMapping("/lobby/{lobbyId}")
      public ResponseEntity<Lobby> findLobbyById(@PathVariable("lobbyId") String lobbyId){
            Lobby lobby = lobbyManagerService.findLobbyByLobbyId(lobbyId);
            return ResponseEntity.ok(lobby);
      }

      /**
       * Adds a player to an existing lobby.
       * 
       * @param lobbyId  the ID of the lobby to join
       * @param playerId the UUID of the player joining the lobby
       * @return the updated {@link Lobby}, or a 409 Conflict status if the game in
       *         the lobby has already started
       */
      @PostMapping("/join")
      public ResponseEntity<Lobby> joinLobby(@RequestBody Map<String, String> requestBody) {
            String lobbyId = requestBody.get("lobbyId");
            String playerId = requestBody.get("playerId");

            try {
                  Lobby joiningLobby = lobbyManagerService.joinLobby(lobbyId, playerId);
                  messagingTemplate.convertAndSend("/topic/lobbies", lobbyManagerService.getAllLobbies());
                  logger.info("Updated lobbies sent to /topic/lobbies: {}", lobbyManagerService.getAllLobbies());

                  return ResponseEntity.ok(joiningLobby);
            } catch (GameAlreadyStartedException e) {
                  logger.error("Error occurred: ", e);
                  return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
      }

      /**
       * Removes a player from an existing lobby
       * 
       * @param lobbyId  the ID of the lobby to leave
       * @param playerId the UUID of the player leaving the lobby
       * @return a {@link ResponseEntity} with an HTTP 200 OK status
       */
      @PostMapping("/leave")
      public ResponseEntity<Void> leaveLobby(@RequestBody Map<String, String> requestBody) {
            String lobbyId = requestBody.get("lobbyId");
            String playerId = requestBody.get("playerId");

            lobbyManagerService.leaveLobby(lobbyId, playerId);
            messagingTemplate.convertAndSend("/topic/lobbies", lobbyManagerService.getAllLobbies());
            logger.info("Updated lobbies sent to /topic/lobbies: {}", lobbyManagerService.getAllLobbies());

            return ResponseEntity.ok().build();
      }

      /**
       * Starts the game in the specified lobby.
       * 
       * @param lobbyId the ID of the lobby where the game is to be started
       * @return a {@link ResponseEntity} with an HTTP 200 OK status
       */
      @PostMapping("/start")
      public ResponseEntity<Void> startGame(@RequestBody Map<String, String> requestBody) {
            String lobbyId = requestBody.get("lobbyId");

            if(lobbyId == null || lobbyId.isEmpty()){
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            lobbyManagerService.startGame(lobbyId);
            //messagingTemplate.convertAndSend("/topic/lobbies/" + lobbyId, lobbyManagerService.findLobbyByUUID(lobbyId));
            messagingTemplate.convertAndSend("/topic/lobbies", lobbyManagerService.getAllLobbies());
            return ResponseEntity.ok().build();
      }

}
