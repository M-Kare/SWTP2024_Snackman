import * as THREE from 'three'
import {SnackType} from '@/stores/Snack/ISnackDTD'
import {ChickenThickness} from '@/stores/Chicken/IChickenDTD'

import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js'
import ghostGLB from '@/assets/ghost.glb'

/**
 * for creating the objects in the map
 * objects are rendered by the GameMapRenderer.ts
 */
export const GameObjectRenderer = () => {
  const GROUNDSIZE = 1000
  const loader = new GLTFLoader()

  let ghostModel: THREE.Group | null = null 

  const createSnackOnFloor = (
    xPosition: number,
    zPosition: number,
    sideLength: number,
    type: SnackType,
  ) => {
    let color = 'blue'

    switch (type) {
      case SnackType.STRAWBERRY:
        color = 'purple'
        break
      case SnackType.ORANGE:
        color = 'orange'
        break
      case SnackType.CHERRY:
        color = 'red'
        break
      case SnackType.APPLE:
        color = 'green'
        break
      case SnackType.EGG:
        color = 'white'
        break
      default:
        console.error("SnackType {} doesn't exist", type)
    }

    // TODO add correct snack-material-design
    const SNACK_WIDTH_AND_DEPTH = sideLength / 3
    const SNACK_HEIGHT = 1
    const snackMaterial = new THREE.MeshStandardMaterial({ color: color })
    const snackGeometry = new THREE.BoxGeometry(
      SNACK_WIDTH_AND_DEPTH,
      SNACK_HEIGHT,
      SNACK_WIDTH_AND_DEPTH,
    )
    const snack = new THREE.Mesh(snackGeometry, snackMaterial)

    snack.position.set(xPosition, 0, zPosition)

    return snack
  }

  const createChickenOnFloor = (
    xPosition: number,
    zPosition: number,
    sideLength: number,
    thickness: ChickenThickness,
  ) => {
    const color = 'black'
    const CHICKEN_WIDTH_AND_DEPTH = sideLength / 2
    const CHICKEN_HEIGHT = 15
    const scale =
      ChickenThickness[thickness as unknown as keyof typeof ChickenThickness]

    //@TODO add correct chicken-material-design
    const chickenMaterial = new THREE.MeshStandardMaterial({ color: color })
    const chickenGeometry = new THREE.BoxGeometry(
      CHICKEN_WIDTH_AND_DEPTH * scale,
      CHICKEN_HEIGHT,
      CHICKEN_WIDTH_AND_DEPTH * scale,
    )
    const chicken = new THREE.Mesh(chickenGeometry, chickenMaterial)
    chicken.castShadow = true
    chicken.receiveShadow = true

    chicken.position.set(xPosition, 0, zPosition)

    return chicken
  }

  async function createGhostOnFloor(
    xPosition: number,
    zPosition: number,
    yPosition: number,
    sideLength: number
  ): Promise<THREE.Group> {
    const placeholder = new THREE.Mesh(
      new THREE.SphereGeometry(0.5, 16, 16),
      new THREE.MeshStandardMaterial({ color: 'gray', opacity: 0.5, transparent: true })
    )
    placeholder.position.set(xPosition, yPosition, zPosition)
  
    // If the model is already loaded, clone it
    if (ghostModel) {
      const clonedGhost = ghostModel.clone()
      clonedGhost.position.set(xPosition, yPosition, zPosition)
      clonedGhost.scale.set(0.5, 0.5, 0.5)
      clonedGhost.traverse((child: any) => {
        if (child.isMesh) {
          child.castShadow = true
          child.receiveShadow = true
        }
      })
      return clonedGhost
    }
  
    // Load model asynchronously and replace placeholder
    return new Promise((resolve, reject) => {
      loader.load(
        ghostGLB,
        (gltf) => {
          ghostModel = gltf.scene
          ghostModel.position.set(xPosition, yPosition, zPosition)
          ghostModel.scale.set(0.5, 0.5, 0.5)
          ghostModel.castShadow = true
          ghostModel.receiveShadow = true
          resolve(ghostModel.clone())
        },
        undefined,
      )
    })
  }

  const createFloorSquare = (
    xPosition: number,
    zPosition: number,
    sideLength: number,
  ) => {
    // TODO squareHeight is set for seeing it actually in game
    const squareHeight = 0.1
    const squareMaterial = new THREE.MeshStandardMaterial({ color: 'green' })
    const squareGeometry = new THREE.BoxGeometry(
      sideLength,
      squareHeight,
      sideLength,
    )
    const square = new THREE.Mesh(squareGeometry, squareMaterial)

    // Position the square
    square.position.set(xPosition, 0, zPosition)

    return square
  }

  const createGround = () => {
    // ground setup
    const groundGeometry = new THREE.PlaneGeometry(GROUNDSIZE, GROUNDSIZE)
    const groundMaterial = new THREE.MeshMatcapMaterial({ color: 'lightgrey' })
    const ground = new THREE.Mesh(groundGeometry, groundMaterial)
    ground.castShadow = true
    ground.receiveShadow = true
    ground.rotateX(-Math.PI / 2)
    ground.position.set(0, 0, 0)

    return ground
  }

  /**
   * create a single wall with given x-, y-, z-position, height and sidelengthm
   */
  const createWall = (
    xPosition: number,
    yPosition: number,
    zPosition: number,
    height: number,
    sideLength: number,
  ) => {
    // TODO add correct wall-material-design!!
    const wallMaterial = new THREE.MeshStandardMaterial({ color: 'orange' })
    const wallGeometry = new THREE.BoxGeometry(sideLength, height, sideLength)
    const wall = new THREE.Mesh(wallGeometry, wallMaterial)

    // Position the wall
    wall.position.set(xPosition, yPosition, zPosition)

    return wall
  }

  return {
    createSnackOnFloor,
    createChickenOnFloor,
    createFloorSquare,
    createGround,
    createWall,
    createGhostOnFloor
  }
}
