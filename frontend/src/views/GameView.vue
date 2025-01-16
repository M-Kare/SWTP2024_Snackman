<template>
  <div class="Main">
    <canvas ref="canvasRef"></canvas>
    <div class="sprint-bar" v-if="lobbydata.currentPlayer.role == 'SNACKMAN'">
      <div :style="sprintBarStyle" class="sprint-bar-inner"></div>
    </div>

    <div :style="getBackgroundStyle" class="Calories-Overlay" v-if="lobbydata.currentPlayer.role == 'SNACKMAN'">
      <div class="overlayContent">
        <img alt="calories" class="calories-icon" src="@/assets/calories.svg"/>
        <p v-if="currentCalories < MAX_CALORIES">{{ currentCalories }} kcal</p>
        <p v-else>{{ caloriesMessage }}</p>
      </div>
    </div>

    <div class="time">
      <img alt="clock" class="clock-icon" src="@/assets/clock-icon.svg" />
      <p>{{ formattedTime }}</p>
    </div>

  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, onUnmounted, reactive, ref, watch} from 'vue'
import * as THREE from 'three'
import {Player} from '@/components/Player';
import {fetchSnackManFromBackend} from '@/services/SnackManInitService';
import {GameMapRenderer} from '@/renderer/GameMapRenderer';
import {useGameMapStore} from '@/stores/gameMapStore';
import type {IGameMap} from '@/stores/IGameMapDTD';
import {useLobbiesStore} from '@/stores/Lobby/lobbiesstore';
import type {IPlayerClientDTD} from "@/stores/Lobby/IPlayerClientDTD";
import {GLTFLoader} from 'three/examples/jsm/Addons.js'
import {useRoute} from 'vue-router';
import type {IPlayerDTD} from '@/stores/Player/IPlayerDTD';

const { lobbydata } = useLobbiesStore();
const gameMapStore = useGameMapStore()
gameMapStore.startGameMapLiveUpdate()

const targetHz = 30
let clients: Array<IPlayerClientDTD>;
let playerHashMap = new Map<String, THREE.Group<THREE.Object3DEventMap>>()

const route = useRoute();
const stompclient = gameMapStore.stompclient
let playerData: IPlayerDTD

//Reaktive Calories Variable
const MAX_CALORIES = ref(20000)
let currentCalories = ref()
let caloriesMessage = ref('')
const playerRole = ref(route.query.role || ''); // Player role from the URL query

const SNACKMAN_TEXTURE: string = '/kirby.glb'
let snackManModel: THREE.Group<THREE.Object3DEventMap>

const canvasRef = ref()
let renderer: THREE.WebGLRenderer
let player: Player
let scene: THREE.Scene
let prevTime = performance.now()

const sprintData = reactive({
  sprintTimeLeft: 100, // percentage (0-100)
  isSprinting: false,
  isCooldown: false,
})

// camera setup
let camera: THREE.PerspectiveCamera

// used to calculate fps in animate()
const clock = new THREE.Clock()
let fps: number
let counter = 0

// is called every frame, changes camera position and velocity
// only sends updates to backend at 30hz
function animate() {
  currentCalories.value = player.getCalories()
  fps = 1 / clock.getDelta()
  player.updatePlayer()

  if (counter >= fps / targetHz) {
    const time = performance.now()
    const delta = (time - prevTime) / 1000
    try {
      //Sende and /topic/player/update
      stompclient.publish({
        destination: `/topic/lobbies/${lobbydata.currentPlayer.joinedLobbyId!}/player/update`, headers: {},
        body: JSON.stringify(Object.assign({}, player.getInput(), {jump: player.getIsJumping()},
          {doubleJump: player.getIsDoubleJumping()},
          {sprinting: player.isSprinting},
          {
            qX: player.getCamera().quaternion.x,
            qY: player.getCamera().quaternion.y,
            qZ: player.getCamera().quaternion.z,
            qW: player.getCamera().quaternion.w
          }, {delta: delta}, {playerId: lobbydata.currentPlayer.playerId})),
      });
    } catch (fehler) {
      console.error(fehler)
    }
    prevTime = time
    counter = 0
  }
  counter++;

  renderer.render(scene, camera)
}

