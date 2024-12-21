package de.hsrm.mi.swt.snackman.controller.GameMap;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.Ghost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
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
    private MapService mapService;

    Logger log = LoggerFactory.getLogger(MapService.class);

    @GetMapping("/game-map")
    public ResponseEntity<GameMapDTO> getGameMap() {
        log.debug("Get GameMap");
        return ResponseEntity.ok(GameMapDTO.fromGameMap(mapService.getGameMap()));
    }

    @GetMapping("/snackman")
    public ResponseEntity<SnackManInitDTO> getSnackManPos(){
        SnackMan snackman = mapService.getSnackMan();
        return ResponseEntity.ok(new SnackManInitDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ(), snackman.getRadius(), GameConfig.SNACKMAN_SPEED));
    }

    @GetMapping("/ghost")
    public ResponseEntity<GhostDTO> getGhostPos(){
        Ghost ghost = mapService.getGhost();
        return ResponseEntity.ok(new GhostDTO(ghost.getId(), ghost.getPosX(), ghost.getPosY(), ghost.getPosZ(), ghost.getRadius(), GameConfig.GHOST_SPEED));
    }


}
