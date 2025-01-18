import * as THREE from 'three'
import {SnackType} from '@/stores/Snack/ISnackDTD'
import {ChickenThickness} from '@/stores/Chicken/IChickenDTD'

/**
 * for creating the objects in the map
 * objects are rendered by the GameMapRenderer.ts
 */
export const GameObjectRenderer = () => {
  const GROUNDSIZE = 1000

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

  const createGhostOnFloor = (
    xPosition: number,
    zPosition: number,
    yPosition: number,
    sideLength: number
  ) => {
    let color = 'green';
    const GHOST_WIDTH_AND_DEPTH = sideLength / 2
    const GHOST_HEIGHT = 15

    //@TODO add correct ghost-material-design
    const ghostMaterial = new THREE.MeshStandardMaterial({color: color})
    const ghostGeometry = new THREE.BoxGeometry(GHOST_WIDTH_AND_DEPTH, GHOST_HEIGHT, GHOST_WIDTH_AND_DEPTH)
    const ghost = new THREE.Mesh(ghostGeometry, ghostMaterial)
    ghost.castShadow = true
    ghost.receiveShadow = true

    ghost.position.set(xPosition, yPosition, zPosition)

    return ghost
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
    const groundTexture = new THREE.TextureLoader().load('./textures/ground_white_comic_2.jpg')
    groundTexture.wrapS = THREE.RepeatWrapping;
    groundTexture.wrapT = THREE.RepeatWrapping;
    groundTexture.repeat.set(1000, 1000);
    const groundGeometry = new THREE.PlaneGeometry(GROUNDSIZE, GROUNDSIZE)
    const groundMaterial = new THREE.MeshStandardMaterial({
                              map: groundTexture,
                              color: 0xffffff,
                              emissive: 0x000000,
                              roughness: 0.7,
                              metalness: 0.1,
                            })
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
    //frontend/public/textures/pngtree_cartoon_style_seamless_textured_surface_square.jpg
    const wallTexture = new THREE.TextureLoader().load('./textures/pngtree_cartoon_style_seamless_textured_surface_square.jpg')
    wallTexture.wrapS = THREE.RepeatWrapping;
    wallTexture.wrapT = THREE.RepeatWrapping;
    wallTexture.repeat.set(1, 1.87);

    const wallMaterial = new THREE.MeshStandardMaterial({
                                  map: wallTexture,
                                  color: 0xffd133,
                                  emissive: 0x000000,
                                  emissiveIntensity: 0.5,
                                  roughness: 0.9,
                                })
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