onMounted(async () => {
  startCountDown()
  console.log(formattedTime)

  // for rendering the scene, create gameMap in 3d and change window size
  const {initRenderer, createGameMap, getScene} = GameMapRenderer()
  scene = getScene()
  renderer = initRenderer(canvasRef.value)
  //Add gameMap
  try {
    await gameMapStore.initGameMap()
    const mapContent = gameMapStore.mapContent
    createGameMap(mapContent as IGameMap)

  } catch (error) {
    console.error('Error when retrieving the gameMap:', error)
  }

  clients = lobbydata.lobbies.find((elem) => elem.lobbyId === lobbydata.currentPlayer.joinedLobbyId)?.members!
  console.log(clients)
  playerData = await fetchSnackManFromBackend(lobbydata.currentPlayer.joinedLobbyId!, lobbydata.currentPlayer.playerId);
  MAX_CALORIES.value = playerData.maxCalories

  clients.forEach(it => {
    if (it.playerId === lobbydata.currentPlayer.playerId) {
      // that's you
      player = new Player(renderer, playerData.posX, playerData.posY, playerData.posZ, playerData.radius,
        playerData.speed, playerData.sprintMultiplier)
    } else {
      // other players that are not you
      loadPlayerModel(it.playerId, SNACKMAN_TEXTURE);
    }
  });
  gameMapStore.setOtherPlayers(playerHashMap)
  gameMapStore.setPlayer(player)
  caloriesMessage = player.message

  watch(player.sprintData, (newSprintData) => {
    sprintData.isSprinting = player.sprintData.isSprinting
    sprintData.sprintTimeLeft = (player.sprintData.sprintTimeLeft / 5) * 100

    if (player.sprintData.isCooldown && !sprintData.isCooldown) {
      const usedSprintTime = 5 - player.sprintData.sprintTimeLeft
      startCooldownFill(usedSprintTime)
    }

    // When the backend cooldown has ended, but the local state is still in cooldown
    if (!player.sprintData.isCooldown && sprintData.isCooldown) {
      stopCooldownFill()
    }
    sprintData.isCooldown = player.sprintData.isCooldown
  })

  camera = player.getCamera()
  scene.add(player.getControls().object)

  renderer.render(scene, camera)
  renderer.setAnimationLoop(animate)
  window.addEventListener('resize', resizeCallback)
})

// initially loads the playerModel & attaches playerModel to playerCamera
async function loadPlayerModel(playerId: string, texture: string) {
  const loader = new GLTFLoader()
  loader.load(texture, gltf => {
    snackManModel = gltf.scene
    snackManModel.scale.set(1, 1, 1)
    scene.add(snackManModel)
    snackManModel.position.lerp(new THREE.Vector3(playerData.posX, playerData.posY - 2, playerData.posZ), 0.5)
    playerHashMap.set(playerId, snackManModel);
    // optional offset for thirdPersonView
    // snackManModel.position.set(0, -1.55, -5);
    // player.getCamera().add(snackManModel)
  })
}

onUnmounted(() => {
  renderer.setAnimationLoop(null)
})

function resizeCallback() {
  renderer.setSize(window.innerWidth, window.innerHeight)
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
}

// SPRINT-BAR
let cooldownAnimationFrame: number | null = null

// Starts the cooldown animation for the sprint bar, filling it dynamically (this function is mostly AI generated)
function startCooldownFill(usedSprintTime: number) {
  if (cooldownAnimationFrame) return // Prevent starting a new animation if one is already running

  const cooldownDuration = usedSprintTime * 2 * 1000 // Total cooldown duration in ms
  const startTime = performance.now()
  const startValue = sprintData.sprintTimeLeft
  const fillAmount = 100 - startValue

  sprintData.isCooldown = true

  /**
   * Recursive function to animate the cooldown fill using requestAnimationFrame.
   */
  function animateFill() {
    const now = performance.now()
    const elapsed = now - startTime
    const progress = Math.min(elapsed / cooldownDuration, 1)
    sprintData.sprintTimeLeft = startValue + progress * fillAmount

    if (progress < 1) {
      // If the animation is not complete, request the next animation frame
      cooldownAnimationFrame = requestAnimationFrame(animateFill)
    } else {
      stopCooldownFill()
      sprintData.isCooldown = false
      sprintData.sprintTimeLeft = 100
    }
  }

  cooldownAnimationFrame = requestAnimationFrame(animateFill)
}

