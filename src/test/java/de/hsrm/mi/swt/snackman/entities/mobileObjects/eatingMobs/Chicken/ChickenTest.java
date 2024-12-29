package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.services.MapService;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChickenTest {

    @Autowired
    private MapService mapService;

    @Test
    void testLayEgg_ChickenThicknessAndKcalReset() {
        Square square = new Square(MapObjectType.FLOOR, 0, 0);
        Chicken chicken = new Chicken(square, mapService);
        chicken.setKcal(3000);
        chicken.setThickness(Thickness.HEAVY);

        chicken.layEgg();

        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());
        Assertions.assertEquals(0, chicken.getKcal());
    }

    @Test
    void chickenGetsFatWhenComsumincSnacks(){
        //MapService mapService1 = mock(MapService.class);
        Snack snack = new Snack(SnackType.STRAWBERRY);
        
        Square square = new Square(snack, 0, 0);
        mapService.setSquare(square, 0, 0);

        Chicken chicken = new Chicken(square, mapService);
        
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.THIN, chicken.getThickness());

        snack.setSnackType(SnackType.ORANGE);

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.SLIGHTLY_THICK, chicken.getThickness());

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.MEDIUM, chicken.getThickness());

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.HEAVY, chicken.getThickness());

        square.setSnack(snack);
        chicken.consumeSnackOnSquare();
        Assertions.assertEquals(Thickness.VERY_HEAVY, chicken.getThickness());


    }
}