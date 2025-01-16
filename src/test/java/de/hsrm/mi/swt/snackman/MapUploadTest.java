package de.hsrm.mi.swt.snackman;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({
        "'G G G G\nC o # #', 'noSnackman.txt', 'The map file must contain exactly one ''S''.'",
        "'S S G G\nC o # #', 'multipleSnackman.txt', 'The map file must contain exactly one ''S''.'",
        "'S G G C\nC o # #', 'notEnoughGhosts.txt', 'The map file must contain at least 4 ''G''s for 4 ghost player.'"
    })
    void testUploadMap_InvalidCases(String mapContent, String fileName, String expectedMessage) {
        MockMultipartFile file = new MockMultipartFile(
            "file", fileName, "./extension/map", mapContent.getBytes(StandardCharsets.UTF_8)
        );

        ResponseEntity<String> response = gameMapController.uploadMap(file, "1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }

}
