package de.hsrm.mi.swt.snackman;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;
import de.hsrm.mi.swt.snackman.entities.Snack.SnackType;
import de.hsrm.mi.swt.snackman.services.Square.SquareServiceImpl;

public class SquareServiceImplTest {

    private SquareServiceImpl squareService;

    @BeforeEach
    public void setUp() {
        squareService = new SquareServiceImpl();
    }

    @Test
    public void testRemoveSnack_Success() {
        Snack snack = new Snack(SnackType.ORANGE);
        snack.setPosition(new Position(0, 0));
        squareService.getSnackList().add(snack);
        
        boolean result = squareService.removeSnack(snack);
        
        assertTrue(result);
        assertTrue(squareService.getSnackList().isEmpty(), "Snack list is supposed to be empty.");
    }

    @Test
    public void testRemoveSnack_Fail_SnackNotFound() {
        Snack snackToRemove = new Snack(SnackType.STRAWBERRY);
        snackToRemove.setPosition(new Position(1, 1));
        
        boolean result = squareService.removeSnack(snackToRemove);
        
        assertFalse(result);
    }
}

