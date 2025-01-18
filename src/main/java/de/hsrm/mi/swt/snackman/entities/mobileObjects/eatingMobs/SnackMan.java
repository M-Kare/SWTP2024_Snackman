package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.entities.mechanics.SprintHandler;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.ScriptGhost;

public class SnackMan extends EatingMob {
    private final Logger log = LoggerFactory.getLogger(SnackMan.class);
    private final int GAME_FINISH_BECAUSE_OF_TOO_FEW_CKAL = -1;
    private boolean isJumping = false;
    private double velocityY = 0.0;
    private boolean isSprinting = false;
    private SprintHandler sprintHandler = new SprintHandler();
    private boolean hasDoubleJumped = false;

    public SnackMan(GameMap gameMap, Square currentSquare, double posX, double posY, double posZ) {
        this(gameMap, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS, posX, posY, posZ);

        currentSquare.addMob(this);
    }

    // only for tests??
    public SnackMan(GameMap gameMap, double speed, double radius) {
        super(gameMap, speed, radius);
    }

    public SnackMan(GameMap gameMap, double speed, double radius, double posX, double posY, double posZ) {
        super(gameMap, speed, radius, posX, posY, posZ);
    }

    public void isScaredFromGhost() {
        // Calorsies reduced by 300 if Ghost hit
        if (super.getKcal() > GameConfig.GHOST_DAMAGE) {
            setKcal(getKcal() - GameConfig.GHOST_DAMAGE);
        } else super.setKcal(GAME_FINISH_BECAUSE_OF_TOO_FEW_CKAL);
    }

        public void jump() {
        if (!isJumping && getKcal() >= 100) {
                this.velocityY = GameConfig.JUMP_STRENGTH;
                this.isJumping = true;
                this.hasDoubleJumped = false;
                subtractCaloriesSingleJump();
            }

    }

    public void doubleJump() {
        if (isJumping && getKcal() >= 100) {
            this.velocityY += GameConfig.DOUBLEJUMP_STRENGTH;
            subtractCaloriesDoubleJump();
            this.hasDoubleJumped = true;
        }
    }

    public void updateJumpPosition(double deltaTime) {
        if (isJumping) {
            this.velocityY += GameConfig.GRAVITY * deltaTime;
            this.setPosY(this.getPosY() + this.velocityY * deltaTime);

            if (this.getPosY() <= GameConfig.SQUARE_HEIGHT && squareUnderneathIsWall()) {
                int wallAlignment = checkWallAlignment();
                int wallSection = getWallSection();

                switch (wallAlignment) {
                    case 0:
                        pushback();
                        break;
                    case 1:
                        if (wallSection == 1 || wallSection == 2) {
                            push_forward();
                        } else {
                            push_backward();
                        }
                        break;
                    case 2:
                        if (wallSection == 1 || wallSection == 3) {
                            push_left();
                        } else {
                            push_right();
                        }
                        break;
                    case 3:
                        if (wallSection == 1 || wallSection == 2) {
                            push_forward();
                        } else if (wallSection == 4) {
                            push_right();
                        } else {
                            pushback();
                        }
                        break;
                    case 4:
                        if (wallSection == 3 || wallSection == 4) {
                            push_backward();
                        } else if (wallSection == 2) {
                            push_right();
                        } else {
                            pushback();
                        }
                        break;
                    case 5:
                        if (wallSection == 1 || wallSection == 3) {
                            push_left();
                        } else if (wallSection == 4) {
                            push_backward();
                        } else {
                            pushback();
                        }
                        break;
                    case 6:
                        if (wallSection == 1 || wallSection == 2) {
                            push_forward();
                        } else if (wallSection == 3) {
                            push_left();
                        } else {
                            pushback();
                        }
                        break;
                    case 7:
                        if (wallSection == 1 || wallSection == 2) {
                            push_forward();
                        } else {
                            pushback();
                        }
                        break;
                    case 8:
                        if (wallSection == 2 || wallSection == 4) {
                            push_right();
                        } else {
                            pushback();
                        }
                        break;
                    case 9:
                        if (wallSection == 3 || wallSection == 4) {
                            push_backward();
                        } else {
                            pushback();
                        }
                        break;
                    case 10:
                        if (wallSection == 1 || wallSection == 3) {
                            push_left();
                        } else {
                            pushback();
                        }
                        break;
                    case 11:
                        if (wallSection == 1 || wallSection == 2) {
                            push_forward();
                        } else if (wallSection == 3 ) {
                            push_left();
                        } else {
                            push_right();
                        }
                        break;
                    case 12:
                        if (wallSection == 1) {
                            push_forward();
                        } else if (wallSection == 2 || wallSection == 4 ) {
                            push_right();
                        } else {
                            push_backward();
                        }
                        break;
                    case 13:
                        if (wallSection == 1) {
                            push_left();
                        } else if (wallSection == 2) {
                            push_right();
                        } else {
                            push_backward();
                        }
                        break;
                    case 14:
                        if (wallSection == 1 || wallSection == 3) {
                            push_left();
                        } else if (wallSection == 2) {
                            push_forward();
                        } else {
                            push_backward();
                        }
                        break;
                }
            }

            if (this.getPosY() <= GameConfig.SNACKMAN_GROUND_LEVEL) {
                this.setPosY(GameConfig.SNACKMAN_GROUND_LEVEL);
                this.isJumping = false;
                this.velocityY = 0;
                this.hasDoubleJumped = false;
            }
        }
    }

