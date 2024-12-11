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

    private isJumping: boolean;
    private lastJumpTime: number;
    private doubleJump: boolean;
    private spacePressed: boolean;

    constructor(renderer: WebGLRenderer, posX: number, posY: number, posZ: number, radius: number, speed: number){
        this.prevTime = performance.now();
        this.moveBackward = false;
        this.moveForward = false;
        this.moveLeft = false;
        this.moveRight = false;
        this.canJump = true;
        this.movementDirection = new THREE.Vector3();

        this.isJumping = false;
        this.lastJumpTime = 0;
        this.doubleJump = false;
        this.spacePressed = false;

        this.radius = radius;
        this.speed = speed;

        this.camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 100)
        this.camera.position.set(posX, posY, posZ)
        this.controls = new PointerLockControls(this.camera, renderer.domElement)
        document.addEventListener('keydown', (event)=>{this.onKeyDown(event)})
        document.addEventListener('keyup', (event)=>{this.onKeyUp(event)})
        document.addEventListener('click', ()=>{this.controls.lock()})
    }

    public getCamera():THREE.PerspectiveCamera{
        return this.camera;
    }

    public getControls():PointerLockControls{
        return this.controls;
    }

    public getMovementDirection(){
        return this.movementDirection;
    }

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

          case 'Space':
            this.spacePressed = false;
            break;
        }
      }

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
        }
      }

      public getInput(){
        return {forward:this.moveForward, backward:this.moveBackward, left:this.moveLeft, right:this.moveRight}
      }

    // lerp is used to interpolate the two positions
    public setPosition(x: number,y: number,z: number) {
      this.camera.position.set(x,y,z);
      if (y <= 2) {
        this.isJumping = false
        this.doubleJump = false
      }
    }

    public setCameraRotation(x: number,y: number,z: number) {
      this.camera.rotation.set(x,y,z);
    }

    // code inspired by from https://github.com/mrdoob/three.js/blob/master/examples/misc_controls_pointerlock.html
    // new position is calculated based on which keys are held
    public updatePlayer() {
      const time = performance.now()
      const delta = (time - this.prevTime) / 1000

      this.movementDirection.z = Number(this.moveForward) - Number(this.moveBackward)
      this.movementDirection.x = Number(this.moveRight) - Number(this.moveLeft)

      let move = new THREE.Vector3(0,0,0)
      if(this.moveForward || this.moveBackward){
        move.z -= this.movementDirection.z
      }
      if(this.moveLeft || this.moveRight){
        move.x += this.movementDirection.x
      }

      if(move.z != 0 && move.x != 0){
        move.normalize();
      }
        move.x = move.x  * delta * this.speed
        move.z = move.z * delta * this.speed

      move.applyQuaternion(this.camera.quaternion)
      this.camera.position.x += move.x
      this.camera.position.z += move.z
      this.prevTime = time
    }

    public getIsJumping() {
      return this.isJumping;
    }

    public getIsDoubleJumping() {
      return this.doubleJump;
    }
}
