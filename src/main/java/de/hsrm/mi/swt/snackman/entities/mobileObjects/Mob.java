package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.services.MapService;

public abstract class Mob {
    // private Square square;

    // public void setSquare(Square square){
    // this.square = square;
    // }

    // public Square getSquare(){
    // return this.square;
    // }

    private double posX;
    private double posY;
    private double posZ;
    private double radius;
    private Quaterniond quat;
    private Square currentSquare;

    private MapService mapService;
    private GameMap gameMap;

    public Mob(MapService mapService) {
        super();

        this.mapService = mapService;
        gameMap = this.mapService.getGameMap();
        posY = GameConfig.SNACKMAN_GROUND_LEVEL;
        posX = (gameMap.getGameMap().length / 2) * GameConfig.SQUARE_SIZE;
        posZ = (gameMap.getGameMap()[0].length / 2) * GameConfig.SQUARE_SIZE;
        radius = GameConfig.SNACKMAN_RADIUS;
        quat = new Quaterniond();
        setCurrentSquareWithIndex(posX, posZ);
    }

    public Mob(MapService mapService, double posX, double posY, double posZ) {
        this(mapService);

        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        setCurrentSquareWithIndex(posX, posZ);
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
            currentSquare = gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(x), calcMapIndexOfCoordinate(z));
    }

    public void move(double posX, double posY, double posZ) {
        this.setPosX(posX);
        this.setPosY(posY);
        this.setPosZ(posZ);
        this.setCurrentSquareWithIndex(this.posX, this.posZ);
    }

    /**
     * Result:
     * 0 = no blocked movement direction
     * 1 = blocked x movement direction
     * 2 = blocked z movement direction
     * 3 = blocked x and z movement directions
     * @param f input forward pressed?
     * @param b input backward pressed?
     * @param l input left pressed?
     * @param r input right pressed?
     * @param delta 
     */
    public void move(boolean f, boolean b, boolean l, boolean r, double delta) {
        System.out.println(currentSquare.getIndexX() + "  |  " + currentSquare.getIndexZ());
        int result = 3;
        int moveDirZ = (f ? 1 : 0) - (b ? 1 : 0);
        int moveDirX = (r ? 1 : 0) - (l ? 1 : 0);

        Vector3d move = new Vector3d();

        if (f || b) {
            move.z -= moveDirZ;
        }
        if (l || r) {
            move.x += moveDirX;
        }
        move.rotate(quat);
        move.y = 0;
        move.normalize();
        move.x = move.x * delta * GameConfig.SNACKMAN_SPEED;
        move.z = move.z * delta * GameConfig.SNACKMAN_SPEED;
        double xNew = posX + move.x;
        double zNew = posZ + move.z;
        try{
            result = checkWallCollision(xNew, zNew);
        } catch (IndexOutOfBoundsException e){
            respawn();
            return;
        }
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
                break;
            default:
                break;
        }
        posX = move.x;
        posZ = move.z;  
        setCurrentSquareWithIndex(posX, posZ);
    }

    // TODO: SnackMan spawn in MapService benötigt
    public void respawn() {
        this.posX = (gameMap.getGameMap().length / 2) * GameConfig.SQUARE_SIZE;
        this.posZ = (gameMap.getGameMap()[0].length / 2) * GameConfig.SQUARE_SIZE;
    }

    public int checkWallCollision(double x, double z) throws IndexOutOfBoundsException {
        int collisionCase = 0;

        double squareCenterX = currentSquare.getIndexX() * GameConfig.SQUARE_SIZE + GameConfig.SQUARE_SIZE / 2;
        double squareCenterZ = currentSquare.getIndexZ() * GameConfig.SQUARE_SIZE + GameConfig.SQUARE_SIZE / 2;

        int horizontalRelativeToCenter = (x - squareCenterX <= 0) ? -1 : 1;
        int verticalRelativeToCenter = (z - squareCenterZ <= 0) ? -1 : 1;

        Square squareLeftRight = gameMap.getSquareAtIndexXZ(currentSquare.getIndexX() + horizontalRelativeToCenter,
                currentSquare.getIndexZ());
        Square squareTopBottom = gameMap.getSquareAtIndexXZ(currentSquare.getIndexX(),
                currentSquare.getIndexZ() + verticalRelativeToCenter);
        Square squareDiagonal = gameMap.getSquareAtIndexXZ(currentSquare.getIndexX() + horizontalRelativeToCenter,
                currentSquare.getIndexZ() + verticalRelativeToCenter);

        if (squareLeftRight.getType() == MapObjectType.WALL) {
            Vector3d origin = new Vector3d(
                    horizontalRelativeToCenter > 0 ? (currentSquare.getIndexX() + 1) * GameConfig.SQUARE_SIZE
                            : currentSquare.getIndexX() * GameConfig.SQUARE_SIZE,
                    0, 1);
            Vector3d line = new Vector3d(0, 1, 0);
            if (calcIntersectionWithLine(x, z, origin, line)) {
                collisionCase += 1;
            }
        }

        if (squareTopBottom.getType() == MapObjectType.WALL) {
            Vector3d origin = new Vector3d(0,
                    verticalRelativeToCenter > 0 ? (currentSquare.getIndexZ() + 1) * GameConfig.SQUARE_SIZE
                            : currentSquare.getIndexZ() * GameConfig.SQUARE_SIZE,
                    1);
            Vector3d line = new Vector3d(1, 0, 0);
            if (calcIntersectionWithLine(x, z, origin, line)) {
                collisionCase += 2;
            }
        }

        if (squareDiagonal.getType() == MapObjectType.WALL && collisionCase == 0) {
            double diagX = horizontalRelativeToCenter > 0 ? (currentSquare.getIndexX() + 1) * GameConfig.SQUARE_SIZE
                    : currentSquare.getIndexX() * GameConfig.SQUARE_SIZE;
            double diagZ = verticalRelativeToCenter > 0 ? (currentSquare.getIndexZ() + 1) * GameConfig.SQUARE_SIZE
                    : currentSquare.getIndexZ() * GameConfig.SQUARE_SIZE;
            double dist = Math.sqrt((diagX - x) * (diagX - x) + (diagZ - z) * (diagZ - z));
            if (dist <= this.radius)
                collisionCase = 3;
        }

        return collisionCase;
    }

    public boolean calcIntersectionWithLine(double xNew, double zNew, Vector3d origin, Vector3d direction) {
        Vector3d line = origin.cross(direction);
        double dist = Math.abs(line.x * xNew + line.y * zNew + line.z) / Math.sqrt(line.x * line.x + line.y * line.y);
        return dist <= this.radius;
    }

    public void setQuaternion(double qX, double qY, double qZ, double qW) {
        quat.x = qX;
        quat.y = qY;
        quat.z = qZ;
        quat.w = qW;
    }

    public int calcMapIndexOfCoordinate(double a) {
        return (int) (a / GameConfig.SQUARE_SIZE);
    }
}
