<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue'
import * as THREE from "three"
import {ArcballControls} from 'three/addons/controls/ArcballControls.js';
import {type ISnackDTD, SnackType} from '@/stores/Snack/ISnackDTD'
import {createSnack} from "@/components/Snack";


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

async  function getSnacks(): Promise<ISnackDTD[]>{
  const url = '/api/snack'
  const snackResponse = await fetch(url)

  if(!snackResponse.ok) throw new Error(snackResponse.statusText)
  const snackData = await snackResponse.json()

  return snackData
}

onMounted(async () => {
  const snackData: Array<ISnackDTD> = await getSnacks()
  const cherryMap: Map<number, THREE.Mesh> = new Map();

  snackData.forEach((snack: ISnackDTD) =>  {
    if(snack.snackType == SnackType.STRAWBERRY){
      const createdSnack = createSnack(snack.position.x, snack.position.y, snack.position.z, 'purple')
      cherryMap.set(snack.id, createdSnack)
      scene.add(createdSnack)
    }  else if (snack.snackType == SnackType.ORANGE){
      const createdSnack = createSnack(snack.position.x, snack.position.y, snack.position.z, 'orange')
      cherryMap.set(snack.id, createdSnack)
      scene.add(createdSnack)
    } else {
      const createdSnack = createSnack(snack.position.x, snack.position.y, snack.position.z)
      cherryMap.set(snack.id, createdSnack)
      scene.add(createdSnack)
    }
  })

  //entfernt cube.
  cherryMap.get(3)?.removeFromParent()


  console.log(snackData)

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
