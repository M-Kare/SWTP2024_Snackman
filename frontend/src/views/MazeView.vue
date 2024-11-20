<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as THREE from 'three'

const canvasRef = ref()
let renderer: THREE.WebGLRenderer
const scene = new THREE.Scene()

const camera = new THREE.PerspectiveCamera(
  45, window.innerWidth / window.innerHeight, 0.1, 100)
camera.position.set(5, 5, 10)
camera.lookAt(0, 0, 0)
scene.add(camera)

const light = new THREE.DirectionalLight(0xffffff, 1)
light.position.set(5, 10, 10)
light.castShadow = true
scene.add(light)

async function fetchMaze() {
  try {
    const response = await fetch('http://localhost:8080/api/maze')
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const mazeData = await response.json();

    for (let y = 0; y < mazeData.length; y++) {
      for (let x = 0; x < mazeData[y].length; x++) {
        if (mazeData[y][x] === '#') {
          createWall(x, 0, -y); // Wand an der Position (x,0,-y) erstellen
        }
      }
    }

    renderer.render(scene, camera) // Rendern nach dem Erstellen der Wände
  } catch (error) {
    console.error("Fehler beim Abrufen des Labyrinths:", error)
  }
}

function createWall(x: number, y: number, z: number) {
  const wallMaterial = new THREE.MeshStandardMaterial({ color: 'blue' })

  const cubeGeometry = new THREE.BoxGeometry(1, 3, 1)
  const cube = new THREE.Mesh(cubeGeometry, wallMaterial)

  // Positioniere den Cube
  cube.position.set(x, y + 1.5, z) // Staple die Cubes übereinander
  scene.add(cube)
}

onMounted(() => {
  renderer = new THREE.WebGLRenderer({
    canvas: canvasRef.value,
    alpha: true,
    antialias: true,
  })

  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(window.devicePixelRatio)

  renderer.shadowMap.enabled = true

  fetchMaze() // Maze abrufen und Wände erstellen

  window.addEventListener('resize', resizeCallback)
})

function resizeCallback() {
  renderer.setSize(window.innerWidth, window.innerHeight)
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
}
</script>
