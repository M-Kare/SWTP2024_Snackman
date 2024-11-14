package de.hsrm.mi.swt.snackman.entities.Snack;

public enum SnackTyp {
    KIRSCHE(100),
    ERDBEERE(300),
    ORANGE(500);

    private final int kalorien;
    SnackTyp(int kalorien) {
        this.kalorien = kalorien;
    }

    public int getKalorien() {
        return kalorien;
    }
}
