package de.hsrm.mi.swt.snackman.services.Square;

import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.entities.map.Square;

public interface SquareService {

    Square getSquare();

    boolean removeSnack(Position positionOfSnackToRemove);
}
