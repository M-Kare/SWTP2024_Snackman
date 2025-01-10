import {useGameMapStore} from '@/stores/gameMapStore';
import {MapObjectType, type IGameMap} from '@/stores/IGameMapDTD';
import type {ISquare} from '@/stores/Square/ISquareDTD';
import {type WebGLRenderer} from 'three'
import * as THREE from 'three'
import {PointerLockControls} from 'three/addons/controls/PointerLockControls.js'
import {reactive, ref, type UnwrapNestedRefs} from "vue";

export class Player {
  private prevTime: DOMHighResTimeStamp

  // Booleans for checking movement-input
  private moveForward: boolean;
  private moveBackward: boolean;
  private moveLeft: boolean;
  private moveRight: boolean;
  private canJump: boolean;
  private sprinting: boolean;

  private radius: number;
  private speed: number;
  private sprintMultiplier: number;

  private camera: THREE.PerspectiveCamera;
  private controls: PointerLockControls;

  private movementDirection: THREE.Vector3;

  private isJumping: boolean;
  private lastJumpTime: number;
  private doubleJump: boolean;
  private spacePressed: boolean;

  private calories: number;

  private _message = ref("")

  private _sprintData = reactive({
    sprintTimeLeft: 100, // percentage (0-100)
    isSprinting: false,
    isCooldown: false,
  })

  //TODO: ersetzten durch das maze im pinia store
  private squareSize: Readonly<number>;
  private gameMap: ISquare[][];
  private currentSquare: ISquare;

