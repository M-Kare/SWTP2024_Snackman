package de.hsrm.mi.swt.snackman.api;

import de.hsrm.mi.swt.snackman.entities.map.Wall;
import de.hsrm.mi.swt.snackman.services.MapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling map-related API requests
 * This controller provides endpoints for retrieving maze data
 */
@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class MapAPIController {

    @Autowired
    private MapService mapService;

    @GetMapping("/maze")
    public ResponseEntity<char[][]> getMaze() {
        return ResponseEntity.ok(mapService.getMazeAsArray());
    }
}
