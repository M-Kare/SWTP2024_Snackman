package de.hsrm.mi.swt.snackman.lobby;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Map;
import java.util.NoSuchElementException;
import de.hsrm.mi.swt.snackman.controller.Lobby.LobbyController;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.entities.lobby.PlayerClient;
import de.hsrm.mi.swt.snackman.entities.lobby.ROLE;
import de.hsrm.mi.swt.snackman.services.GameAlreadyStartedException;
import de.hsrm.mi.swt.snackman.services.LobbyAlreadyExistsException;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class LobbyManagerServiceTest {

    private LobbyManagerService lobbyManagerService;

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
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

        assertNotNull(lobby);
        assertEquals("TestLobby", lobby.getName());
        assertEquals(adminPlayer.getPlayerId(), lobby.getAdminClientId());
        assertEquals(ROLE.GHOST, adminPlayer.getRole());
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

    @Test
    public void testAssignSnackmanSuccess() throws LobbyAlreadyExistsException {
        PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);
        lobby.setGameStarted(true);

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getUuid(),
                "playerId", adminPlayer.getPlayerId(),
                "role", "SNACKMAN"
        ));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(adminPlayer.getRole(), ROLE.SNACKMAN);
    }

    @Test
    public void testAssignGhostSuccess() throws LobbyAlreadyExistsException {
        PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);
        lobby.setGameStarted(true);

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getUuid(),
                "playerId", adminPlayer.getPlayerId(),
                "role", "GHOST"
        ));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(adminPlayer.getRole(), ROLE.GHOST);
    }

    @Test
    public void testRoleAssignmentLobbyNotStarted() throws LobbyAlreadyExistsException {
        PlayerClient adminPlayer = lobbyManagerService.createNewClient("AdminPlayer");
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getUuid(),
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
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);
        lobby.getMembers().add(secondPlayer);
        lobby.setGameStarted(true);

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getUuid(),
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
        Lobby lobby = lobbyManagerService.createLobby("TestLobby", adminPlayer);

        ResponseEntity<Void> response = lobbyController.setPlayerRole(Map.of(
                "lobbyId", lobby.getUuid(),
                "playerId", adminPlayer.getPlayerId(),
                "role", "INVALID_ROLE"
        ));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

}