  /**
   * Initialises the Player as a camera
   * @param renderer Renderer for the scene. Needed for PointerLockControls
   * @param posX x-spawn-sosition
   * @param posY y-spawn-position
   * @param posZ z-spawn-position
   * @param radius size of the player
   * @param speed speed-modifier of the player
   */
  constructor(renderer: WebGLRenderer, posX: number, posY: number, posZ: number, radius: number, speed: number, sprintMultiplier: number) {
    this.prevTime = performance.now();
    this.moveBackward = false;
    this.moveForward = false;
    this.moveLeft = false;
    this.moveRight = false;
    this.canJump = true;
    this.sprinting = false;
    this.movementDirection = new THREE.Vector3();
    this.calories = 0;

    this.isJumping = false;
    this.lastJumpTime = 0;
    this.doubleJump = false;
    this.spacePressed = false;

    const {mapContent} = useGameMapStore();
    this.squareSize = mapContent.DEFAULT_SQUARE_SIDE_LENGTH;

    const mapArray = Array.from(mapContent.gameMap.values())
    let lastSquare = mapArray[mapArray.length - 1]
    this.gameMap = this.reshapeArray(mapArray, lastSquare?.indexX! + 1, lastSquare?.indexZ! + 1) // lastSquare is of type ISquare|undefined because promise

    this.currentSquare = this.gameMap[this.calcMapIndexOfCoordinate(posX)][this.calcMapIndexOfCoordinate(posY)];

    this.radius = radius;
    this.speed = speed;
    this.sprintMultiplier = sprintMultiplier;

    this.camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 60)
    this.camera.position.set(posX, posY, posZ)
    this.controls = new PointerLockControls(this.camera, renderer.domElement)
    document.addEventListener('keydown', (event) => {
      this.onKeyDown(event)
    })
    document.addEventListener('keyup', (event) => {
      this.onKeyUp(event)
    })
    document.addEventListener('click', () => {
      this.controls.lock()
    })
  }

  private reshapeArray(arr: Array<any>, rows: number, cols: number) {
    const result = new Array(rows);
    for (let x = 0; x < rows; x++) {
      result[x] = new Array(cols);
      for (let z = 0; z < cols; z++) {
        result[x][z] = arr[x * cols + z];
      }
    }
    return result;
  }

  public calcMapIndexOfCoordinate(a: number): number {
    return Math.floor(a / this.squareSize);
  }

  public getCamera(): THREE.PerspectiveCamera {
    return this.camera;
  }

  public getControls(): PointerLockControls {
    return this.controls;
  }

  public getMovementDirection() {
    return this.movementDirection;
  }

  /**
   * unsets released keys
   * @param event keyboard-events
   */
  public onKeyUp(event: any) {
    switch (event.code) {
      case 'ArrowUp':
      case 'KeyW':
        this.moveForward = false
        this.sprinting = false;
        break

      case 'ArrowLeft':
      case 'KeyA':
        this.moveLeft = false
        break

      case 'ArrowDown':
      case 'KeyS':
        this.moveBackward = false
        break

      case 'ArrowRight':
      case 'KeyD':
        this.moveRight = false
        break

      case 'Space':
        this.spacePressed = false;
        break

      case 'ShiftLeft':
        this.sprinting = false;
        break;
    }
  }

  /**
   * sets pressed keys
   * @param event keyboard-events
   */
  public onKeyDown(event: any) {
    switch (event.code) {
      case 'ArrowUp':
      case 'KeyW':
        this.moveForward = true
        if (event.shiftKey) {
          this.sprinting = true;
        }
        break

      case 'ArrowLeft':
      case 'KeyA':
        this.moveLeft = true
        break

      case 'ArrowDown':
      case 'KeyS':
        this.moveBackward = true
        break

      case 'ArrowRight':
      case 'KeyD':
        this.moveRight = true
        break

      case 'Space':
        if (!this.spacePressed) {
          this.spacePressed = true;
          const currentTime = performance.now();
          if (!this.isJumping) {
            //Single Jump
            this.isJumping = true;
            this.lastJumpTime = currentTime;
          } else if (!this.doubleJump && (currentTime - this.lastJumpTime <= 600)) {
            //Double Jump
            this.doubleJump = true;
            this.lastJumpTime = currentTime;
          }
        }
        break;

      case 'ShiftLeft':
        this.sprinting = this.moveForward;
        if (this.moveForward) {
          this.sprinting = true;
        }
        break;
    }
  }

  /**
   * get booleans of held inputs
   * @returns Object with inputs-values
   */
  public getInput(): object {
    return {forward: this.moveForward, backward: this.moveBackward, left: this.moveLeft, right: this.moveRight}
  }

  /**
   * lerp is used to interpolate the two positions
   */
  public setPosition(pos: THREE.Vector3) {
    this.camera.position.lerp(pos, 0.5);

    if (pos.y <= 2) {
      this.isJumping = false
      this.doubleJump = false
    }
  }

  /**
   * sets the rotation of the camera from a directional vector
   * @param x x-direction
   * @param y y-direction
   * @param z z-direction
   */
  public setCameraRotation(x: number, y: number, z: number) {
    this.camera.rotation.set(x, y, z);
  }

  /**
   * code inspired by from https://github.com/mrdoob/three.js/blob/master/examples/misc_controls_pointerlock.html
   * new position is calculated based on which keys are held, rotation and how much time passed since the last update
   */
  public updatePlayer() {
    const time = performance.now()
    const delta = (time - this.prevTime) / 1000
    let result = 3;

    const currentSpeed = this._sprintData.isSprinting ? this.speed * this.sprintMultiplier : this.speed;
    const adjustedDelta = Math.min(delta, 0.016) // max. 60 FPS, otherwise it lags while sprinting

    this.movementDirection.z = Number(this.moveForward) - Number(this.moveBackward)
    this.movementDirection.x = Number(this.moveRight) - Number(this.moveLeft)

    let move = new THREE.Vector3(0, 0, 0)
    if (this.moveForward || this.moveBackward) {
      move.z -= this.movementDirection.z
    }
    if (this.moveLeft || this.moveRight) {
      move.x += this.movementDirection.x
    }

    move.applyQuaternion(this.camera.quaternion)
    move.y = 0;
    if (!(move.x == 0 && move.z == 0))
      move.normalize();
    move.x = move.x * adjustedDelta * currentSpeed;
    move.z = move.z * adjustedDelta * currentSpeed;
    const xNew = this.camera.position.x + move.x;
    const zNew = this.camera.position.z + move.z;
    try {
      result = this.checkWallCollision(xNew, zNew);
    } catch (e) {
      console.log(e)
    }
    switch (result) {
      case 0:
        this.camera.position.x += move.x;
        this.camera.position.z += move.z;
        break;
      case 1:
        this.camera.position.z += move.z;
        break;
      case 2:
        this.camera.position.x += move.x;
        break;
      case 3:
        break;
      default:
        break;
    }
    this.setCurrentSquareWithIndex(this.camera.position.x, this.camera.position.z);
    this.prevTime = time
  }

  // TODO: Mock-Mapo  durch richtige Map ersetzten (Pinia-Store)
  /**
   * calculates the index of the square that correspondes to the position and sets the new square based on these indices
   * @param x x-position
   * @param z z-position
   */
  public setCurrentSquareWithIndex(x: number, z: number) {
    this.currentSquare = this.gameMap[this.calcMapIndexOfCoordinate(x)][this.calcMapIndexOfCoordinate(z)];
  }

  /**
   * firstly determines which walls are close and need to be checked for collision
   * secondly checks for collision with the walls (checks if target location is a wall and if player circle overlaps any walls)
   * @param x target x-position
   * @param z target z-position
   * @returns the type of collision represented as an int
   * 0 = no collision
   * 1 = horizontal collision
   * 2 = vertical collision
   * 3 = both / diagonal collision / corner
   */
  public checkWallCollision(x: number, z: number): number {
    let squareLeftRight: ISquare;
    let squareTopBottom: ISquare;
    let squareDiagonal: ISquare;
    let collisionCase = 0;

    const squareCenterX = this.currentSquare.indexX * this.squareSize + this.squareSize / 2;
    const squareCenterZ = this.currentSquare.indexZ * this.squareSize + this.squareSize / 2;

    const horizontalRelativeToCenter = (x - squareCenterX <= 0) ? -1 : 1;
    const verticalRelativeToCenter = (z - squareCenterZ <= 0) ? -1 : 1;
    squareLeftRight = this.gameMap[this.currentSquare.indexX + horizontalRelativeToCenter][this.currentSquare.indexZ];
    squareTopBottom = this.gameMap[this.currentSquare.indexX][this.currentSquare.indexZ + verticalRelativeToCenter];
    squareDiagonal = this.gameMap[this.currentSquare.indexX + horizontalRelativeToCenter][this.currentSquare.indexZ + verticalRelativeToCenter];
    if (squareLeftRight.type === MapObjectType.WALL) {
      const origin = new THREE.Vector3(
        horizontalRelativeToCenter > 0 ? (this.currentSquare.indexX + 1) * this.squareSize
          : this.currentSquare.indexX * this.squareSize,
        0, 1);
      const line = new THREE.Vector3(0, 1, 0);
      if (this.calcIntersectionWithLine(x, z, origin, line)) {
        collisionCase += 1;
      }
    }

    if (squareTopBottom.type === MapObjectType.WALL) {
      const origin = new THREE.Vector3(0,
        verticalRelativeToCenter > 0 ? (this.currentSquare.indexZ + 1) * this.squareSize
          : this.currentSquare.indexZ * this.squareSize,
        1);
      const line = new THREE.Vector3(1, 0, 0);
      if (this.calcIntersectionWithLine(x, z, origin, line)) {
        collisionCase += 2;
      }
    }

    if (squareDiagonal.type === MapObjectType.WALL && collisionCase == 0) {
      const diagX = horizontalRelativeToCenter > 0 ? (this.currentSquare.indexX + 1) * this.squareSize
        : this.currentSquare.indexX * this.squareSize;
      const diagZ = verticalRelativeToCenter > 0 ? (this.currentSquare.indexZ + 1) * this.squareSize
        : this.currentSquare.indexZ * this.squareSize;
      const dist = Math.sqrt((diagX - x) * (diagX - x) + (diagZ - z) * (diagZ - z));
      if (dist <= this.radius)
        collisionCase = 3;
    }

    return collisionCase;
  }

  /**
   * calculates whether the player-cirlce intersects with a line
   * @param xNew target x-position
   * @param zNew target z-position
   * @param origin
   * @param direction
   * @returns
   */
  public calcIntersectionWithLine(xNew: number, zNew: number, origin: THREE.Vector3, direction: THREE.Vector3): boolean {
    const line = origin.cross(direction);
    const dist = Math.abs(line.x * xNew + line.y * zNew + line.z) / Math.sqrt(line.x * line.x + line.y * line.y);
    return dist <= this.radius;
  }

  public getIsJumping() {
    return this.isJumping;
  }

  public getIsDoubleJumping() {
    return this.doubleJump;
  }

  public get isSprinting(): boolean {
    return this.sprinting;
  }

  get sprintData(): UnwrapNestedRefs<{ isSprinting: boolean; sprintTimeLeft: number; isCooldown: boolean }> & {} {
    return this._sprintData;
  }

  public get message(){
    return this._message;
  }

  public getCalories(): number {
    return this.calories;
  }

  public setCalories(cal: number): void {
    this.calories = cal;
  }
}
