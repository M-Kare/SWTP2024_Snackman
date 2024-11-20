package de.hsrm.mi.swt.snackman.entities.Square;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;

import java.util.List;

public class Square {

    private final int widthX = 1;
    private final double hightY = 0.1;
    private final int depthZ = 1;
    private List<Snack> snacks;

    public Square( List<Snack> snacks) {
        this.snacks = snacks;
    }

    public List<Snack> getSnacks() {
        return snacks;
    }

    public double getHightY() {
        return hightY;
    }

    public void setSnacks(List<Snack> snacks) {
        this.snacks = snacks;
    }

    public int getDepthZ() {
        return depthZ;
    }

    public int getWidthX() {
        return widthX;
    }
}
