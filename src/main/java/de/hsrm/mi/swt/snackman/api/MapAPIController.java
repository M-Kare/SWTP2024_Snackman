package de.hsrm.mi.swt.snackman.api;

import de.hsrm.mi.swt.snackman.services.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST Controller for handling map-related API requests
 * This controller provides endpoints for retrieving maze data
 */
@RestController
// enable Cross-Origin Resource Sharing (CORS) for requests coming from the specified origin
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class MapAPIController {

    @Autowired
    private MapService mapService;

    @GetMapping("/maze")
    public ResponseEntity<Map<String, Object>> getMaze() {
        return ResponseEntity.ok(mapService.prepareMazeForJson());
    }
}
