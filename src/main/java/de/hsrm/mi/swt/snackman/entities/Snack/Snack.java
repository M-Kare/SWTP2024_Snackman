package de.hsrm.mi.swt.snackman.entities.Snack;

/**
 * Class that defines Snack, that can be eaten by Snackman
 */
public class Snack {
    private int id;
    private SnackTyp snackTyp;
    private int kalorien;

    public Snack(int id, SnackTyp snackTyp) {
        this.id = id;
        this.snackTyp = snackTyp;
        this.kalorien = snackTyp.getKalorien();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SnackTyp getSnackTyp() {
        return snackTyp;
    }

    public void setSnackTyp(SnackTyp snackTyp) {
        this.snackTyp = snackTyp;
    }

    public int getKalorien() {
        return kalorien;
    }

    public void setKalorien(int kalorien) {
        this.kalorien = kalorien;
    }
}
