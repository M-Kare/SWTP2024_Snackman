package de.hsrm.mi.swt.snackman.controller.GameMap;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.services.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling map-related API requests
 * This controller provides endpoints for retrieving maze data
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
}
