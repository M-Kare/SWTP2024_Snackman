package de.hsrm.mi.swt.snackman.entities.Square;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;

import java.util.List;

public class Square {

    private final int widthX = 100;
    private final int depthZ = 100;
    private List<Snack> snacks;

    public Square( List<Snack> snacks) {
        this.snacks = snacks;
    }

    public List<Snack> getSnacks() {
        return snacks;
    }

    public void setSnacks(List<Snack> snacks) {
        this.snacks = snacks;
    }
}
