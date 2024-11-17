package de.hsrm.mi.swt.snackman.services.Snack;

import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;
import de.hsrm.mi.swt.snackman.entities.Snack.SnackType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnackServiceImpl implements SnackService {
    @Override
    public List<Snack> getSnacks() {
        var snack1 = new Snack(1, SnackType.CHERRY, new Position(5, 1, 5));
        var snack2 = new Snack(2, SnackType.ORANGE, new Position(3, 1, 3));
        var snack3 = new Snack(3, SnackType.STRAWBERRY, new Position(4, 1, 4));

        return List.of(snack1, snack2, snack3);
    }
}
