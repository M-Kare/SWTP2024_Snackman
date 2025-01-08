package de.hsrm.mi.swt.snackman.controller.GameMap;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Upload custom map and save in folder "./extensions/map"
     * @param file file .txt
     * @return
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadMap(@RequestParam("file") MultipartFile file, @RequestParam("lobbyId") String lobbyId){
        try{
            if (!file.getOriginalFilename().endsWith(".txt")) {
                return ResponseEntity.badRequest().body("Invalid file type. Only .txt files are allowed.");
            }

            Path uploadPath = Paths.get("./extensions/map").toAbsolutePath();

            String fileName = String.format("SnackManMap_%s.txt", lobbyId);

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e){
            log.error("Error occurred: ", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
}

