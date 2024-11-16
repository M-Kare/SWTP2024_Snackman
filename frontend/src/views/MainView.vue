<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import * as THREE from "three"
import {ArcballControls} from 'three/addons/controls/ArcballControls.js';
import {addSnacksToScene} from "@/services/SnackService";


const canvasRef = ref()

let renderer: THREE.WebGLRenderer;
const scene = new THREE.Scene()

const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 1, 10000)
camera.position.set(0, 2, 10)
camera.lookAt(0, 0, 0)
scene.add(camera)

//Helper
const axesHelper = new THREE.AxesHelper(5);
const gridHelper = new THREE.GridHelper(10, 10);

scene.add(axesHelper);
scene.add(gridHelper);

onMounted(async () => {
  await addSnacksToScene(scene)

  renderer = new THREE.WebGLRenderer({
    canvas: canvasRef.value,
    alpha: true,
    antialias: true,
  })

  const controls = new ArcballControls(camera, renderer.domElement, scene);
  controls.addEventListener('change', function () {
    renderer.render(scene, camera);
  });

  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.render(scene, camera)
})
</script>
