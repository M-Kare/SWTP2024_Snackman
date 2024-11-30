package de.hsrm.mi.swt.snackman.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hsrm.mi.swt.snackman.services.MapService;

class MapAPIControllerTest {

    @Mock
    private MapService mapService;

    // @InjectMocks
    // private MapAPIController mapAPIController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    Map<String, Object> createSimpleMap() {
        // Create a simplified version of the expected maze data
        Map<String, Object> expectedMaze = new HashMap<>();
        expectedMaze.put("default-side-length", 1);
        expectedMaze.put("height", 3);

        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(Map.of("x", 0, "y", 0, "type", "wall"));
        mapList.add(Map.of("x", 14, "y", 14, "type", "wall"));
        expectedMaze.put("map", mapList);

        return expectedMaze;
    }

    @Test
    void getMaze_shouldReturnMazeData() {
        // Map<String, Object> expectedMaze = createSimpleMap();

        // // Mock the mapService to return the expected maze data
        // // when(mapService.prepareMazeForJson()).thenReturn(expectedMaze);

        // // Call the controller method
        // // ResponseEntity<Map<String, Object>> response = mapAPIController.getMaze();

        // // Assert the response
        // assertEquals(HttpStatus.OK, response.getStatusCode());
        // assertEquals(expectedMaze, response.getBody());

        // // Additional assertions to verify the structure of the returned data
        // Map<String, Object> responseBody = response.getBody();
        // assertNotNull(responseBody);
        // assertEquals(1, responseBody.get("default-side-length"));
        // assertEquals(3, responseBody.get("height"));
        // assertInstanceOf(List.class, responseBody.get("map"));

        // List<?> map = (List<?>) responseBody.get("map");
        // assertFalse(map.isEmpty());
        // assertInstanceOf(Map.class, map.getFirst());
    }
}