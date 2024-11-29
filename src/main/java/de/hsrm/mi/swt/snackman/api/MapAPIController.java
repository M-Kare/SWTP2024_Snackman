package de.hsrm.mi.swt.snackman.api;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.services.MapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling map-related API requests
 * This controller provides endpoints for retrieving maze data
 */
@Slf4j
@RestController
// enable Cross-Origin Resource Sharing (CORS) for requests coming from the specified origin
@RequestMapping("/api")
public class MapAPIController {

    @Autowired
    private MapService mapService;

    //TODO add Logger
    @GetMapping("/maze")
    public ResponseEntity<GameMap> getMaze() {
        return ResponseEntity.ok(mapService.getGameMap());
    }
}