    private void subtractCaloriesSingleJump() {
        setKcal(getKcal() - 100);
    }

    private void subtractCaloriesDoubleJump() {
        if (!hasDoubleJumped) {
            setKcal(getKcal() - 100);
        }
    }

    public int getCurrentCalories() {
        return super.getKcal();
    }

    @Override
    public void move(boolean forward, boolean backward, boolean left, boolean right, double delta, GameMap gameMap) {
        if (isSprinting) {
            if (sprintHandler.canSprint()) {
                setSpeed(GameConfig.SNACKMAN_SPEED * GameConfig.SNACKMAN_SPRINT_MULTIPLIER);
            } else {
                sprintHandler.stopSprint();
                setSpeed(GameConfig.SNACKMAN_SPEED);
            }
        } else {
            sprintHandler.stopSprint();
            setSpeed(GameConfig.SNACKMAN_SPEED);
        }

        Square oldSquare = gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(super.getPosX()), calcMapIndexOfCoordinate(super.getPosZ()));
        super.move(forward, backward, left, right, delta, gameMap);
        Square newSquare = gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(super.getPosX()), calcMapIndexOfCoordinate(super.getPosZ()));

        if (!oldSquare.equals(newSquare)) {
            oldSquare.removeMob(this);
            newSquare.addMob(this);
        }

        // when snackman runs into a ghost
        for (Mob mob : newSquare.getMobs()) {
            if (mob instanceof Ghost || mob instanceof ScriptGhost) {
                this.isScaredFromGhost();
            }
        }
    }

    public int getSprintTimeLeft() {
        return sprintHandler.getSprintTimeLeft();
    }

    public boolean isInCooldown() {
        return sprintHandler.isInCooldown();
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.isSprinting = sprinting;

        if (sprinting) {
            if (sprintHandler.canSprint()) {
                sprintHandler.startSprint();
            } else {
                this.isSprinting = false;
            }
        } else {
            sprintHandler.stopSprint();
        }
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     *
     * @param square to eat the snack from
     */
    @Override
    public void consumeSnackOnSquare(Square square) {
        Snack snackOnSquare = square.getSnack();

        if (snackOnSquare.getSnackType() != SnackType.EMPTY) {
            try {
                super.gainKcal(snackOnSquare.getCalories());
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            //set snack to null after consuming it
            square.setSnack(new Snack(SnackType.EMPTY));
        }
    }

    public boolean isJumping() {
        return isJumping;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setSprintHandler(SprintHandler sprintHandler) {
        this.sprintHandler = sprintHandler;
    }
}

