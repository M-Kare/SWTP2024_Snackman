package de.hsrm.mi.swt.snackman.services.Square;

import de.hsrm.mi.swt.snackman.entities.Snack.Snack;
import de.hsrm.mi.swt.snackman.entities.Square.Square;

public interface SquareService {

    Square getSquare();

    boolean removeSnack(Snack snackToRemove);
}
