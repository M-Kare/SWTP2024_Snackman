import * as THREE from 'three'
import { type IGameMap, MapObjectType } from '@/stores/IGameMapDTD'
import { useGameMapStore } from '@/stores/gameMapStore'
import { GameObjectRenderer } from '@/renderer/GameObjectRenderer'
import { SnackType } from '@/stores/Snack/ISnackDTD'

/**
 * for rendering the game map
 */
export const GameMapRenderer = () => {
  const gameMapStore = useGameMapStore()
  const scene = gameMapStore.getScene()
  const gameObjectRenderer = GameObjectRenderer()

  // create new three.js scene
  const GROUNDSIZE = 1000
  let renderer: THREE.WebGLRenderer

  // set up light
  const light = new THREE.DirectionalLight(0xffffff, 1)
  light.position.set(5, 10, 10)
  // light.castShadow = true
  scene.add(light)

  // add more natural outdoor lighting
  const hemiLight = new THREE.HemisphereLight(0xffffbb, 0x080820, 1)
  scene.add(hemiLight)

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
    const OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH / 2
    const DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH
    const WALL_HEIGHT = mapData.DEFAULT_WALL_HEIGHT

    const ground = gameObjectRenderer.createGround()
    scene.add(ground)

    // Iterate through map data and create walls
    for (const [id, square] of mapData.gameMap) {
      if (square.type === MapObjectType.WALL) {
        // Create wall at position (x, 0, z) -> y = 0 because of 'building the walls'
        const wall = gameObjectRenderer.createWall(
          square.indexX * DEFAULT_SIDE_LENGTH + OFFSET,
          0,
          square.indexZ * DEFAULT_SIDE_LENGTH + OFFSET,
          WALL_HEIGHT,
          DEFAULT_SIDE_LENGTH,
        )
        scene.add(wall)
      }
      if (square.type === MapObjectType.FLOOR) {

        if (square.snack != null && square.snack.snackType != SnackType.EMPTY) {
          const snackToAdd = gameObjectRenderer.createSnackOnFloor(
            square.indexX * DEFAULT_SIDE_LENGTH + OFFSET,
            square.indexZ * DEFAULT_SIDE_LENGTH + OFFSET,
            DEFAULT_SIDE_LENGTH,
            square.snack?.snackType,
          )
          scene.add(snackToAdd)
          gameMapStore.setSnackMeshId(id, snackToAdd.id)
        }
      }
    }
    // add chickens
    for (let currentChicken of mapData.chickens) {
      const chickenToAdd = gameObjectRenderer.createChickenOnFloor(
        currentChicken.chickenPosX * DEFAULT_SIDE_LENGTH + OFFSET,
        currentChicken.chickenPosZ * DEFAULT_SIDE_LENGTH + OFFSET,
        DEFAULT_SIDE_LENGTH,
        currentChicken.thickness,
      )
      scene.add(chickenToAdd)

      gameMapStore.setChickenMeshId(chickenToAdd.id, currentChicken.id)
    }

    console.log("GameMap data ", mapData)
    for (let currentGhost of mapData.scriptGhosts) {
      console.log("Initialising script ghost with x {} y {}", currentGhost.scriptGhostPosX, currentGhost.scriptGhostPosZ)
      const scriptGhostToAdd = gameObjectRenderer.createGhostOnFloor(
        currentGhost.scriptGhostPosX * DEFAULT_SIDE_LENGTH + OFFSET,
        currentGhost.scriptGhostPosZ * DEFAULT_SIDE_LENGTH + OFFSET,
        0,
        DEFAULT_SIDE_LENGTH
      )
      scene.add(scriptGhostToAdd)

      gameMapStore.setScriptGhostMeshId(scriptGhostToAdd.id, currentGhost.id)
    }
  }

  const getScene = () => {
    return scene
  }

  return {initRenderer, createGameMap, getScene}
}
