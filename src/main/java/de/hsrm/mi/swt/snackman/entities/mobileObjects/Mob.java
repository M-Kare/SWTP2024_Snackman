package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;

/**
 * A mobile object with the ability to move its position
 */
public abstract class Mob {
    protected long id;
    private Vector3d position;
    private double radius;
    private Quaterniond quat;
    private double speed;

    @JsonIgnore
    private GameMap gameMap;

    private Vector3d spawn;
    private  static long idCounter = 0;

    /**
     * Base constructor for Map with spawn-location at center of Map
     *
     * @param gameMap GameMap
     * @param speed      speed of the mob
     * @param radius     size of the mob
     */
    public Mob(GameMap gameMap, double speed, double radius) {
        this.gameMap = gameMap;
        this.speed = speed;
        this.radius = radius;
        spawn = new Vector3d((gameMap.getGameMapSquares()[0].length / 2.0) * GameConfig.SQUARE_SIZE, GameConfig.SNACKMAN_GROUND_LEVEL,
                (gameMap.getGameMapSquares()[0].length / 2.0) * GameConfig.SQUARE_SIZE);
        position = new Vector3d(spawn);
        quat = new Quaterniond();
        setCurrentSquareWithIndex(position.x, position.z);
        setPositionWithIndexXZ(position.x, position.z);
        id = generateId();
    }

    public Mob(GameMap gameMap) {
        this.gameMap = gameMap;
        position = new Vector3d();
        quat = new Quaterniond();
    }

    /**
     * Constructor for Mob with custom spawn point
     *
     * @param gameMap MapService of the map the mob is located on
     * @param speed      speed of the mob
     * @param radius     size of the mob
     * @param posX       x-spawn-position
     * @param posY       y-spawn-positon
     * @param posZ       z-spawn-position
     */
    public Mob(GameMap gameMap, double speed, double radius, double posX, double posY, double posZ) {
        this.speed = speed;
        this.radius = radius;
        this.gameMap = gameMap;

        spawn = new Vector3d(posX, posY, posZ);
        position = new Vector3d(spawn);
        setCurrentSquareWithIndex(position.x, position.z);
        quat = new Quaterniond();
        setPositionWithIndexXZ(position.x, position.z);
        id = generateId();
    }

    protected synchronized static long generateId() {
        return idCounter++;
    }

    public double getPosX() {
        return position.x;
    }

    public void setPosX(double posX) {
        position.x = posX;
    }

    public double getPosY() {
        return position.y;
    }

    public void setPosY(double posY) {
        this.position.y = posY;
    }

    public double getPosZ() {
        return position.z;
    }

    public void setPosZ(double posZ) {
        this.position.z = posZ;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setSpawn(Vector3d spawn) {
        this.spawn = spawn;
    }

    public double getSpeed(){
        return speed;
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    public Quaterniond getRotationQuaternion(){
        return this.quat;
    }
    /**
     * Calculates the square-indices to set the currentSquare
     *
     * @param x x-position
     * @param z z-position
     */
    public void setCurrentSquareWithIndex(double x, double z) {
        setPositionWithIndexXZ(calcMapIndexOfCoordinate(x), calcMapIndexOfCoordinate(z));
    }

    public Square getCurrentSquare() {
        return this.gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(this.position.x), calcMapIndexOfCoordinate(this.position.z));
    }

    public void setPositionWithIndexXZ(double x, double z){
        this.position.x = x;
        this.position.z = z;
    }

    /**
     * Teleports player to given coordinates
     *
     * @param posX x-position
     * @param posY y-position
     * @param posZ z-position
     */
    public void move(double posX, double posY, double posZ) {
        this.setPosX(posX);
        this.setPosY(posY);
        this.setPosZ(posZ);
        this.setPositionWithIndexXZ(posX,posZ);
    }

    /**
     * Moves the player based on inputs and passed time since last update (delta). Forward is relative to the rotation of the player and handled by a quaternion.
     * Result:
     * 0 = no blocked movement direction
     * 1 = blocked x movement direction
     * 2 = blocked z movement direction
     * 3 = blocked x and z movement directions
     *
     * @param f     input forward pressed?
     * @param b     input backward pressed?
     * @param l     input left pressed?
     * @param r     input right pressed?
     * @param delta time since last update
     */
    public void move(boolean f, boolean b, boolean l, boolean r, double delta) {
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
        if (!(move.x == 0 && move.z == 0))
            move.normalize();
        move.x = move.x * delta * speed;
        move.z = move.z * delta * speed;
        double xNew = position.x + move.x;
        double zNew = position.z + move.z;
        try {
            result = checkWallCollision(xNew, zNew);
        } catch (IndexOutOfBoundsException e) {
            respawn();
            return;
        }
        switch (result) {
            case 0:
                position.x += move.x;
                position.z += move.z;
                break;
            case 1:
                position.z += move.z;
                break;
            case 2:
                position.x += move.x;
                break;
            case 3:
                return;
            default:
                break;
        }
        setPositionWithIndexXZ(position.x, position.z);
    }

    public Quaterniond getQuat() {
        return quat;
    }

    /**
     * respawns the mob at his spawn-location
     */
    public void respawn() {
        this.position.x = spawn.x;
        this.position.z = spawn.z;
        setCurrentSquareWithIndex(position.x, position.z);

    /*
    TODO unterschied zwischen snackman und geistern beachten in konstructor ändern!!
    für geister

    this.position.x = (mapService.getGameMap().getGameMap().length / 2) * GameConfig.SQUARE_SIZE;
        this.position.z = (mapService.getGameMap().getGameMap()[0].length / 2) * GameConfig.SQUARE_SIZE;
        setPositionWithIndexXZ(position.x, position.z);

     */

    }

    /**
     * firstly determines which walls are close and need to be checked for collision
     * secondly checks for collision with the walls (checks if target location is a wall and if player circle overlaps any walls)
     *
     * @param x target x-position
     * @param z target z-position
     * @returns the type of collision represented as an int
     * 0 = no collision
     * 1 = horizontal collision
     * 2 = vertical collision
     * 3 = both / diagonal collision / corner
     */
    public int checkWallCollision(double x, double z) throws IndexOutOfBoundsException {
        if (gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(x), calcMapIndexOfCoordinate(z)).getType() == MapObjectType.WALL) {
            return 3;
        }

        int collisionCase = 0;
        Square currentSquare = this.gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(x), calcMapIndexOfCoordinate(z));

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

    /**
     * calculates whether the player-cirlce intersects with a line
     *
     * @param xNew      target x-position
     * @param zNew      target z-position
     * @param origin
     * @param direction
     * @returns
     */
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

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public int calcMapIndexOfCoordinate(double a) {
        return (int) (a / GameConfig.SQUARE_SIZE);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Vector3d getSpawn() {
        return spawn;
    }

    public Vector3d getPosition() {
        return position;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Mob{" +
                "id=" + id +
                ", position=" + position +
                ", radius=" + radius +
                ", quat=" + quat +
                ", speed=" + speed +
                ", gameMap=" + gameMap +
                ", spawn=" + spawn +
                '}';
    }
}
