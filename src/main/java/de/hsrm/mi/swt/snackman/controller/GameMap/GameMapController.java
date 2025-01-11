package de.hsrm.mi.swt.snackman.controller.GameMap;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.hsrm.mi.swt.snackman.entities.lobby.Lobby;
import de.hsrm.mi.swt.snackman.services.LobbyManagerService;

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
     * Upload custom map and save in folder "./extensions/map"
     * @param file uploaded File
     * @param lobbyId lobbyId, where upload custom map
     * @return ResponseEntity: file is uploaded sucessfully or 
     *          409 (Conflict) status of an error occurs during the upload process
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

    /**
     * Deletes the uploaded map file associated with a given lobby ID.
     * This method is invoked when a lobby no longer exists, and the map file
     * for that lobby needs to be deleted from the server.
     * @param requestBody A map containing the lobbyId
     * @return Returns an HTTP status code:
     *     - HTTP 200 (OK) if the file was successfully deleted.
     *     - HTTP 404 (Not Found) if the file does not exist.
     *     - HTTP 500 (Internal Server Error) if an error occurs during the deletion process.
     */
    @DeleteMapping("/deleteMap")
    public ResponseEntity<Void> deleteUploadedMap(@RequestParam("lobbyId") String lobbyId){
        try {
            String fileName = String.format("SnackManMap_%s.txt", lobbyId);
            Path filePath = Paths.get("./extensions/map/" + fileName).toAbsolutePath();

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IOException e) {
            log.error("Error occurred while deleting the file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates the custom map usage status for a lobby.
     *
     * @param requestBody A Map containing the following parameters:
     *                    - lobbyId          :The ID of the lobby (String).
     *                    - usedCustomMap    :The custom map usage status (Boolean).
     * @return Returns an HTTP status code:
     *         - 200 OK if the update is successful.
     *         - 404 Not Found if the lobby is not found.
     *         - 400 Bad Request if an error occurs during processing.
     */
    @PostMapping("/change-used-map-status")
    public ResponseEntity<Void> updateUsedMapStatus(@RequestBody Map<String, Object> requestBody){
        try{
            String lobbyId = (String) requestBody.get("lobbyId");
            Boolean usedCustomMap = (Boolean) requestBody.get("usedCustomMap");
            
            Lobby lobby = lobbyManagerService.findLobbyByLobbyId(lobbyId);
            if (lobby == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        
            lobby.setUsedCustomMap(usedCustomMap);
            log.info(lobbyId + " have the staus of used custom map: " + usedCustomMap.toString());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
    }
}

