package de.hsrm.mi.swt.snackman.lobby;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;
import de.hsrm.mi.swt.snackman.services.GameAlreadyStartedException;
import de.hsrm.mi.swt.snackman.services.LobbyAlreadyExistsException;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;
import de.hsrm.mi.swt.snackman.services.MapService;

@SpringBootTest
public class LobbyManagerServiceTest {

      @Autowired
      private MapService mapService;
      private LobbyManagerService lobbyManagerService;

      @BeforeEach
      public void setup(){
            lobbyManagerService = new LobbyManagerService(mapService);
      }

      @Test
      public void testCreateNewClient() {
            PlayerClient newPlayer = lobbyManagerService.createNewClient("TestPlayer");

            assertNotNull(newPlayer);
            assertEquals("TestPlayer", newPlayer.getPlayerName());
            assertNotNull(newPlayer.getPlayerId());
      }

      @Test
      public void testCreateLobbySuccess() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            assertNotNull(lobby);
            assertEquals("TestLobby", lobby.getName());
            assertEquals(adminPlayer.getPlayerId(), lobby.getAdminClientId());
            assertEquals(ROLE.SNACKMAN, adminPlayer.getRole());
      }

      @Test
      public void testCreateLobbyDuplicateNameThrowsException() {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");

            assertThrows(LobbyAlreadyExistsException.class, () -> {
                  lobbyManagerService.createLobby("DuplicateLobby", adminPlayer);
                  lobbyManagerService.createLobby("DuplicateLobby", adminPlayer);
            });
      }

      @Test
      public void testJoinLobbySuccess() throws LobbyAlreadyExistsException, GameAlreadyStartedException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);
            PlayerClient secondPlayer = lobbyManagerService.createNewClient("2.Player");
            lobby = lobbyManagerService.joinLobby(lobby.getLobbyId(), secondPlayer.getPlayerId());

            assertNotNull(lobby);
            assertEquals(2, lobby.getMembers().size());
            assertEquals(secondPlayer.getPlayerId(), lobby.getMembers().get(1).getPlayerId());
      }

      @Test
      public void testJoinLobbyGameAlreadyStarted() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            PlayerClient secondPlayer = lobbyManagerService.createNewClient("2.Player");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            assertDoesNotThrow(() -> {
                  lobbyManagerService.joinLobby(lobby.getLobbyId(), secondPlayer.getPlayerId());
            });
            lobbyManagerService.startGame(lobby.getLobbyId());
            PlayerClient thirdPlayer = lobbyManagerService.createNewClient("3.Player");

            assertThrows(GameAlreadyStartedException.class, () -> {
                  lobbyManagerService.joinLobby(lobby.getLobbyId(), thirdPlayer.getPlayerId());
            });
      }

      @Test
      public void testLeaveLobbySuccess() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            PlayerClient secondPlayer = lobbyManagerService.createNewClient("2.Player");

            assertDoesNotThrow(() -> {
                  lobbyManagerService.joinLobby(lobby.getLobbyId(), secondPlayer.getPlayerId());
            });

            lobbyManagerService.leaveLobby(lobby.getLobbyId(), secondPlayer.getPlayerId());
            assertEquals(1, lobby.getMembers().size());
            assertEquals(adminPlayer.getPlayerId(), lobby.getMembers().get(0).getPlayerId());
      }

      @Test
      public void testLeaveLobbyAdminLeavesDeletesLobby() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);
            lobbyManagerService.leaveLobby(lobby.getLobbyId(), adminPlayer.getPlayerId());

            assertThrows(NoSuchElementException.class, () -> {
                  lobbyManagerService.findLobbyByLobbyId(lobby.getLobbyId());
            });
      }

      @Test
      public void testStartGameSuccess() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            PlayerClient secondPlayer = lobbyManagerService.createNewClient("2.Player");
            PlayerClient thirdPlayer = lobbyManagerService.createNewClient("3.Player");

            assertDoesNotThrow(() -> {
                  lobbyManagerService.joinLobby(lobby.getLobbyId(), secondPlayer.getPlayerId());
                  lobbyManagerService.joinLobby(lobby.getLobbyId(), thirdPlayer.getPlayerId());
            });

            lobbyManagerService.startGame(lobby.getLobbyId());
            assertTrue(lobby.isGameStarted());
      }

      @Test
      public void testStartGameNotEnoughPlayers() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            assertThrows(IllegalStateException.class, () -> {
                  lobbyManagerService.startGame(lobby.getLobbyId());
            });
      }

      @Test
      public void testFindLobbyByUUIDSuccess() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            Lobby foundLobby = lobbyManagerService.findLobbyByLobbyId(lobby.getLobbyId());

            assertNotNull(foundLobby);
            assertEquals(lobby.getLobbyId(), foundLobby.getLobbyId());
      }

      @Test
      public void testFindLobbyByUUIDNotFound() {
            assertThrows(NoSuchElementException.class, () -> {
                  lobbyManagerService.findLobbyByLobbyId("1234");
            });
      }

}
