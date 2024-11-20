package de.hsrm.mi.swt.snackman.services.Square;

import de.hsrm.mi.swt.snackman.entities.Snack.Snack;
import de.hsrm.mi.swt.snackman.entities.Snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.Square.Square;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SquareServiceImpl implements SquareService {

    @Override
    public Square getSquare() {
        ArrayList<Snack> snackList = new ArrayList<Snack>();
        snackList.add(new Snack( 1 , SnackType.ORANGE ));

        Square test = new Square(snackList);

        return test;
    }
}
