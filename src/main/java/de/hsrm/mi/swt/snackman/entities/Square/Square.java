package de.hsrm.mi.swt.snackman.entities.Square;
import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;

import java.util.List;

public class Square {
    private final double DEFAULT_SIDE_LENGTH = 1;
    private Position position;
    private List<Snack> snacks;

    public Square( List<Snack> snacks, Position position ) {
        this.snacks = snacks;
        this.position = position;
        setSnackPositions();
    }

    private void setSnackPositions(){
        for(Snack snack: snacks){
            snack.setPosition(position);
        }
    }

    public double getDEFAULT_SIDE_LENGTH() {
        return DEFAULT_SIDE_LENGTH;
    }

    public List<Snack> getSnacks() {
        return snacks;
    }

    public Position getPosition() {
        return position;
    }
}
