package de.hsrm.mi.swt.snackman.lobby;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import de.hsrm.mi.swt.snackman.SnackmanApplication;
import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;
import de.hsrm.mi.swt.snackman.services.GameAlreadyStartedException;
import de.hsrm.mi.swt.snackman.services.LobbyAlreadyExistsException;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;

class LobbyManagerServiceTest {

      private LobbyManagerService lobbyManagerService;
      private static final Path workFolder = Paths.get("./extensions").toAbsolutePath();

      @BeforeAll
      static void fileSetUp() {
            try{
                  tearDownAfter();  
            }catch(Exception e){
                  System.out.println("No file to delete");
            }   
            SnackmanApplication.checkAndCopyResources();
      }

      @AfterAll
      static void tearDownAfter() throws IOException {
            if (Files.exists(workFolder)) {
                  FileSystemUtils.deleteRecursively(workFolder.toFile());
            }
      }

      @BeforeEach
      void setUp() {
            lobbyManagerService = new LobbyManagerService();
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
            lobby = lobbyManagerService.joinLobby(lobby.getUuid(), secondPlayer.getPlayerId());

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
                  lobbyManagerService.joinLobby(lobby.getUuid(), secondPlayer.getPlayerId());
            });
            lobbyManagerService.startGame(lobby.getUuid());
            PlayerClient thirdPlayer = lobbyManagerService.createNewClient("3.Player");

            assertThrows(GameAlreadyStartedException.class, () -> {
                  lobbyManagerService.joinLobby(lobby.getUuid(), thirdPlayer.getPlayerId());
            });
      }

      @Test
      public void testLeaveLobbySuccess() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            PlayerClient secondPlayer = lobbyManagerService.createNewClient("2.Player");

            assertDoesNotThrow(() -> {
                  lobbyManagerService.joinLobby(lobby.getUuid(), secondPlayer.getPlayerId());
            });

            lobbyManagerService.leaveLobby(lobby.getUuid(), secondPlayer.getPlayerId());
            assertEquals(1, lobby.getMembers().size());
            assertEquals(adminPlayer.getPlayerId(), lobby.getMembers().get(0).getPlayerId());
      }

      @Test
      public void testLeaveLobbyAdminLeavesDeletesLobby() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);
            lobbyManagerService.leaveLobby(lobby.getUuid(), adminPlayer.getPlayerId());

            assertThrows(NoSuchElementException.class, () -> {
                  lobbyManagerService.findLobbyByUUID(lobby.getUuid());
            });
      }

      @Test
      public void testStartGameSuccess() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            PlayerClient secondPlayer = lobbyManagerService.createNewClient("2.Player");
            PlayerClient thirdPlayer = lobbyManagerService.createNewClient("3.Player");

            assertDoesNotThrow(() -> {
                  lobbyManagerService.joinLobby(lobby.getUuid(), secondPlayer.getPlayerId());
                  lobbyManagerService.joinLobby(lobby.getUuid(), thirdPlayer.getPlayerId());
            });

            lobbyManagerService.startGame(lobby.getUuid());
            assertTrue(lobby.isGameStarted());
      }

      @Test
      public void testStartGameNotEnoughPlayers() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            assertThrows(IllegalStateException.class, () -> {
                  lobbyManagerService.startGame(lobby.getUuid());
            });
      }

      @Test
      public void testFindLobbyByUUIDSuccess() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

            Lobby foundLobby = lobbyManagerService.findLobbyByUUID(lobby.getUuid());

            assertNotNull(foundLobby);
            assertEquals(lobby.getUuid(), foundLobby.getUuid());
      }

      @Test
      public void testFindLobbyByUUIDNotFound() {
            assertThrows(NoSuchElementException.class, () -> {
                  lobbyManagerService.findLobbyByUUID("1234");
            });
      }

}
