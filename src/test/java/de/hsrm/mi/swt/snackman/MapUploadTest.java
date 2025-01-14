package de.hsrm.mi.swt.snackman;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import de.hsrm.mi.swt.snackman.controller.GameMap.GameMapController;

@SpringBootTest
public class MapUploadTest {
    
    @Autowired
    private GameMapController gameMapController;

    @Test
    void testUploadMap_InvalidMapContent(){
        String invalidMap = "A S G \nC o # #";
        MockMultipartFile file = new MockMultipartFile(
            "file", "invalidMap.txt","./extension/map", invalidMap.getBytes(StandardCharsets.UTF_8)
        );

        ResponseEntity<String> response = gameMapController.uploadMap(file, "1");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The map file is only allowed to contain the following characters: S, G, C, o, #, and spaces.", response.getBody());
    }

    @Test
    void testUploadMap_noSnackman(){
        String mapContent = "G G G G\nC o # #";
        MockMultipartFile file = new MockMultipartFile(
            "file", "noSnackman.txt", "./extension/map", mapContent.getBytes(StandardCharsets.UTF_8)
        );

        ResponseEntity<String> response = gameMapController.uploadMap(file, "1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The map file must contain exactly one 'S'.", response.getBody());
    }

    @Test
    void testUploadMap_multipleSnackman(){
        String mapContent = "S S G G\nC o # #";
        MockMultipartFile file = new MockMultipartFile(
            "file", "multipleSnackman.txt", "./extension/map", mapContent.getBytes(StandardCharsets.UTF_8)
        );

        ResponseEntity<String> response = gameMapController.uploadMap(file, "1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The map file must contain exactly one 'S'.", response.getBody());
    }

    @Test
    void testUploadMap_notEnoughGhosts(){
        String mapContent = "S G G C\nC o # #";
        MockMultipartFile file = new MockMultipartFile(
            "file", "notEnoughGhosts.txt", "./extension/map", mapContent.getBytes(StandardCharsets.UTF_8)
        );

        ResponseEntity<String> response = gameMapController.uploadMap(file, "1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The map file must contain at least 4 'G's for 4 ghost player.", response.getBody());
    }

}
