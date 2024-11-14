<template>
  <canvas ref="canvasRef"></canvas>
  <Snack color="blue" :x-pos=2 :y-pos=2 :z-pos=2  @meshCreated="addSnackToScene" ></Snack>
  <Snack color="green" :x-pos=3 :y-pos=3 :z-pos=3 @meshCreated="addSnackToScene"></Snack>
  <Snack color="yellow" :x-pos=1 :y-pos=1 :z-pos=1 @meshCreated="addSnackToScene"></Snack>
  <Snack :x-pos=-2 :y-pos=-1 :z-pos=3 @meshCreated="addSnackToScene"></Snack>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as THREE from "three"
import Snack from "@/components/Snack.vue";
import { ArcballControls } from 'three/addons/controls/ArcballControls.js';

const canvasRef = ref()

let renderer: THREE.WebGLRenderer;
const scene = new THREE.Scene()

const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 1, 10000)
camera.position.set(0, 2, 10)
camera.lookAt(0, 0, 0)
scene.add(camera)

//Helper
const axesHelper = new THREE.AxesHelper( 5 );
const gridHelper = new THREE.GridHelper( 10, 10 );

function addSnackToScene(snack: THREE.Mesh) {
  scene.add(snack)
}
scene.add( axesHelper );
scene.add( gridHelper );

onMounted(() => {
  renderer = new THREE.WebGLRenderer({
    canvas: canvasRef.value,
    alpha: true,
    antialias: true,
  })

  const controls = new ArcballControls( camera, renderer.domElement, scene );
  controls.addEventListener( 'change', function () {
    renderer.render( scene, camera );
  } );

  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.render(scene, camera)
})
</script>
