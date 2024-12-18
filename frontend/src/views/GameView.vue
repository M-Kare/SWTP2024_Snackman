<template>
  <div class ="Main">
  <canvas ref="canvasRef"></canvas>

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
import {computed, onMounted, onUnmounted, ref} from 'vue'
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

const WSURL = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/player'
const targetHz = 30

const UPDATE = '/topic/calories'

//Reaktive Calories Variable
const MAXCALORIES = 3000;
const currentCalories  = ref(0);
const caloriesMessage = ref('');





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
    player.setPosition(event.posX, event.posY, event.posZ);



  });

  // Calories Verarbeitung
  stompclient.subscribe(UPDATE, message => {
    const event: IFrontendCaloriesMessageEvent = JSON.parse(message.body);


    // Get Calories
    if (event.calories !== undefined) {
      currentCalories.value = event.calories;
    }
    if ( event.message) {
      caloriesMessage.value = event.message;
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
        }, {delta: delta}, { jump: player.getIsJumping()}, { doubleJump: player.getIsDoubleJumping()}))
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
  player = new Player(renderer, playerData.posX, playerData.posY, playerData.posZ, playerData.radius, playerData.speed)
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

</style>
