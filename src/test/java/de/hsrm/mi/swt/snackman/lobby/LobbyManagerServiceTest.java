package de.hsrm.mi.swt.snackman.lobby;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Map;
import java.util.NoSuchElementException;
import de.hsrm.mi.swt.snackman.controller.Lobby.LobbyController;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.FileSystemUtils;

import de.hsrm.mi.swt.snackman.SnackmanApplication;
import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.services.GameAlreadyStartedException;
import de.hsrm.mi.swt.snackman.services.LobbyAlreadyExistsException;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class LobbyManagerServiceTest {

      @Mock
      private MapService mapService;

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
      public void setup() {
            Square[][] emptyMap = { {new Square(0,0), new Square(0,1), new Square(0,2)},
                                {new Square(1,0), new Square(1,1), new Square(1,2)},
                                {new Square(2,0), new Square(2,1), new Square(2,2)} };
            GameMap gameMap = new GameMap(emptyMap);
            Mockito.when(mapService.createNewGameMap(Mockito.any())).thenReturn(gameMap);
            lobbyManagerService = new LobbyManagerService(mapService, null);
      }
    @Mock
    private LobbyController lobbyController;

    @Mock
    private FrontendMessageService frontendMessageService;

    @BeforeEach
    void setUp() {
        lobbyManagerService = new LobbyManagerService();
        frontendMessageService = mock(FrontendMessageService.class);
        lobbyController = new LobbyController(lobbyManagerService, frontendMessageService);
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
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);

            assertNotNull(lobby);
            assertEquals("TestLobby", lobby.getName());
            assertEquals(adminPlayer.getPlayerId(), lobby.getAdminClientId());
            assertEquals(ROLE.SNACKMAN, adminPlayer.getRole());
      }

      @Test
      public void testCreateLobbyDuplicateNameThrowsException() {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");

            assertThrows(LobbyAlreadyExistsException.class, () -> {
                  lobbyManagerService.createLobby("DuplicateLobby", adminPlayer, null);
                  lobbyManagerService.createLobby("DuplicateLobby", adminPlayer, null);
            });
      }

      @Test
      public void testJoinLobbySuccess() throws LobbyAlreadyExistsException, GameAlreadyStartedException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);
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
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);

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
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);

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
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);
            lobbyManagerService.leaveLobby(lobby.getLobbyId(), adminPlayer.getPlayerId());

            assertThrows(NoSuchElementException.class, () -> {
                  lobbyManagerService.findLobbyByLobbyId(lobby.getLobbyId());
            });
      }

      @Test
      public void testStartGameSuccess() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);

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
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);

            assertThrows(IllegalStateException.class, () -> {
                  lobbyManagerService.startGame(lobby.getLobbyId());
            });
      }

      @Test
      public void testFindLobbyByUUIDSuccess() throws LobbyAlreadyExistsException {
            PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
            Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);

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

    @Test
    public void testAssignSnackmanSuccess() throws LobbyAlreadyExistsException {
        PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);
        lobby.setGameStarted();

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getLobbyId(),
                "playerId", adminPlayer.getPlayerId(),
                "role", "SNACKMAN"
        ));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(adminPlayer.getRole(), ROLE.SNACKMAN);
    }

    @Test
    public void testAssignGhostSuccess() throws LobbyAlreadyExistsException {
        PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);
        lobby.setGameStarted();

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getLobbyId(),
                "playerId", adminPlayer.getPlayerId(),
                "role", "GHOST"
        ));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(adminPlayer.getRole(), ROLE.GHOST);
    }

    @Test
    public void testRoleAssignmentLobbyNotStarted() throws LobbyAlreadyExistsException {
        PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getLobbyId(),
                "playerId", adminPlayer.getPlayerId(),
                "role", "GHOST"
        ));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testRoleSnackmanAlreadySelected() throws LobbyAlreadyExistsException {
        PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
        PlayerClient secondPlayer = lobbyManagerService.createNewClient("SecondPlayer");
        secondPlayer.setRole(ROLE.SNACKMAN);
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);
        lobby.getMembers().add(secondPlayer);
        lobby.setGameStarted();

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getLobbyId(),
                "playerId", adminPlayer.getPlayerId(),
                "role", "SNACKMAN"
        ));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertSame(adminPlayer.getRole(), ROLE.GHOST);
        assertSame(secondPlayer.getRole(), ROLE.SNACKMAN);
    }

    @Test
    public void testAssignInvalidRole() throws LobbyAlreadyExistsException {
        PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer, null);
        lobby.setGameStarted();

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getLobbyId(),
                "playerId", adminPlayer.getPlayerId(),
                "role", "INVALID_ROLE"
        ));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
