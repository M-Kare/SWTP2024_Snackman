package de.hsrm.mi.swt.snackman.entities.Square;
import de.hsrm.mi.swt.snackman.Types.Position;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;

import java.util.List;

public class Square {
    private final double HEIGHT = 0.1;
    private Position position;
    private List<Snack> snacks;

    public Square( List<Snack> snacks, Position position ) {
        this.snacks = snacks;
        setSnackPositions();
    }

    private void setSnackPositions(){
        for(Snack snack: snacks){
            snack.setPosition(position);
        }
    }

    public List<Snack> getSnacks() {
        return snacks;
    }

    public double getHEIGHT() {
        return HEIGHT;
    }

    public Position getPosition() {
        return position;
    }
}
