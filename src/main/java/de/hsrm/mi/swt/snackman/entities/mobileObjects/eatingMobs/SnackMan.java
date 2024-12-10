package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;
import de.hsrm.mi.swt.snackman.services.MapService;

@Component
public class SnackMan extends EatingMob {

    private int currentCalories;
    private double posX;
    private double posY;
    private double posZ;
    private double radius;
    private Quaterniond quat;
    private Square currentSquare;
    private double speed;

    private static final double SPRINT_MULTIPLIER = 1.5;
    private static final double MAX_SPRINT_TIME = 5.0;
    private static final double MAX_COOLDOWN_TIME = 10.0;
    private static final double RECHARGE_RATE = 0.5; // 1 second of recharge = 2 seconds Cooldown

    private double remainingSprintTime = MAX_SPRINT_TIME;
    private double cooldownTime = 0;
    private boolean isSprinting = false;

    private ScheduledExecutorService timerService;

    @Autowired
    private MapService mapService;

    public SnackMan(MapService mapService){
        super();
        currentCalories = 0;

        this.mapService = mapService;
        posY = GameConfig.SNACKMAN_GROUND_LEVEL;
        posX = (this.mapService.getGameMap().getGameMap().length/2)*GameConfig.SQUARE_SIZE;
        posZ = (this.mapService.getGameMap().getGameMap()[0].length/2)*GameConfig.SQUARE_SIZE;
        radius = GameConfig.SNACKMAN_RADIUS;
        quat = new Quaterniond();
        speed = GameConfig.SNACKMAN_SPEED;
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

    public double getSpeed() {
        return speed;
    }

    public void move(boolean f, boolean b, boolean l, boolean r, double delta) {
        //System.out.println(currentSquare.getIndexX() + "  |  " + currentSquare.getIndexZ());
        int moveDirZ = (f ? 1 : 0) - (b ? 1 : 0);
        int moveDirX = (r ? 1 : 0) - (l ? 1 : 0);

        Vector3d move = new Vector3d();

        if (isSprinting && canSprint()) {
            this.speed = GameConfig.SNACKMAN_SPEED * SPRINT_MULTIPLIER;
        } else {
            this.speed = GameConfig.SNACKMAN_SPEED;
            stopSprint();
        }

        if (f || b) {
            move.z -= moveDirZ;
        }
        if (l || r) {
            move.x += moveDirX;
        }
        if(move.x != 0 && move.z != 0){
            move.normalize();
        }
        move.x = move.x * delta * speed;
        move.z = move.z * delta * speed;    
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

    /* Sprint timer logic */

    /* Adjusts the speed based on whether sprinting is allowed and manages sprint state. */
    public void setSpeed(boolean sprinting) {
        if (sprinting && canSprint()) {
            if (!isSprinting) {
                this.isSprinting = true;
                this.speed = GameConfig.SNACKMAN_SPEED * SPRINT_MULTIPLIER;
                System.out.println("[DEBUG] Sprint gestartet. Verbleibende Sprintzeit: " + remainingSprintTime + " Sekunden.");
                startTimer();
            }
        } else {
            if (isSprinting) {
                endSprint();
            }
            this.speed = GameConfig.SNACKMAN_SPEED;
        }
    }

    /* Checks if sprinting is allowed based on cooldown and remaining sprint time. */
    private boolean canSprint() {
        if (cooldownTime > 0) {
            System.out.println("[DEBUG] Remaining Cooldown: " + cooldownTime);
            return false;
        }
        if (remainingSprintTime <= 0) {
            System.out.println("[DEBUG] No sprint time left");
            return false;
        }
        return true;
    }

    /* Ends the sprint, calculates the cooldown duration, and resets speed. */
    private void endSprint() {
        this.isSprinting = false;
        double usedTime = MAX_SPRINT_TIME - remainingSprintTime;
        this.cooldownTime = Math.min(MAX_COOLDOWN_TIME, usedTime * 2);
        System.out.println("[DEBUG] Sprint beendet. Cooldown gestartet: " + cooldownTime + " Sekunden.");
        startTimer();
    }

    /* Starts a timer for managing sprint and cooldown logic if not already running. */
    private void startTimer() {
        if (timerService == null || timerService.isShutdown()) {
            // Create and schedule a recurring task to handle timers
            timerService = Executors.newSingleThreadScheduledExecutor();
            timerService.scheduleAtFixedRate(this::updateTimersTask, 0, 100, TimeUnit.MILLISECONDS);
        }
    }

    /* Stops the timer when it is no longer needed. */
    private void stopTimer() {
        if (timerService != null && !timerService.isShutdown()) {
            timerService.shutdown();
        }
    }

    /* Handles updates to sprint time, cooldown, and recharging in the timer task. */
    private void updateTimersTask() {
        double delta = 0.1; // Time increment in seconds (100ms)
    
        if (isSprinting) {
            // Decrease remaining sprint time during active sprint
            remainingSprintTime -= delta;
            if (remainingSprintTime <= 0) {
                remainingSprintTime = 0;
                endSprint();
            }
        } else if (cooldownTime > 0) {
            // Decrease cooldown time if active
            cooldownTime -= delta;
            if (cooldownTime <= 0) {
                cooldownTime = 0;
                remainingSprintTime = MAX_SPRINT_TIME;
                System.out.println("[DEBUG] Cooldown expired. Sprint time resets in " + MAX_SPRINT_TIME);
                stopTimer();
            }
        } else if (remainingSprintTime < MAX_SPRINT_TIME) {
            // Recharge sprint time if not sprinting and cooldown is complete
            remainingSprintTime += delta * RECHARGE_RATE;
            if (remainingSprintTime >= MAX_SPRINT_TIME) {
                remainingSprintTime = MAX_SPRINT_TIME;
                System.out.println("[DEBUG] Sprint time fully charged");
                stopTimer();
            } else {
                System.out.println("[DEBUG] Sprint time up in: " + remainingSprintTime);
            }
        }
    
        System.out.println("[DEBUG] Sprint time: " + remainingSprintTime + " Cooldown: " + cooldownTime);
    }

    /* Starts the sprint if allowed and initializes the timer. */
    public void startSprint() {
        if (canSprint()) {
            if (!isSprinting) {
                this.isSprinting = true;
                this.speed = GameConfig.SNACKMAN_SPEED * SPRINT_MULTIPLIER;
                System.out.println("[DEBUG] Sprint started. Remaining sprint time: " + remainingSprintTime);
                startTimer();
            }
        } else {
            System.out.println("[DEBUG] Sprint could not be started. Cooldown active or sprint time exhausted.");
        }
    }
    
    /* Stops the sprint and resets the speed to normal. */
    public void stopSprint() {
        if (isSprinting) {
            this.isSprinting = false;
            this.speed = GameConfig.SNACKMAN_SPEED;
            System.out.println("[DEBUG] Sprint beendet.");
        }
    }

    /* Handles updates to sprint time, cooldown, and recharging. */
    public void updateTimers(double delta) {
        // Update sprint time if currently sprinting
        if (isSprinting) {
            remainingSprintTime -= delta;
            if (remainingSprintTime <= 0) {
                // End sprint when time runs out
                remainingSprintTime = 0;
                endSprint();
            }
        } else if (cooldownTime > 0) {
            // Update cooldown time if active
            cooldownTime -= delta;
            if (cooldownTime <= 0) {
                // Reset sprint time when cooldown finishes
                cooldownTime = 0;
                remainingSprintTime = MAX_SPRINT_TIME;
                System.out.println("[DEBUG] Cooldown expired. Sprint time fully recharged.");
            }
        } else if (remainingSprintTime < MAX_SPRINT_TIME) {
            // Recharge sprint time if not sprinting and no cooldown
            remainingSprintTime += delta * RECHARGE_RATE;
            if (remainingSprintTime >= MAX_SPRINT_TIME) {
                remainingSprintTime = MAX_SPRINT_TIME;
                System.out.println("[DEBUG] Sprint time fully recharged.");
            }
        }

        System.out.println("[DEBUG] Sprint time: " + remainingSprintTime + ", Cooldown: " + cooldownTime);
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
