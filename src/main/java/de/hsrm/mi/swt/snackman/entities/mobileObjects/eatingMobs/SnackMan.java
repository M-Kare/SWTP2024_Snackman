package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.services.MapService;

@Component
public class SnackMan extends EatingMob {

    //JUMPING
    private boolean isJumping = false;
    private double velocityY = 0.0;
    //JUMPING

    private int currentCalories;
    private double posX;
    private double posY;
    private double posZ;
    private double radius;
    private Quaterniond quat;
    private Square currentSquare;

    private MapService mapService;

    @Autowired
    public SnackMan(MapService mapService){
        super();
        currentCalories = 0;

        this.mapService = mapService;
        posY = GameConfig.SNACKMAN_GROUND_LEVEL;
        posX = (this.mapService.getGameMap().getGameMap().length/2)*GameConfig.SQUARE_SIZE;
        posZ = (this.mapService.getGameMap().getGameMap()[0].length/2)*GameConfig.SQUARE_SIZE;
        radius = GameConfig.SNACKMAN_RADIUS;
        quat = new Quaterniond();
        setCurrentSquareWithIndex(posX, posY);
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Square getCurrentSquare() {
        return currentSquare;
    }

    public void setCurrentSquareWithIndex(double x, double z) {
        currentSquare = mapService.getSquareAtIndexXZ(calcMapIndexOfCoordinate(x), calcMapIndexOfCoordinate(z));
    }

    public void move(boolean f, boolean b, boolean l, boolean r, double delta) {
        //System.out.println(currentSquare.getIndexX() + "  |  " + currentSquare.getIndexZ());
        int moveDirZ = (f ? 1 : 0) - (b ? 1 : 0);
        int moveDirX = (r ? 1 : 0) - (l ? 1 : 0);

        Vector3d move = new Vector3d();

        if (f || b) {
            move.z -= moveDirZ;
        }
        if (l || r) {
            move.x += moveDirX;
        }
        if(move.x != 0 && move.z != 0){
            move.normalize();
        }
            move.x = move.x * delta * GameConfig.SNACKMAN_SPEED;
            move.z = move.z * delta * GameConfig.SNACKMAN_SPEED;
        move.rotate(quat);
        double xNew = posX + move.x;
        double zNew = posZ + move.z;
        int result = checkWallCollision(xNew, zNew);
        switch (result) {
            case 0:
                posX += move.x;
                posZ += move.z;
                break;
            case 1:
                posZ += move.z;
                break;
            case 2:
                posX += move.x;
                break;
            case 3:
                // move.normalize();
                // posX += (-0.1)*move.x;
                // posZ += (-0.1)*move.z;
                break;
        }
        setCurrentSquareWithIndex(posX, posZ);
        consumeSnackOnSquare(currentSquare);
    }

    // TODO: Find out why frontend and backend are not completly synced (labyrinth size not displayed correcty in frontend?)
    public int checkWallCollision(double x, double z) {
        int change = 0;
        double middleX = currentSquare.getIndexX()*GameConfig.SQUARE_SIZE + GameConfig.SQUARE_SIZE/2;
        double middleZ = currentSquare.getIndexZ()*GameConfig.SQUARE_SIZE + GameConfig.SQUARE_SIZE/2;
        int horizontal = (x - middleX <= 0) ? -1 : 1;
        int vertical = (z - middleZ <= 0) ? -1 : 1;
        Square squareX = mapService.getSquareAtIndexXZ(currentSquare.getIndexX()+horizontal, currentSquare.getIndexZ());
        Square squareZ = mapService.getSquareAtIndexXZ(currentSquare.getIndexX(), currentSquare.getIndexZ()+vertical);
        Square squareDiagonal = mapService.getSquareAtIndexXZ(currentSquare.getIndexX()+horizontal, currentSquare.getIndexZ()+vertical);
        if (squareX.getType() == MapObjectType.WALL) {
            Vector3d origin = new Vector3d(horizontal > 0 ? (currentSquare.getIndexX()+1)*GameConfig.SQUARE_SIZE : currentSquare.getIndexX()*GameConfig.SQUARE_SIZE, middleZ - GameConfig.SQUARE_SIZE/2, 1);
            Vector3d line = new Vector3d(0,1,0);
            if (calcIntersectionWithLine(x, z, origin, line)) {
                change += 1;
            }
        }
        if (squareZ.getType() == MapObjectType.WALL) {
            Vector3d origin = new Vector3d(middleX - GameConfig.SQUARE_SIZE/2, vertical > 0 ? (currentSquare.getIndexZ()+1)*GameConfig.SQUARE_SIZE : currentSquare.getIndexZ()*GameConfig.SQUARE_SIZE, 1);
            Vector3d line = new Vector3d(1,0,0);
            if (calcIntersectionWithLine(x, z, origin, line)) {
                change += 2;
            }
        }
        if (squareDiagonal.getType() == MapObjectType.WALL && change == 0) {
            double diagX = horizontal > 0 ? (currentSquare.getIndexX()+1)*GameConfig.SQUARE_SIZE : currentSquare.getIndexX()*GameConfig.SQUARE_SIZE;
            double diagZ =  vertical > 0 ? (currentSquare.getIndexZ()+1)*GameConfig.SQUARE_SIZE : currentSquare.getIndexZ()*GameConfig.SQUARE_SIZE;
            double dist = Math.sqrt((diagX-x)*(diagX-x) + (diagZ-z)*(diagZ-z));
            if (dist <= this.radius)
                change = 3;
        }
        return change;
    }

    public boolean calcIntersectionWithLine(double xNew, double zNew, Vector3d origin, Vector3d direction) {
        Vector3d line = origin.cross(direction);
        double dist = Math.abs(line.x*xNew + line.y*zNew + line.z) / Math.sqrt(line.x*line.x + line.y*line.y);
        return dist <= this.radius;
    }

    public void setQuaternion(double qX, double qY, double qZ, double qW) {
        quat.x = qX;
        quat.y = qY;
        quat.z = qZ;
        quat.w = qW;
    }

    private int calcMapIndexOfCoordinate(double a){
        return (int)(a / GameConfig.SQUARE_SIZE);
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

    //JUMPING
    public void jump() {
        if (!isJumping) {
            if (currentCalories >= 100) {
                this.velocityY = GameConfig.JUMP_STRENGTH;
                this.isJumping = true;
                currentCalories = currentCalories - 100;
            }
        }
    }

    public void doubleJump() {
        if (isJumping) {
            if (currentCalories >= 100) {
                this.velocityY += GameConfig.DOUBLEJUMP_STRENGTH;
                currentCalories = currentCalories - 100;
            }    
        }
    }

    public void updateJumpPosition(double deltaTime) {
        if (isJumping) {
            this.velocityY += GameConfig.GRAVITY * deltaTime;
            this.posY += this.velocityY * deltaTime;

            if (this.posY <= GameConfig.SNACKMAN_GROUND_LEVEL) {
                this.posY = GameConfig.SNACKMAN_GROUND_LEVEL;
                this.isJumping = false;
                this.velocityY = 0;
            }
        }
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

