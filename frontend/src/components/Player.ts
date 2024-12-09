import type { WebGLRenderer } from 'three'
import * as THREE from 'three'
import { PointerLockControls } from 'three/addons/controls/PointerLockControls.js'

export class Player {
  private prevTime: DOMHighResTimeStamp

  // Booleans for checking movement-input
  private moveForward: boolean;
  private moveBackward: boolean;
  private moveLeft: boolean;
  private moveRight: boolean;
  private canJump: boolean;

  private radius: number;
  private speed: number;

  private camera: THREE.PerspectiveCamera;
  private controls: PointerLockControls;

  private movementDirection: THREE.Vector3;

  //TODO: ersetzten durch das maze im pinia store
  private HARDCODED_SQUARE_SIZE = 4;
  private geilesHardgecodetesMaze: string[][] = [
    ['#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'],
    ['#', ' ', ' ', ' ', '#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#'],
    ['#', '#', '#', ' ', '#', ' ', '#', '#', '#', '#', '#', '#', '#', ' ', '#'],
    ['#', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#', ' ', ' ', ' ', ' ', ' ', '#'],
    ['#', ' ', '#', '#', '#', '#', '#', ' ', '#', '#', '#', '#', '#', ' ', '#'],
    ['#', ' ', '#', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#', ' ', '#'],
    ['#', ' ', '#', '#', '#', ' ', ' ', ' ', ' ', ' ', '#', ' ', '#', '#', '#'],
    ['#', ' ', ' ', ' ', '#', ' ', ' ', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#'],
    ['#', '#', '#', ' ', '#', '#', ' ', ' ', ' ', ' ', '#', '#', '#', ' ', '#'],
    ['#', ' ', ' ', ' ', '#', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '#'],
    ['#', ' ', '#', '#', '#', ' ', '#', '#', '#', '#', '#', '#', '#', ' ', '#'],
    ['#', ' ', '#', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#'],
    ['#', ' ', '#', ' ', '#', '#', '#', ' ', '#', ' ', '#', ' ', '#', '#', '#'],
    ['#', ' ', ' ', ' ', '#', ' ', ' ', ' ', '#', ' ', ' ', ' ', ' ', ' ', '#'],
    ['#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#']
  ];
  private currentSquare: { x: number, z: number };

  /**
   * Initialises the Player as a camera
   * @param renderer Renderer for the scene. Needed for PointerLockControls
   * @param posX x-spawn-sosition
   * @param posY y-spawn-position
   * @param posZ z-spawn-position
   * @param radius size of the player 
   * @param speed speed-modifier of the player
   */
  constructor(renderer: WebGLRenderer, posX: number, posY: number, posZ: number, radius: number, speed: number) {
    this.prevTime = performance.now();
    this.moveBackward = false;
    this.moveForward = false;
    this.moveLeft = false;
    this.moveRight = false;
    this.canJump = true;
    this.movementDirection = new THREE.Vector3();

    this.currentSquare = { x: this.calcMapIndexOfCoordinate(posX), z: this.calcMapIndexOfCoordinate(posY) }

    this.radius = radius;
    this.speed = speed;

    this.camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 100)
    this.camera.position.set(posX, posY, posZ)
    this.controls = new PointerLockControls(this.camera, renderer.domElement)
    document.addEventListener('keydown', (event) => { this.onKeyDown(event) })
    document.addEventListener('keyup', (event) => { this.onKeyUp(event) })
    document.addEventListener('click', () => { this.controls.lock() })
  }

  public calcMapIndexOfCoordinate(a: number): number {
    return Math.floor(a / this.HARDCODED_SQUARE_SIZE);
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
    }
  }
  /**
   * get booleans of held inputs
   * @returns Object with inputs-values
   */
  public getInput(): object {
    return { forward: this.moveForward, backward: this.moveBackward, left: this.moveLeft, right: this.moveRight }
  }

  /**
   * lerp is used to interpolate the two positions
   */
  public setPosition(x: number, y: number, z: number) {
    this.camera.position.lerp(new THREE.Vector3(x, y, z), 0.5);
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
    move.x = move.x * delta * this.speed
    move.z = move.z * delta * this.speed
    const xNew = this.camera.position.x + move.x;
    const zNew = this.camera.position.z + move.z;
    const result = this.checkWallCollision(xNew, zNew);
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
    console.log(result)
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
    this.currentSquare = { x: this.calcMapIndexOfCoordinate(x), z: this.calcMapIndexOfCoordinate(z) };
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
    const wall = '#'
    const floor = ' '
    let squareLeftRight: { indexX: number, indexZ: number, type: string }
    let squareTopBottom: { indexX: number, indexZ: number, type: string }
    let squareDiagonal: { indexX: number, indexZ: number, type: string }
    let collisionCase = 0;

    const squareCenterX = this.currentSquare.x * this.HARDCODED_SQUARE_SIZE + this.HARDCODED_SQUARE_SIZE / 2;
    const squareCenterZ = this.currentSquare.z * this.HARDCODED_SQUARE_SIZE + this.HARDCODED_SQUARE_SIZE / 2;

    const horizontalRelativeToCenter = (x - squareCenterX <= 0) ? -1 : 1;
    const verticalRelativeToCenter = (z - squareCenterZ <= 0) ? -1 : 1;
    console.log(this.currentSquare.x + horizontalRelativeToCenter)
    squareLeftRight = { indexX: this.currentSquare.x + horizontalRelativeToCenter, indexZ: this.currentSquare.z, type: this.geilesHardgecodetesMaze[this.currentSquare.x + horizontalRelativeToCenter][this.currentSquare.z] };
    squareTopBottom = { indexX: this.currentSquare.x, indexZ: this.currentSquare.z + verticalRelativeToCenter, type: this.geilesHardgecodetesMaze[this.currentSquare.x][this.currentSquare.z + verticalRelativeToCenter] };
    squareDiagonal = { indexX: this.currentSquare.x + horizontalRelativeToCenter, indexZ: this.currentSquare.z + verticalRelativeToCenter, type: this.geilesHardgecodetesMaze[this.currentSquare.x + horizontalRelativeToCenter][this.currentSquare.z + verticalRelativeToCenter] };
    console.log(squareLeftRight)
    if (squareLeftRight.type == wall) {
      const origin = new THREE.Vector3(
        horizontalRelativeToCenter > 0 ? (this.currentSquare.x + 1) * this.HARDCODED_SQUARE_SIZE
          : this.currentSquare.x * this.HARDCODED_SQUARE_SIZE,
        0, 1);
      const line = new THREE.Vector3(0, 1, 0);
      if (this.calcIntersectionWithLine(x, z, origin, line)) {
        collisionCase += 1;
      }
    }

    if (squareTopBottom.type == wall) {
      const origin = new THREE.Vector3(0,
        verticalRelativeToCenter > 0 ? (this.currentSquare.z + 1) * this.HARDCODED_SQUARE_SIZE
          : this.currentSquare.z * this.HARDCODED_SQUARE_SIZE,
        1);
      const line = new THREE.Vector3(1, 0, 0);
      if (this.calcIntersectionWithLine(x, z, origin, line)) {
        collisionCase += 2;
      }
    }

    if (squareDiagonal.type == wall && collisionCase == 0) {
      const diagX = horizontalRelativeToCenter > 0 ? (this.currentSquare.x + 1) * this.HARDCODED_SQUARE_SIZE
        : this.currentSquare.x * this.HARDCODED_SQUARE_SIZE;
      const diagZ = verticalRelativeToCenter > 0 ? (this.currentSquare.z + 1) * this.HARDCODED_SQUARE_SIZE
        : this.currentSquare.z * this.HARDCODED_SQUARE_SIZE;
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
}
