<template>
  <canvas ref="canvasRef"></canvas>
  <div class="sprint-bar">
    <div class="sprint-bar-inner" :style="sprintBarStyle"></div>
  </div>
</template>

<script setup lang="ts">
import {computed, onMounted, onUnmounted, reactive, ref} from 'vue'
import * as THREE from 'three'
import {Client} from '@stomp/stompjs'
import {Player} from '@/components/Player';
import type {IPlayerDTD} from '@/stores/Player/IPlayerDTD';
import {fetchSnackManFromBackend} from '@/services/SnackManInitService';
import {GameMapRenderer} from "@/renderer/GameMapRenderer";
import {useGameMapStore} from '@/stores/gameMapStore'
import type {IGameMap} from "@/stores/IGameMapDTD";

const WSURL = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/player'
const targetHz = 30

// stomp
const stompclient = new Client({brokerURL: WSURL})
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

    sprintData.sprintTimeLeft = (event.sprintTimeLeft / 5) * 100;
    sprintData.isSprinting = event.isSprinting;
    sprintData.isCooldown = event.isInCooldown;

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
  if (counter >= fps / targetHz) {
    // console.log(`${player.getCamera().position.x}  |  ${player.getCamera().position.z}`)
    const time = performance.now()
    const delta = (time - prevTime) / 1000
    try {
      //Sende and /topic/player/update
      stompclient.publish({
        destination: DEST + "/update", headers: {},
        body: JSON.stringify(Object.assign({}, player.getInput(), {
          qX: player.getCamera().quaternion.x,
          qY: player.getCamera().quaternion.y,
          qZ: player.getCamera().quaternion.z,
          qW: player.getCamera().quaternion.w
        }, {delta: delta}, { jump: player.getIsJumping()}, { doubleJump: player.getIsDoubleJumping()}, {sprinting: player.isSprinting}))
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

  //Add gameMap
  try {
    const gameMapStore = useGameMapStore()
    await gameMapStore.initGameMap()

    const mapContent = gameMapStore.mapContent
    createGameMap(mapContent as IGameMap)

    await gameMapStore.startGameMapLiveUpdate()
  } catch (error) {
    console.error('Error when retrieving the gameMap:', error)
  }

  const playerData = await fetchSnackManFromBackend();
  player = new Player(renderer, playerData.posX, playerData.posY, playerData.posZ, playerData.radius, playerData.speed, playerData.baseSpeed, playerData.sprintMultiplier)
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

// SPRINT-BAR 
const sprintData = reactive({
  sprintTimeLeft: 100, // percentage (0-100)
  isSprinting: false,
  isCooldown: false,
  cooldownDuration: 0, // Total cooldown duration in seconds
});

// Computed style for sprint bar
const sprintBarStyle = computed(() => {
  let color = 'green';
  if (sprintData.isSprinting) {
    color = 'red';
  } else if (sprintData.isCooldown) {
    color = 'blue';
  }

  return {
    width: `${sprintData.sprintTimeLeft}%`,
    backgroundColor: color,
  };
});
</script>

<style>
.sprint-bar {
  position: absolute;
  bottom: 3vh;
  right: 3vh;
  width: 25rem;
  height: 2.5rem;
  background-color: #ccc;
  border: 0.25rem solid #000;
  border-radius: 0.5rem;
  overflow: hidden;
}

.sprint-bar-inner {
  height: 100%;
  transition: width 0.1s ease-out, background-color 0.2s ease-out;
}
</style>