<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import * as THREE from 'three'
import { Client } from '@stomp/stompjs'
import type { IFrontendNachrichtEvent } from '@/services/IFrontendNachrichtEvent'
import { Player } from '@/components/Chicken'
import type { IChickenDTD } from '@/stores/IChickenDTD'

const GROUNDSIZE = 1000
const DECELERATION = 20.0
const ACCELERATION = 300.0
const WSURL = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/cube'

// stomp
const stompclient = new Client({ brokerURL: WSURL })
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
    player.setPosition(
      event.square.indexX,
      event.square.DEFAULT_SIDE_LENGTH,
      event.square.indexZ,
    )
  })
}
stompclient.activate()

const canvasRef = ref()
let renderer: THREE.WebGLRenderer
let player: Player
const scene = new THREE.Scene()

// camera setup
let camera = new THREE.PerspectiveCamera(
  45,
  window.innerWidth / window.innerHeight,
  0.1,
  100,
)
camera.position.set(0, 2, 10)
scene.add(camera)

// light source setup
const light = new THREE.DirectionalLight()
light.position.set(0, 10, 10)
light.castShadow = true
scene.add(light)

// ground setup
const groundGeometry = new THREE.PlaneGeometry(GROUNDSIZE, GROUNDSIZE)
const groundMaterial = new THREE.MeshMatcapMaterial({ color: 'lightgrey' })
const ground = new THREE.Mesh(groundGeometry, groundMaterial)
ground.castShadow = true
ground.receiveShadow = true
ground.rotateX(-Math.PI / 2)
ground.position.set(0, 0, 0)
scene.add(ground)

// test box1
const box01Geometry = new THREE.BoxGeometry(1, 1, 1)
const box01Material = new THREE.MeshMatcapMaterial({ color: 'blue' })
const box01 = new THREE.Mesh(box01Geometry, box01Material)
box01.castShadow = true
box01.receiveShadow = true
box01.position.set(10, 0.5, 10)
scene.add(box01)

// test box2
const box02Geometry = new THREE.BoxGeometry(1, 1, 1)
const box02Material = new THREE.MeshMatcapMaterial({ color: 'blue' })
const box02 = new THREE.Mesh(box02Geometry, box02Material)
box02.castShadow = true
box02.receiveShadow = true
box02.position.set(-10, 5, 10)
scene.add(box02)

// is called every frame, changes camera position and velocity
function animate() {
  player.updatePlayer()
  renderer.render(scene, camera)
  try {
    //Sende and /topic/player/update
    const messageObject = {
      posX: player.getCamera().position.x,
      posY: player.getCamera().position.y,
      posZ: player.getCamera().position.z,
      dirX: player.getCamera().rotation.x,
      dirY: player.getCamera().rotation.y,
      dirZ: player.getCamera().rotation.z,
    }
    stompclient.publish({
      destination: DEST + '/player',
      headers: {},
      body: JSON.stringify(messageObject),
    })
  } catch (fehler) {
    console.log(fehler)
  }
}

// Chicken-Modell erstellen
const chickenGeometry = new THREE.BoxGeometry(1, 1, 1)
const chickenMaterial = new THREE.MeshMatcapMaterial({ color: 'blue' })
const chickenMesh = new THREE.Mesh(chickenGeometry, chickenMaterial)
chickenMesh.castShadow = true
chickenMesh.receiveShadow = true
scene.add(chickenMesh)

onMounted(() => {
  renderer = new THREE.WebGLRenderer({
    canvas: canvasRef.value!,
    alpha: true,
    antialias: true,
  })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(window.devicePixelRatio)
  renderer.shadowMap.enabled = true

  // Player-Instanz mit Chicken-Modell erstellen
  player = new Player(renderer, DECELERATION, ACCELERATION, chickenMesh)
  camera = player.getCamera()
  scene.add(player.getControls().object)

  renderer.render(scene, camera)
  renderer.setAnimationLoop(animate)
  window.addEventListener('resize', resizeCallback)
})

onUnmounted(() => {
  renderer.setAnimationLoop(null)
})

function resizeCallback() {
  renderer.setSize(window.innerWidth, window.innerHeight)
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
}
</script>
