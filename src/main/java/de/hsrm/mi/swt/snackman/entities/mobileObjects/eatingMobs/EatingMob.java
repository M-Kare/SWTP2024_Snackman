package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.Mob;

/**
 * A mob which can consume snacks
 */
public abstract class EatingMob extends Mob {
    private int kcal;

    public EatingMob(GameMap gameMap, int speed, double radius) {
        super(gameMap, speed, radius);
    }

    public EatingMob(GameMap gameMap, int speed, double radius, double posX, double posY, double posZ) {
        super(gameMap, speed, radius, posX, posY, posZ);
    }

    public EatingMob(GameMap gameMap) {
        super(gameMap);
    }

    protected void setKcal(int value) {
        kcal = value;
    }

    protected int getKcal() {
        return kcal;
    }

    protected void gainKcal(int addingKcal) throws Exception {
        if ((this.kcal + addingKcal) > 0) {
            this.kcal += addingKcal;
        } else {
            throw new Exception("Kcal cannot be below zero!");
        }
    }

    protected void loseKcal() {
        // @todo
    }

    @Override
    public void move(boolean f, boolean b, boolean l, boolean r, double delta) {
        super.move(f, b, l, r, delta);

        if (getCurrentSquare().getSnack() != null)
            consumeSnackOnSquare(getCurrentSquare());
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     *
     * @param square to eat the snack from
     */
    public void consumeSnackOnSquare(Square square) {
        Snack snackOnSquare = square.getSnack();

        if (snackOnSquare != null) {
            kcal += snackOnSquare.getCalories();

            //set snack to null after consuming it
            square.setSnack(null);
        }
    }
}
