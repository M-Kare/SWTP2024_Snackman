package de.hsrm.mi.swt.snackman.entities.Snack;

/**
 * Class that defines Snack, that can be eaten by Snackman
 */
public class Snack {
    private String name;
    private int kalorien;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKalorien() {
        return kalorien;
    }

    public void setKalorien(int kalorien) {
        this.kalorien = kalorien;
    }
}
