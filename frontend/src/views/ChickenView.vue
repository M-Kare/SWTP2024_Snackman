<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as THREE from 'three'
import { Client } from '@stomp/stompjs'
import { ChickenThickness, type IChickenDTD } from '@/stores/IChickenDTD'

const wsurl = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/chicken'
const CHICKEN_HEIGHT = 1

const stompclient = new Client({ brokerURL: wsurl })
stompclient.onWebSocketError = event => {
  console.log(event)
}
stompclient.onStompError = frame => {
  console.log(frame)
}
stompclient.onConnect = frame => {
  // Callback: erfolgreicher Verbindugsaufbau zu Broker
  stompclient.subscribe(DEST, message => {
    // Callback: Nachricht auf DEST empfangen
    // empfangene Nutzdaten in message.body abrufbar,
    // ggf. mit JSON.parse(message.body) zu JS konvertieren
    const event: IChickenDTD = JSON.parse(message.body)
    console.log('ERHALTENES CHANGE: ')
    console.log(event)
    updateChicken(event)
  })
}
stompclient.activate()

const canvasRef = ref()
let renderer: THREE.WebGLRenderer
const scene = new THREE.Scene()

const camera = new THREE.PerspectiveCamera(
  45,
  window.innerWidth / window.innerHeight,
  0.1,
  100,
)
camera.position.set(0, 2, 10)
camera.lookAt(0, 0, 0)
scene.add(camera)

const light = new THREE.DirectionalLight()
light.position.set(0, 10, 10)
light.castShadow = true
scene.add(light)

const boxGeometry = new THREE.BoxGeometry(1, 1, 1)
const boxMaterial = new THREE.MeshMatcapMaterial({ color: 'blue' })
const chicken = new THREE.Mesh(boxGeometry, boxMaterial)
chicken.castShadow = true
chicken.receiveShadow = true
scene.add(chicken)

function updateChicken(e: IChickenDTD) {
    console.log("Scaling");
  switch (e.thickness) {
    case ChickenThickness.THIN:
      chicken.scale.set(1, 1, 1)
      break

    case ChickenThickness.SLIGHTLY_THICK:
      chicken.scale.set(1.5, 1.5, 1.5)
      break

    case ChickenThickness.MEDIUM:
      chicken.scale.set(2, 2, 2)
      break

    case ChickenThickness.HEAVY:
      chicken.scale.set(2.5, 2.5, 2.5)
      break

    case ChickenThickness.VERY_HEAVY:
      chicken.scale.set(3, 3, 3)
      break

    default:
      throw Error('ChickenThickness not recognized.')
  }

  chicken.position.set(e.square.indexX, CHICKEN_HEIGHT, e.square.indexZ)
  renderer.render(scene, camera)
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

  renderer.render(scene, camera)
  window.addEventListener('resize', resizeCallback)
})

function resizeCallback() {
  renderer.setSize(window.innerWidth, window.innerHeight)
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
}
</script>
