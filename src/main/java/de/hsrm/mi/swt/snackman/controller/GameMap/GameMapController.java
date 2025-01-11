package de.hsrm.mi.swt.snackman.controller.GameMap;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    Logger log = LoggerFactory.getLogger(GameMapController.class);

    @GetMapping("/lobby/{lobbyId}/game-map")
    public ResponseEntity<GameMapDTO> getGameMap(@PathVariable("lobbyId") String lobbyId) {
        log.debug("Get GameMap");
        return ResponseEntity.ok(GameMapDTO.fromGameMap(lobbyManagerService.getGameMapByLobbyId(lobbyId)));
    }

    /**
     * Downloads the last map file as "SnackManMap.txt".
     *
     * @return ResponseEntity containing the map file as a resource, or:
     *         - HTTP 404 (Not Found) if the file does not exist.
     *         - HTTP 409 (Conflict) if an error occurs during the process.
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadMap(){
        
        // TODO: download map with the lobbyId

        try{
            Path filePath = Paths.get("./extensions/map/LastMap.txt").toAbsolutePath();
            Resource resource = new UrlResource(filePath.toUri());

            if(!resource.exists()){
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"SnackManMap.txt\"").body(resource);
        } catch (Exception e){
            log.error("Error occurred: ", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

}

