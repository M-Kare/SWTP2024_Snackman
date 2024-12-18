package de.hsrm.mi.swt.snackman.services;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.messaging.EventType;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageEvent;
import de.hsrm.mi.swt.snackman.messaging.FrontendMessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
public class MapServiceTest {

    @Autowired
    private MapService mapService;

    @MockBean
    private FrontendMessageService frontendMessageService;

    public MapServiceTest() {

    }

    @Test
    void newMazeGeneratedWhenNewInstanceOfMapService() {
        List<String> mazeBeforeMapService = new LinkedList<String>();
        List<String> mazeAfterMapService = new LinkedList<String>();

        try {
            mazeBeforeMapService = Files.readAllLines(Paths.get("./Maze.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapService.generateNewMaze();

        try {
            mazeAfterMapService = Files.readAllLines(Paths.get("./Maze.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assertions.assertNotEquals(mazeAfterMapService, mazeBeforeMapService, "No new maze generated");

    }

    @Test
    void testAddEggToSquare_EggAddedToSquare() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = new Snack(SnackType.EGG);

        mapService.addEggToSquare(square, egg);

        Assertions.assertNotNull(square.getSnack());
        Assertions.assertEquals(SnackType.EGG, square.getSnack().getSnackType());
    }

    @Test
    void testAddEggToSquare_EventTypeCorrect() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = new Snack(SnackType.EGG);
        // ArgumentCaptor is used to capture the actual arguments passed to a method when it is called on a mocked
        // object, allowing you to verify and test the parameters that were used in the method call
        ArgumentCaptor<FrontendMessageEvent> eventCaptor = ArgumentCaptor.forClass(FrontendMessageEvent.class);

        mapService.addEggToSquare(square, egg);

        Mockito.verify(frontendMessageService).sendEvent(eventCaptor.capture());
        FrontendMessageEvent capturedEvent = eventCaptor.getValue();
        Assertions.assertEquals(EventType.SNACK, capturedEvent.eventType());
    }

    @Test
    void testAddEggToSquare_SquareInEventCorrect() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = new Snack(SnackType.EGG);
        ArgumentCaptor<FrontendMessageEvent> eventCaptor = ArgumentCaptor.forClass(FrontendMessageEvent.class);

        mapService.addEggToSquare(square, egg);

        Mockito.verify(frontendMessageService).sendEvent(eventCaptor.capture());
        FrontendMessageEvent capturedEvent = eventCaptor.getValue();
        Assertions.assertNotNull(capturedEvent.square());

        Assertions.assertEquals(MapObjectType.FLOOR, capturedEvent.square().getType());
        Assertions.assertEquals(0, capturedEvent.square().getIndexX());
        Assertions.assertEquals(0, capturedEvent.square().getIndexZ());
    }

    @Test
    void testAddEggToSquare_SnackInEventCorrect() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Snack egg = new Snack(SnackType.EGG);
        ArgumentCaptor<FrontendMessageEvent> eventCaptor = ArgumentCaptor.forClass(FrontendMessageEvent.class);

        mapService.addEggToSquare(square, egg);

        Mockito.verify(frontendMessageService).sendEvent(eventCaptor.capture());
        FrontendMessageEvent capturedEvent = eventCaptor.getValue();
        Assertions.assertNotNull(capturedEvent.square().getSnack());
        Assertions.assertEquals(SnackType.EGG, capturedEvent.square().getSnack().getSnackType());
    }

    @Test
    void testAddRandomSnackToSquare_WithoutEggs() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);

        mapService.addRandomSnackToSquare(square);

        Assertions.assertNotNull(square.getSnack());
        Assertions.assertNotEquals(SnackType.EGG, square.getSnack().getSnackType());
    }
}