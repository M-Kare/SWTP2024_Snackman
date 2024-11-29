package de.hsrm.mi.swt.snackman.services.Square;

import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SquareServiceImpl implements SquareService {

    //TODO is this Service needed?
    private ArrayList<Snack> snackList = new ArrayList<Snack>();

    public ArrayList<Snack> getSnackList() {
        return snackList;
    }

    @Override
    public Square getSquare() {
        Snack snack = new Snack(SnackType.ORANGE);

        snackList.add(snack);

        return new Square(0,0);
    }

    public boolean removeSnack(Position positionOfSnackToRemove) {
        return snackList.removeIf(snack -> snack.getPosition().equals(positionOfSnackToRemove));
    }
}

