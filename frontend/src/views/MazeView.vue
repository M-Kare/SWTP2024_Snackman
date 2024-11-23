<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as THREE from 'three'

// create new three.js scene
const canvasRef = ref()
let renderer: THREE.WebGLRenderer
const scene = new THREE.Scene()

// set up camera
const camera = new THREE.PerspectiveCamera(
  45,
  window.innerWidth / window.innerHeight,
  0.1,
  100,
)
// switch y to 2 for having the right perspective to play
// switch z to 0 for being in the maze
camera.position.set(0, 8, 15)
camera.lookAt(0, 0, 0)
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
 * fetch maze data and create walls
 */
async function fetchMaze(): Promise<void> {
  let mazeData: IMazeDTD

  try {
    // rest endpoint from backend
    const response = await fetch('/api/maze')
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    const mazeData: IMazeDTD = await response.json()
    console.info(mazeData)

    const DEFAULT_SIDE_LENGTH = mazeData['default-side-length']
    console.info(DEFAULT_SIDE_LENGTH)
    const WALL_HEIGHT = mazeData.height
    console.info(WALL_HEIGHT)

    // Iterate through maze data and create walls
    for (const item of mazeData.map) {
      if (item.type === 'wall') {
        // Create wall at position (x, 0, -z) -> y = 0 because of 'floor'
        createWall(item.x, 0, item.z, WALL_HEIGHT, DEFAULT_SIDE_LENGTH)
      }
    }
    // Render after creating walls
    renderer.render(scene, camera)
  } catch (error) {
    console.error('Fehler beim Abrufen des Labyrinths:', error)
  }
}

/**
 * create a single wall
 *
 * @param x x-coordinate
 * @param y y-coordinate
 * @param z z-coordinate
 * @param height wall-height
 * @param sideLength side-length for width and depth
 */
function createWall(
  x: number,
  y: number,
  z: number,
  height: number,
  sideLength: number,
) {
  const wallMaterial = new THREE.MeshStandardMaterial({ color: 'orange' })
  const cubeGeometry = new THREE.BoxGeometry(sideLength, height, sideLength)
  const cube = new THREE.Mesh(cubeGeometry, wallMaterial)

  // Position the cube
  cube.position.set(x, y, z)
  scene.add(cube)
}

onMounted(() => {
  // initialize renderer
  renderer = new THREE.WebGLRenderer({
    canvas: canvasRef.value,
    alpha: true,
    antialias: true, // activates edge smoothing for better image quality
  })

  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(window.devicePixelRatio)

  renderer.shadowMap.enabled = true // activates the shadow calculation

  fetchMaze() // fetch maze and create walls

  window.addEventListener('resize', resizeCallback)
})

/**
 * handle window rezising
 */
function resizeCallback() {
  renderer.setSize(window.innerWidth, window.innerHeight)
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()

  // re-render the scene
  renderer.render(scene, camera)
}
</script>
