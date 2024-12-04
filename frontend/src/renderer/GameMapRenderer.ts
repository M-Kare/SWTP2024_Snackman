import * as THREE from 'three'
import {type IGameMap, MapObjectType} from "@/stores/IGameMapDTD";
import {SnackType} from "@/stores/Snack/ISnackDTD";
import {useGameMapStore} from "@/stores/gameMapStore";

/**
 * for rendering the game map
 */
export const GameMapRenderer = () => {
  const gameMapStore = useGameMapStore()

  // create new three.js scene
  const GROUNDSIZE = 1000
  let renderer: THREE.WebGLRenderer
  const scene = new THREE.Scene()

  // set up light
  const light = new THREE.DirectionalLight(0xffffff, 1)
  light.position.set(5, 10, 10)
  light.castShadow = true
  scene.add(light)

  // add more natural outdoor lighting
  const hemiLight = new THREE.HemisphereLight(0xffffbb, 0x080820, 1)
  scene.add(hemiLight)

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
    const wallMaterial = new THREE.MeshStandardMaterial({color: 'orange'})
    const wallGeometry = new THREE.BoxGeometry(sideLength, height, sideLength)
    const wall = new THREE.Mesh(wallGeometry, wallMaterial)

    // Position the wall
    wall.position.set(xPosition, yPosition, zPosition)
    scene.add(wall)
  }

  /**
   * initialize renderer
   *
   * @param canvas
   */
  const initRenderer = (canvas: HTMLCanvasElement): THREE.WebGLRenderer => {
    renderer = new THREE.WebGLRenderer({
      canvas,
      alpha: true,
      antialias: true, // activates edge smoothing for better image quality
    })
    renderer.setSize(window.innerWidth, window.innerHeight)
    renderer.setPixelRatio(window.devicePixelRatio)
    renderer.shadowMap.enabled = true // activates the shadow calculation

    return renderer
  }

  const createGameMap = (mapData: IGameMap) => {
    const OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH/2
    const DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH
    const WALL_HEIGHT = mapData.DEFAULT_WALL_HEIGHT

    createGround()

  // Iterate through map data and create walls
    for (const [id, square] of mapData.gameMap) {
      if (square.type === MapObjectType.WALL) {
        // Create wall at position (x, 0, z) -> y = 0 because of 'building the walls'
        createWall(square.indexX*DEFAULT_SIDE_LENGTH+OFFSET, 0, square.indexZ*DEFAULT_SIDE_LENGTH+OFFSET, WALL_HEIGHT, DEFAULT_SIDE_LENGTH)
      }
      if (square.type === MapObjectType.FLOOR) {
        const squareToAdd = createFloorSquare(square.indexX*DEFAULT_SIDE_LENGTH+OFFSET, square.indexZ*DEFAULT_SIDE_LENGTH+OFFSET, DEFAULT_SIDE_LENGTH)
        scene.add(squareToAdd)

        //TODO Remove because it's only for test purpose
        if(square.snack === null){
          console.log(square.id)

        }
        const snackToAdd = createSnackOnFloor(square.indexX*DEFAULT_SIDE_LENGTH+OFFSET, square.indexZ*DEFAULT_SIDE_LENGTH+OFFSET, DEFAULT_SIDE_LENGTH, square.snack?.snackType)
        scene.add(snackToAdd)
        gameMapStore.setSnackMeshId(id, snackToAdd.id)

      }
    }
  }

  const createSnackOnFloor = (
    xPosition: number,
    zPosition: number,
    sideLength: number,
    type: SnackType
  ) => {
    let color = 'blue';

    if (type == SnackType.STRAWBERRY) {
      color = 'purple'
    } else if (type == SnackType.ORANGE) {
      color = 'orange'
    } else if (type == SnackType.CHERRY) {
      color = 'red'
    } else if (type == SnackType.APPLE) {
      color = 'green'
    }

    // TODO add correct snack-material-design
    const SNACK_WIDTH_AND_DEPTH = sideLength / 3
    const SNACK_HEIGHT = 1
    const snackMaterial = new THREE.MeshStandardMaterial({color: color})
    const snackGeometry = new THREE.BoxGeometry(SNACK_WIDTH_AND_DEPTH, SNACK_HEIGHT, SNACK_WIDTH_AND_DEPTH)
    const snack = new THREE.Mesh(snackGeometry, snackMaterial)

    snack.position.set(xPosition, 0, zPosition)

    return snack
  }

  const createFloorSquare = (
    xPosition: number,
    zPosition: number,
    sideLength: number,
  ) => {
    // TODO squareHeight is set for seeing it actually in game
    const squareHeight = 0.1
    const squareMaterial = new THREE.MeshStandardMaterial({color: 'green'})
    const squareGeometry = new THREE.BoxGeometry(sideLength, squareHeight, sideLength)
    const square = new THREE.Mesh(squareGeometry, squareMaterial)

    // Position the square
    square.position.set(xPosition, 0, zPosition)

    return square
  }

  const createGround = () => {
    // ground setup
    const groundGeometry = new THREE.PlaneGeometry(GROUNDSIZE, GROUNDSIZE)
    const groundMaterial = new THREE.MeshMatcapMaterial({color: 'lightgrey'})
    const ground = new THREE.Mesh(groundGeometry, groundMaterial)
    ground.castShadow = true
    ground.receiveShadow = true
    ground.rotateX(-Math.PI / 2)
    ground.position.set(0, 0, 0)

    scene.add(ground)
  }

  const getScene = () => {
    return scene
  }

  return {initRenderer, createGameMap, getScene}
}
