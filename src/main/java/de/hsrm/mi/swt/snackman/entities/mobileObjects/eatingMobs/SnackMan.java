package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.services.MapService;

public class SnackMan extends EatingMob {

    private int currentCalories;

    public SnackMan(MapService mapService, int speed, double radius){
        super(mapService, speed, radius);    }

    public SnackMan(MapService mapService, int speed, double radius, double posX, double posY, double posZ){
        super(mapService, speed, radius, posX, posY, posZ);
    }

    @Override
    public void gainKcal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gainKcal'");
    }

    @Override
    public void loseKcal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loseKcal'");
    }

    public void jump(){

    }

    private void jumpOverChicken(){

    }

    private void jumpToSeeMap(){

    }

    private void jumpOverWall(){

    }

    public void collectItems(){

    }

    @Override
    public void move(double x, double y, double z) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'move'");
    }

    public int getCurrentCalories() {
        return currentCalories;
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     * @param square to eat the snack from
     */
    public void consumeSnackOnSquare(Square square){
        Snack snackOnSquare = square.getSnack();

        if(snackOnSquare != null){
            currentCalories += snackOnSquare.getCalories();

            //set snack to null after consuming it
            square.setSnack(null);
        }
    }
}
