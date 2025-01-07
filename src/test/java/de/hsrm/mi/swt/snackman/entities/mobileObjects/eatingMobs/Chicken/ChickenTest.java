package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.services.MapService;
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
}