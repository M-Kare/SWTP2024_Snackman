<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { MazeRenderer } from '@/renderer/MazeRenderer'
import { fetchMazeDataFromBackend } from '@/services/MazeDataService'

const canvasRef = ref()

/**
 *  initializes a 3D maze renderer when the component is mounted,
 *  fetches maze data from a backend, creates the maze,
 *  and sets up a window resize listener
 */
onMounted(async () => {
  if (!canvasRef.value) console.error('canvas ref empty!')

  // for rendering the scene, create maze in 3d and change window size
  const { initRenderer, createMaze, resizeCallback } = MazeRenderer()

  initRenderer(canvasRef.value)

  try {
    const mazeData = await fetchMazeDataFromBackend()
    createMaze(mazeData)
  } catch (error) {
    console.error('Error when retrieving the maze:', error)
  }

  window.addEventListener('resize', resizeCallback)
})
</script>
