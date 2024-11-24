import type { WebGLRenderer } from 'three'
import * as THREE from 'three'
import { PointerLockControls } from 'three/addons/controls/PointerLockControls.js'

export class Player {
    private prevTime: DOMHighResTimeStamp
    private DECELERATION: number;
    private ACCELERATION: number;

    // Booleans for checking movement-input
    private moveForward: boolean;
    private moveBackward: boolean;
    private moveLeft: boolean;
    private moveRight: boolean;
    private canJump: boolean;

    private camera: THREE.PerspectiveCamera;
    private controls: PointerLockControls;

    private velocity: THREE.Vector3;
    private movementDirection: THREE.Vector3;

    constructor(renderer: WebGLRenderer, deceleration: number, acceleration: number){
        this.DECELERATION = deceleration;
        this.ACCELERATION = acceleration;

        this.prevTime = performance.now();
        this.moveBackward = false;
        this.moveForward = false;
        this.moveLeft = false;
        this.moveRight = false;
        this.canJump = true;

        this.velocity = new THREE.Vector3();
        this.movementDirection = new THREE.Vector3();

        this.camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 100)
        this.camera.position.set(0, 2, 0)
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

    public getVelocity(){
        return this.velocity;
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
        }
      }

    // lerp is used to interpolate the two positions
    public setPosition(x: number,y: number,z: number) {
      this.camera.position.lerp(new THREE.Vector3(x,y,z), 1);
    }

    public setCameraRotation(x: number,y: number,z: number) {
      this.camera.rotation.set(x,y,z);
    }

    // code inspired by from https://github.com/mrdoob/three.js/blob/master/examples/misc_controls_pointerlock.html
    // new position is calculated based on which keys are held
    public updatePlayer() {
        const time = performance.now()
        const delta = (time - this.prevTime) / 1000

        this.velocity.x -= this.velocity.x * this.DECELERATION * delta
        this.velocity.z -= this.velocity.z * this.DECELERATION * delta
        this.movementDirection.z = Number(this.moveForward) - Number(this.moveBackward)
        this.movementDirection.x = Number(this.moveRight) - Number(this.moveLeft)
        this.movementDirection.normalize()
        if (this.moveForward || this.moveBackward)
            this.velocity.z -= this.movementDirection.z * this.ACCELERATION * delta
        if (this.moveLeft || this.moveRight)
            this.velocity.x -= this.movementDirection.x * this.ACCELERATION * delta
        this.controls.moveRight(-this.velocity.x * delta)
        this.controls.moveForward(-this.velocity.z * delta)
        this.prevTime = time
    }
}
