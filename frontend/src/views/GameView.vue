<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import * as THREE from 'three'
import { Client } from '@stomp/stompjs'
import { Player } from '@/components/Player';
import type { IPlayerDTD } from '@/stores/IPlayerDTD';
import {fetchMazeDataFromBackend} from "@/services/MazeDataService";
import {MazeRenderer} from "@/renderer/MazeRenderer";
import { GLTFLoader } from 'three/examples/jsm/Addons.js';

const DECELERATION = 20.0
const ACCELERATION = 300.0
const WSURL = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/player'

const SNACKMAN_TEXTURE = 'src/assets/kirby.glb';
const GHOST_TEXTURE = '';

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
    //console.log(`Received: (${event.posX}|${event.posZ})`)
    player.setPosition(event.posX, event.posY, event.posZ);
  })
}
stompclient.activate()

const canvasRef = ref()
let renderer: THREE.WebGLRenderer
let player: Player;
let scene: THREE.Scene

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
    try {
      //Sende and /topic/player/update
      const messageObject = {
        posX: player.getCamera().position.x,
        posY: player.getCamera().position.y,
        posZ: player.getCamera().position.z,
        dirY: player.getCamera().rotation.y,
      };
      stompclient.publish({
        destination: DEST + "/update", headers: {},
        body: JSON.stringify(messageObject)
      });
    } catch (fehler) {
      console.log(fehler)
    }
    counter = 0
  }
  counter++;
  renderer.render(scene, camera)
}

onMounted(async () => {
// for rendering the scene, create maze in 3d and change window size
  const {initRenderer, createMaze, getScene} = MazeRenderer()
  scene = getScene()
  renderer = initRenderer(canvasRef.value)

  player = new Player(renderer, DECELERATION, ACCELERATION)
  camera = player.getCamera()
  scene.add(player.getControls().object)

  const loader = new GLTFLoader();
  loader.load(
    SNACKMAN_TEXTURE,
    (gltf) => {
        const playerModel = gltf.scene;
        playerModel.position.set(10, 0, 10);
        playerModel.scale.set(10, 10, 10);

        scene.add(playerModel);
    },

    // shows progess of loading the playerModel
    xhr => {
      console.log('PlayerModel - ' + ( xhr.loaded / xhr.total * 100 ) + '% loaded' );
    },
    // error while loading playerModel
    error => {
      console.log( 'An error happened' );
    }
  )

  //Add maze
  try {
    const mazeData = await fetchMazeDataFromBackend()
    createMaze(mazeData)
  } catch (error) {
    console.error('Error when retrieving the maze:', error)
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
