package de.hsrm.mi.swt.snackman.controller.GameMap;

import de.hsrm.mi.swt.snackman.controller.Ghost.GhostDTO;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.mi.swt.snackman.services.LobbyManagerService;
import de.hsrm.mi.swt.snackman.services.MapService;

/**
 * REST Controller for handling map-related API requests
 * This controller provides endpoints for retrieving game map data
 */
@RestController
// enable Cross-Origin Resource Sharing (CORS) for requests coming from the specified origin
@RequestMapping("/api")
public class GameMapController {

    @Autowired
    private LobbyManagerService lobbyManagerService;

    Logger log = LoggerFactory.getLogger(MapService.class);

    @GetMapping("/lobby/{lobbyId}/game-map")
    public ResponseEntity<GameMapDTO> getGameMap(@PathVariable("lobbyId") String lobbyId) {
        log.debug("Get GameMap");
        return ResponseEntity.ok(GameMapDTO.fromGameMap(lobbyManagerService.getGameMapByLobbyId(lobbyId)));
    }

    @GetMapping("/ghost")
    public ResponseEntity<GhostDTO> getGhostPos(@RequestParam long id){
        Ghost ghost = mapService.getGhost(id);
        return ResponseEntity.ok(new GhostDTO(ghost.getId(), ghost.getPosX(), ghost.getPosY(), ghost.getPosZ(), ghost.getRadius(), GameConfig.GHOST_SPEED));
    }


}
