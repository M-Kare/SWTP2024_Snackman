import { ChickenThickness } from '@/stores/IChickenDTD'
import type { WebGLRenderer } from 'three'
import * as THREE from 'three'
import { PointerLockControls } from 'three/addons/controls/PointerLockControls.js'

export class Player {

    
  private prevTime: DOMHighResTimeStamp
  private DECELERATION: number
  private ACCELERATION: number

  // Booleans for checking movement-input
  private moveForward: boolean
  private moveBackward: boolean
  private moveLeft: boolean
  private moveRight: boolean
  private canJump: boolean

  private chickenMesh: THREE.Mesh; // Referenz auf das Chicken-Modell
  private increaseThickness: ChickenThickness

  private camera: THREE.PerspectiveCamera
  private controls: PointerLockControls

  private velocity: THREE.Vector3
  private movementDirection: THREE.Vector3

  constructor(
    renderer: WebGLRenderer,
    deceleration: number,
    acceleration: number,
    chickenMesh: THREE.Mesh, // Chicken wird beim Erstellen übergeben
  ) {
    this.DECELERATION = deceleration
    this.ACCELERATION = acceleration

    this.prevTime = performance.now()
    this.moveBackward = false
    this.moveForward = false
    this.moveLeft = false
    this.moveRight = false
    this.canJump = true
    this.chickenMesh = chickenMesh; // Chicken-Modell speichern
    this.increaseThickness = ChickenThickness.THIN

    this.velocity = new THREE.Vector3()
    this.movementDirection = new THREE.Vector3()

    this.camera = new THREE.PerspectiveCamera(
      45,
      window.innerWidth / window.innerHeight,
      0.1,
      100,
    )
    this.camera.position.set(0, 2, 10)
    this.controls = new PointerLockControls(this.camera, renderer.domElement)
    document.addEventListener('keydown', event => {
      this.onKeyDown(event)
    })
    document.addEventListener('keyup', event => {
      this.onKeyUp(event)
    })
    document.addEventListener('click', () => {
      this.controls.lock()
    })
  }

  public getCamera(): THREE.PerspectiveCamera {
    return this.camera
  }

  public getControls(): PointerLockControls {
    return this.controls
  }

  public getVelocity() {
    return this.velocity
  }

  public getMovementDirection() {
    return this.movementDirection
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

      case 'F':
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

        case 'F':
            // Logik zur Erhöhung der Thickness
            switch (this.increaseThickness) {
              case ChickenThickness.THIN:
                this.increaseThickness = ChickenThickness.SLIGHTLY_THICK;
                this.chickenMesh.scale.set(1.5, 1.5, 1.5);
                break;
    
              case ChickenThickness.SLIGHTLY_THICK:
                this.increaseThickness = ChickenThickness.MEDIUM;
                this.chickenMesh.scale.set(2, 2, 2);
                break;
    
              case ChickenThickness.MEDIUM:
                this.increaseThickness = ChickenThickness.HEAVY;
                this.chickenMesh.scale.set(2.5, 2.5, 2.5);
                break;
    
              case ChickenThickness.HEAVY:
                this.increaseThickness = ChickenThickness.VERY_HEAVY;
                this.chickenMesh.scale.set(3, 3, 3);
                break;
    
              case ChickenThickness.VERY_HEAVY:
                this.increaseThickness = ChickenThickness.THIN;
                this.chickenMesh.scale.set(1, 1, 1);
                break;
    
              default:
                throw Error('ChickenThickness not recognized.');
            }
            break;
    }
  }

  public setPosition(x: number, y: number, z: number) {
    this.camera.position.set(x, y, z)
  }

  public setCameraRotation(x: number, y: number, z: number) {
    this.camera.rotation.set(x, y, z)
  }

  public updatePlayer() {
    const time = performance.now()
    const delta = (time - this.prevTime) / 1000

    this.velocity.x -= this.velocity.x * this.DECELERATION * delta
    this.velocity.z -= this.velocity.z * this.DECELERATION * delta
    this.movementDirection.z =
      Number(this.moveForward) - Number(this.moveBackward)
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
