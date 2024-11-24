import * as THREE from 'three'

/**
 * for rendering the maze
 */
export const MazeRenderer = () => {
  // create new three.js scene
  let renderer: THREE.WebGLRenderer
  const scene = new THREE.Scene()

  // set up camera
  const camera = new THREE.PerspectiveCamera(
    45,
    window.innerWidth / window.innerHeight,
    0.1,
    100,
  )

  // switch x, y, z for editing the camera perspective
  camera.position.set(6, 6, 20)
  camera.lookAt(6, 0, 0)
  scene.add(camera)

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
  const initRenderer = (canvas: HTMLCanvasElement) => {
    renderer = new THREE.WebGLRenderer({
      canvas,
      alpha: true,
      antialias: true, // activates edge smoothing for better image quality
    })
    renderer.setSize(window.innerWidth, window.innerHeight)
    renderer.setPixelRatio(window.devicePixelRatio)
    renderer.shadowMap.enabled = true // activates the shadow calculation
  }

  const createMaze = (mazeData: IMazeDTD) => {
    const DEFAULT_SIDE_LENGTH = mazeData['default-side-length']
    const WALL_HEIGHT = mazeData.height

    // Iterate through maze data and create walls
    for (const item of mazeData.map) {
      if (item.type === 'wall') {
        // Create wall at position (x, 0, z) -> y = 0 because of 'floor'
        createWall(item.x, 0, item.z, WALL_HEIGHT, DEFAULT_SIDE_LENGTH)
      }
    }
    // Render after creating walls
    renderer.render(scene, camera)
  }

  /**
   * handle window rezising
   */
  const resizeCallback = () => {
    renderer.setSize(window.innerWidth, window.innerHeight)
    camera.aspect = window.innerWidth / window.innerHeight
    camera.updateProjectionMatrix()

    // re-render the scene
    renderer.render(scene, camera)
  }

  return { initRenderer, createMaze, resizeCallback }
}
