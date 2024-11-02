<template>
  <canvas ref="canvasRef"></canvas>
  <!-- <div>
    <h1>Testabfrage</h1>
    <button @click="infoholen">Server-Anfrage</button>
    <p />
    Anfrager-IP-Adresse: {{ anfragerip }}
    <p />
    Server-Zeit: {{ zeitstempel }}
  </div> -->
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as THREE from "three"
import { ArcballControls } from 'three/addons/controls/ArcballControls.js';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

const canvasRef = ref()
let renderer: THREE.WebGLRenderer;
const scene = new THREE.Scene()

const camera = new THREE.PerspectiveCamera(45, window.innerWidth/window.innerHeight, 0.1, 100)
camera.position.set(0,2,10)
camera.lookAt(0,0,0)
scene.add(camera)

const light = new THREE.DirectionalLight()
light.position.set(0,10,10)
light.castShadow = true
scene.add(light)


const boxGeometry = new THREE.BoxGeometry(1,1,1)
const boxMaterial = new THREE.MeshMatcapMaterial({ color: "blue"})
const box = new THREE.Mesh(boxGeometry, boxMaterial)
box.castShadow = true
box.receiveShadow = true
scene.add(box)


onMounted(()=>{
  renderer = new THREE.WebGLRenderer({ 
    canvas: canvasRef.value, 
    alpha: true,
    antialias: true,
  })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(window.devicePixelRatio)

  renderer.shadowMap.enabled = true
  

  const controls = new ArcballControls(camera, renderer.domElement)
  controls.addEventListener("change", function(){renderer.render(scene, camera)})
  
  
  renderer.render(scene, camera)
})








// // Java: record BeispielDTO(String anfragerip, String zeitstempel) {}
// interface IBeispielDTO {
//   anfragerip: string
//   zeitstempel: string
// }

// const anfragerip = ref('???')
// const zeitstempel = ref('??:??')

// async function infoholen() {
//   try {
//     const response = await fetch('/api/getinfo')
//     if (!response.ok) throw new Error(response.statusText)
//     const bspdto: IBeispielDTO = await response.json()

//     anfragerip.value = bspdto.anfragerip
//     zeitstempel.value = bspdto.zeitstempel
//   } catch (e) {
//     anfragerip.value = `ERROR - ${e}`
//     zeitstempel.value = '??:??'
//     alert(e)
//   }
// }
</script>
