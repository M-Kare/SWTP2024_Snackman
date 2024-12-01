<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import * as THREE from 'three'
import { Client } from '@stomp/stompjs'
import { Player } from '@/components/Player';
import type { IPlayerDTD } from '@/stores/IPlayerDTD';
import { fetchSnackManFromBackend } from '@/services/SnackManInitService';
import {fetchGameMapDataFromBackend} from "@/services/GameMapDataService";
import {GameMapRenderer} from "@/renderer/GameMapRenderer";

const DECELERATION = 20.0
const ACCELERATION = 300.0
const WSURL = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/player'

// stomp
const stompclient = new Client({ brokerURL: WSURL })
stompclient.onWebSocketError = event => {
  //console.log(event)
}
stompclient.onStompError = frame => {
  //console.log(frame)
}
stompclient.onConnect = frame => {
  // Callback: erfolgreicher Verbindugsaufbau zu Broker
  stompclient.subscribe(DEST, message => {
    // Callback: Nachricht auf DEST empfangen
    // empfangene Nutzdaten in message.body abrufbar,
    // ggf. mit JSON.parse(message.body) zu JS konvertieren
    const event: IPlayerDTD = JSON.parse(message.body)
    player.setPosition(event.posX, event.posY, event.posZ);
  })
}
stompclient.activate()

const canvasRef = ref()
let renderer: THREE.WebGLRenderer
let player: Player;
let scene: THREE.Scene
let prevTime = performance.now();

// camera setup
let camera: THREE.PerspectiveCamera;



// used to calculate fps in animate()
const clock = new THREE.Clock();
let fps: number;
let counter = 0;

// is called every frame, changes camera position and velocity
// only sends updates to backend at 30hz
function animate() {
  fps = 1 / clock.getDelta()
  player.updatePlayer();
  if (counter >= fps / 30) {
    console.log(`${player.getCamera().position.x}  |  ${player.getCamera().position.z}`)
    const time = performance.now()
    const delta = (time - prevTime) / 1000
    try {
      //Sende and /topic/player/update
      stompclient.publish({
        destination: DEST + "/update", headers: {},
        body: JSON.stringify(Object.assign({}, player.getInput(), { qX: player.getCamera().quaternion.x, qY: player.getCamera().quaternion.y, qZ: player.getCamera().quaternion.z, qW: player.getCamera().quaternion.w }, { delta: delta }))
      });
    } catch (fehler) {
      console.log(fehler)
    }
    prevTime = time;
    counter = 0
  }
  counter++;
  renderer.render(scene, camera)
}

onMounted(async () => {
// for rendering the scene, create gameMap in 3d and change window size
  const {initRenderer, createGameMap, getScene} = GameMapRenderer()
  scene = getScene()
  renderer = initRenderer(canvasRef.value)

  const playerData = await fetchSnackManFromBackend();
  player = new Player(renderer, playerData.posX, playerData.posY, playerData.posZ, playerData.radius, playerData.speed)
  camera = player.getCamera()
  scene.add(player.getControls().object)

  //Add gameMap
  try {
    const gameMapData = await fetchGameMapDataFromBackend()
    createGameMap(gameMapData)
  } catch (error) {
    console.error('Error when retrieving the gameMap:', error)
  }

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
