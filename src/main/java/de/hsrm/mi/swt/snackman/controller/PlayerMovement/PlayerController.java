package de.hsrm.mi.swt.snackman.controller.PlayerMovement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import de.hsrm.mi.swt.snackman.services.MapService;

@Controller
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    private MapService mapService;
    
    // Zum Registrieren eines neuen Spielers
    @PostMapping("/snackman")
    public ResponseEntity<PlayerToFrontendDTO> initSnackman(@RequestParam("uuid") String uuid){
        SnackMan snackman = mapService.createSnackMan(uuid);
        return ResponseEntity.ok(new PlayerToFrontendDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ(), snackman.getRadius(), snackman.getSpeed(), uuid));
    }

    // TODO: Ghost ertsellen
    // @PostMapping("/ghost")
    // public ResponseEntity<PlayerToFrontendDTO> initGhost(@RequestParam("uuid") String uuid){
    //     SnackMan snackman = mapService.createSnackMan(uuid);
    //     return ResponseEntity.ok(new PlayerToFrontendDTO(snackman.getPosX(), snackman.getPosY(), snackman.getPosZ(), snackman.getRadius(), GameConfig.SNACKMAN_SPEED, uuid));
    // }
}
