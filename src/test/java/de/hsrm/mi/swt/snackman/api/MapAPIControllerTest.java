package de.hsrm.mi.swt.snackman.api;

import de.hsrm.mi.swt.snackman.services.MapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MapAPIControllerTest {

    @Mock
    private MapService mapService;

    @InjectMocks
    private MapAPIController mapAPIController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMaze_shouldReturnMazeData() {

        char[][] expectedMaze = {
                {'#', '#', '#'},
                {'#', ' ', '#'},
                {'#', '#', '#'}
        };
        when(mapService.getMazeAsArray()).thenReturn(expectedMaze);

        ResponseEntity<char[][]> response = mapAPIController.getMaze();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(expectedMaze, response.getBody());
    }
}