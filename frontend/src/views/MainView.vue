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

  snackData.forEach((snack: ISnackDTD) =>  {
    if(snack.snackType == SnackType.STRAWBERRY){
      scene.add(createSnack(snack.position.x, snack.position.y, snack.position.z, 'purple'))
    }  else if (snack.snackType == SnackType.ORANGE){
      scene.add(createSnack(snack.position.x, snack.position.y, snack.position.z, 'orange'))
    } else {
      scene.add(createSnack(snack.position.x, snack.position.y, snack.position.z))
    }
  })

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
