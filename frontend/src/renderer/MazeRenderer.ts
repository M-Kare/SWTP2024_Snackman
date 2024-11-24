import * as THREE from 'three'

/**
 * for rendering the maze
 */
export const MazeRenderer = () => {
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
    const wallMaterial = new THREE.MeshStandardMaterial({ color: 'orange' })
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


  const createMaze = (mazeData: IMazeDTD) => {
    const DEFAULT_SIDE_LENGTH = mazeData['default-side-length']
    const WALL_HEIGHT = mazeData.height

    createGround()

    // Iterate through maze data and create walls
    for (const item of mazeData.map) {
      if (item.type === 'wall') {
        // Create wall at position (x, 0, z) -> y = 0 because of 'building the walls'
        createWall(item.x, 0, item.z, WALL_HEIGHT, DEFAULT_SIDE_LENGTH)
      }
    }
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

    scene.add(ground)
  }

  const getScene = () => {
    return scene
  }

  return { initRenderer, createMaze, getScene }
}
