package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import org.joml.Quaterniond;
import org.joml.Vector3d;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.services.MapService;

public abstract class Mob {
    //private Square square;

    // public void setSquare(Square square){
    //     this.square = square;
    // }

    // public Square getSquare(){
    //     return this.square;
    // }

        
    private double posX;
    private double posY;
    private double posZ;
    private double radius;
    private Quaterniond quat;
    private Square currentSquare;

    private MapService mapService;
    private GameMap gameMap;

    public Mob(MapService mapService){
        super();

        this.mapService = mapService;
        gameMap = this.mapService.getGameMap();
        posY = GameConfig.SNACKMAN_GROUND_LEVEL;
        posX = (gameMap.getGameMap().length/2)*GameConfig.SQUARE_SIZE;
        posZ = (gameMap.getGameMap()[0].length/2)*GameConfig.SQUARE_SIZE;
        radius = GameConfig.SNACKMAN_RADIUS;
        quat = new Quaterniond();
        setCurrentSquareWithIndex(posX, posZ);
    }

    public Mob(MapService mapService, double posX, double posY, double posZ){
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
        try {
            currentSquare = gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(x), calcMapIndexOfCoordinate(z));
        } catch (IndexOutOfBoundsException e) {
            respawn();
        }
    }

    
    public void move(double posX, double posY, double posZ) {
        this.setPosX(posX);
        this.setPosY(posY);
        this.setPosZ(posZ);
        this.setCurrentSquareWithIndex(this.posX, this.posZ);
    }

    public void move(boolean f, boolean b, boolean l, boolean r, double delta) {
        System.out.println(currentSquare.getIndexX() + "  |  " + currentSquare.getIndexZ());
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
                //TODO: Wenn jemand eine einfache Methode hat den Spieler in die richige Richtung zu nudgen, gerne einfügen!
                break;
        }
        setCurrentSquareWithIndex(posX, posZ);
    }

    //TODO: SnackMan spawn in MapService benötigt
    public void respawn(){
        this.posX = (gameMap.getGameMap().length/2)*GameConfig.SQUARE_SIZE;
        this.posZ = (gameMap.getGameMap()[0].length/2)*GameConfig.SQUARE_SIZE;
    }

    public int checkWallCollision(double x, double z) {
        int change = 0;
        double middleX = currentSquare.getIndexX()*GameConfig.SQUARE_SIZE + GameConfig.SQUARE_SIZE/2;
        double middleZ = currentSquare.getIndexZ()*GameConfig.SQUARE_SIZE + GameConfig.SQUARE_SIZE/2;
        int horizontal = (x - middleX <= 0) ? -1 : 1;
        int vertical = (z - middleZ <= 0) ? -1 : 1;
        Square squareX = gameMap.getSquareAtIndexXZ(currentSquare.getIndexX()+horizontal, currentSquare.getIndexZ());
        Square squareZ = gameMap.getSquareAtIndexXZ(currentSquare.getIndexX(), currentSquare.getIndexZ()+vertical);
        Square squareDiagonal = gameMap.getSquareAtIndexXZ(currentSquare.getIndexX()+horizontal, currentSquare.getIndexZ()+vertical);
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

    public int calcMapIndexOfCoordinate(double a){
        return (int)(a / GameConfig.SQUARE_SIZE);
    }
}
