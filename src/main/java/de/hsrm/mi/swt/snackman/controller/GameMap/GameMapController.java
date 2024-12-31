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
        //log.debug("Get GameMap");
        return ResponseEntity.ok(GameMapDTO.fromGameMap(mapService.getGameMap()));
    }

    @GetMapping("/snackman")
    public ResponseEntity<SnackManInitDTO> getSnackManPos(){
        SnackMan snackman = mapService.getSnackMan();
        return ResponseEntity.ok(new SnackManInitDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ(), snackman.getRadius(), GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_SPRINT_MULTIPLIER));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadMap(){
        try{
            Path filePath = Paths.get(mapService.getFilePath()).toAbsolutePath();
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

