<template>
  <div class ="Main">
  <canvas ref="canvasRef"></canvas>
  <div class="sprint-bar">
    <div class="sprint-bar-inner" :style="sprintBarStyle"></div>
  </div>

    <div class="Calories-Overlay" :style="getBackgroundStyle">
      <div class="overlayContent">
        <img src="@/assets/calories.svg" alt="calories" class="calories-icon" />
        <p v-if="currentCalories<MAXCALORIES">{{ currentCalories }}kcal</p>
        <p v-else>{{ caloriesMessage }}</p>
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import {computed, onMounted, onUnmounted, reactive, ref} from 'vue'
import * as THREE from 'three'
import { Client } from '@stomp/stompjs'
import { Player } from '@/components/Player';
import type { IPlayerDTD } from '@/stores/Player/IPlayerDTD';
import { fetchSnackManFromBackend } from '@/services/SnackManInitService';
import { GameMapRenderer } from '@/renderer/GameMapRenderer';
import { useGameMapStore } from '@/stores/gameMapStore';
import type { IGameMap } from '@/stores/IGameMapDTD';
import type {IFrontendCaloriesMessageEvent} from "@/services/IFrontendMessageEvent";
import { GLTFLoader } from 'three/examples/jsm/Addons.js';
import { useRouter } from 'vue-router';

const WSURL = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/player'
const targetHz = 30

const router = useRouter();

const UPDATE = '/topic/calories'

//Reaktive Calories Variable
const MAXCALORIES = 3000;
const currentCalories  = ref(0);
const caloriesMessage = ref('');
const playerRole = ref('SnackMan'); // Spielerrolle: 'SnackMan' oder 'Geist'

const SNACKMAN_TEXTURE: string = 'src/assets/kirby.glb';
let snackManModel: THREE.Group<THREE.Object3DEventMap>;
// other textures

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

    // If the cooldown is active in the backend and the local state is not yet in cooldown
    if (event.isInCooldown && !sprintData.isCooldown) {
      const usedSprintTime = 5 - event.sprintTimeLeft;
      startCooldownFill(usedSprintTime);
    }

    // When the backend cooldown has ended, but the local state is still in cooldown
    if (!event.isInCooldown && sprintData.isCooldown) {
      stopCooldownFill();
    }

    sprintData.isCooldown = event.isInCooldown;

    player.setPosition(event.posX, event.posY, event.posZ);



  });

// Calories Verarbeitung
stompclient.subscribe(UPDATE, message => {
  const event: IFrontendCaloriesMessageEvent = JSON.parse(message.body);

  if (event.calories !== undefined) {
    currentCalories.value = event.calories;
  }

  // Check win/lose conditions for SnackMan or Ghosts
  if (event.calories >= MAXCALORIES) {
    // Navigate to GameEndView with "SnackMan Wins"
    router.push({
      name: 'GameEnd',
      query: {
        role: playerRole.value,
        result: 'Gewonnen'
      }
    });
  } else if (event.calories < 0) {
    // Navigate to GameEndView with "Ghosts Win"
    router.push({
      name: 'GameEnd',
      query: {
        role: playerRole.value,
        result: 'Verloren'
      }
    });
  }
    });

}


// Kalorien-Overlay Fill berrechnen
const getBackgroundStyle = computed(() => {
  const maxCalories = 3000;
  //Prozent berechnen
  const percentage = Math.min(currentCalories.value / maxCalories, 1);

  const color = `linear-gradient(to right, #EEC643 ${percentage * 100}%, #5E4A08 ${percentage * 100}%)`;

  return {
    background: color
  };
});







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
  fps = 1 / clock.getDelta();
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
      console.log(fehler);
    }
    prevTime = time;
    counter = 0;
  }
  counter++;

  renderer.render(scene, camera);
}

// initially loads the playerModel & attaches playerModel to playerCamera
function loadPlayerModel(texture: string) {
      const loader = new GLTFLoader();
      loader.load(
        texture,
        (gltf) => {
            snackManModel = gltf.scene;

            snackManModel.scale.set(1, 1, 1);
            // rotation in radians (Bogenmaß), 180° doesnt work as intended
            snackManModel.rotation.y = Math.PI;
            // optional offset for thirdPersonView
            // snackManModel.position.set(0, -1.55, -5);
            player.getCamera().add(snackManModel);
        }
      )
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

  loadPlayerModel(SNACKMAN_TEXTURE);

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
let cooldownAnimationFrame: number | null = null;

// Starts the cooldown animation for the sprint bar, filling it dynamically (this function is mostly AI generated)
function startCooldownFill(usedSprintTime: number) {
  if (cooldownAnimationFrame) return; // Prevent starting a new animation if one is already running

  const cooldownDuration = usedSprintTime * 2 * 1000; // Total cooldown duration in ms
  const startTime = performance.now();
  const startValue = sprintData.sprintTimeLeft;
  const fillAmount = 100 - startValue;

  sprintData.isCooldown = true;

  /**
   * Recursive function to animate the cooldown fill using requestAnimationFrame.
   */
  function animateFill() {
    const now = performance.now();
    const elapsed = now - startTime;
    const progress = Math.min(elapsed / cooldownDuration, 1);
    sprintData.sprintTimeLeft = startValue + progress * fillAmount;

    if (progress < 1) {
      // If the animation is not complete, request the next animation frame
      cooldownAnimationFrame = requestAnimationFrame(animateFill);
    } else {
      stopCooldownFill();
      sprintData.isCooldown = false;
      sprintData.sprintTimeLeft = 100;
    }
  }

  cooldownAnimationFrame = requestAnimationFrame(animateFill);
}

// Stops the cooldown fill animation and cleans up the animation frame reference. (this function is mostly AI generated)
function stopCooldownFill() {
  if (cooldownAnimationFrame) {
    cancelAnimationFrame(cooldownAnimationFrame);
    cooldownAnimationFrame = null;
  }
}

const sprintData = reactive({
  sprintTimeLeft: 100, // percentage (0-100)
  isSprinting: false,
  isCooldown: false,
});

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
.Calories-Overlay{
  color: black;
  position: fixed;
  top: 10px;
  right: 10px;
  padding: 10px;
  border-radius: 5px;
  z-index: 10;
  font-size: 25px;
  width: 400px;
  height: 60px;
  display: flex;
  justify-content: left;
}

.overlayContent {
  display: flex;
  align-items: center;
  gap: 16px;
}

.calories-icon {
  width: 30px;
  height: 30px;
}

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