// Stops the cooldown fill animation and cleans up the animation frame reference. (this function is mostly AI generated)
function stopCooldownFill() {
  if (cooldownAnimationFrame) {
    cancelAnimationFrame(cooldownAnimationFrame)
    cooldownAnimationFrame = null
  }
}

// Kalorien-Overlay Fill berrechnen
const getBackgroundStyle = computed(() => {
  //Prozent berechnen
  const percentage = Math.min(currentCalories.value / MAX_CALORIES.value, 1)

  const color = `linear-gradient(to right, #EEC643 ${percentage * 100}%, #5E4A08 ${percentage * 100}%)`

  return {
    background: color,
  }
})

const sprintBarStyle = computed(() => {
  let color = 'green'
  if (sprintData.isSprinting) {
    color = 'red'
  } else if (sprintData.isCooldown) {
    color = 'blue'
  }
  return {
    width: `${sprintData.sprintTimeLeft}%`,
    backgroundColor: color,
  }
})

const countdownTime = ref(300) // 5 Minute in seconds
const lobbyId = ref(route.query.lobbyId || '');

/**
 * Get Current Playing Time From Backend
 */
const getCurrentPlayingTime = async () => {
  try {
    const url = `/api/lobby/${lobbyId.value}/current-playing-time`
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const currentPlayingTime = await response.json();
    console.log('Current Playing Time:', currentPlayingTime);
    return currentPlayingTime;

    } catch (error: any) {
      console.error('Error:', error)
      return null
    }
}
   
const formattedTime = computed(() => {
  const minutes = Math.floor(countdownTime.value / 60)
  const seconds = countdownTime.value % 60
  return `${minutes < 10 ? '0' : ''}${minutes}:${seconds < 10 ? '0' : ''}${seconds}`
})

const startCountDown = async () => {
  const currentTime = await getCurrentPlayingTime();
  if (currentTime !== null) {
    countdownTime.value = Math.floor(currentTime / 1000); // Chuyển từ ms sang giây
  } else {
    console.error('Failed to fetch current playing time. Using default value.');
  }

  const interval = setInterval(() => {
    if (countdownTime.value > 0) {
      countdownTime.value -= 1;
    } else {
      clearInterval(interval); // Stop when countdown reaches 0
    }
  }, 1000)
}

</script>

<style>
.time {
  position: absolute;
  color: black;
  font-weight: bold;
  top: 3vh;
  right: 3vh;
  font-size: 40px;
  z-index: 10;
  display: flex;
  width: 400px;
  height: 60px;
  gap: 10px;
  justify-content: right;
  align-items: center;
}

.time p {
  margin: 0;
  line-height: 1;
}

.clock-icon {
  width: 40px;
  height: 40px;
  filter: grayscale(100%);
}

.Calories-Overlay {
  color: black;
  position: fixed;
  top: 3vh;
  left: 3vh;
  padding: 10px;
  border-radius: 5px;
  z-index: 10;
  font-size: 25px;
  width: 400px;
  height: 60px;
  display: flex;
  justify-content: left;
  align-items: center; 
  box-shadow: 7px 7px 0 rgba(0, 0, 0, 0.8);
}

.overlayContent {
  display: flex;
  align-items: center;
  gap: 16px;
}

.overlayContent p {
  margin: 0;
  line-height: 1;
}

.calories-icon {
  width: 30px;
  height: 30px;
}

.sprint-bar {
  position: absolute;
  z-index: 10;
  bottom: 3vh;
  left: 3vh;
  width: 25rem;
  height: 2.5rem;
  background-color: #ccc;
  border: 0.25rem solid #000;
  border-radius: 0.5rem;
  overflow: hidden;
  box-shadow: 7px 7px 0 rgba(0, 0, 0, 0.8);
}

.sprint-bar-inner {
  height: 100%;
  transition: width 0.1s ease-out, background-color 0.2s ease-out;
}
</style>
