<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import {onMounted, onUnmounted, ref} from 'vue'
import * as THREE from 'three'
import { Client } from '@stomp/stompjs'
import { Player } from '@/components/Player';
import { fetchSnackManFromBackend } from '@/services/SnackManInitService';
import { GameMapRenderer } from '@/renderer/GameMapRenderer';
import { useGameMapStore } from '@/stores/gameMapStore';
import type { IGameMap } from '@/stores/IGameMapDTD';
import type { ClientsDTD } from '@/stores/ClientsDTD';
import { useLobbiesStore } from '@/stores/lobbiesstore';
import type { IPlayerDTD } from '@/stores/IPlayerDTD';

const { lobbydata } = useLobbiesStore();


const WSURL = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/player'
const targetHz = 30
let clients: ClientsDTD;
let playerHashMap = new Map<String, THREE.Mesh>()

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
    if(event.uuid === lobbydata.currentPlayer.playerId){
      player.setPosition(event.posX, event.posY, event.posZ);
    } else {
      console.log(event.uuid)
      playerHashMap.get(event.uuid)?.position.set(event.posX, event.posY, event.posZ)
      playerHashMap.get(event.uuid)?.setRotationFromQuaternion(new THREE.Quaternion(event.qX, event.qY, event.qZ, event.qW))
    }
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
        }, {delta: delta}, {uuid: lobbydata.currentPlayer.playerId}))
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

onMounted(async () =>{
// for rendering the scene, create gameMap in 3d and change window size
  const {initRenderer, createGameMap, getScene} = GameMapRenderer()
  scene = getScene()
  renderer = initRenderer(canvasRef.value)

  clients = await fetchClients();

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

  const playerData = await fetchSnackManFromBackend(lobbydata.currentPlayer.playerId);
  clients.clients.forEach(it => {
    if(it === lobbydata.currentPlayer.playerId){
      player = new Player(renderer, playerData.posX, playerData.posY, playerData.posZ, playerData.radius, playerData.speed)
    } else {
      let material = new THREE.MeshBasicMaterial( { color: 0x00ff00 } ) 
      material.color = new THREE.Color(Math.random(), Math.random(), Math.random());
      let cube = new THREE.Mesh( new THREE.BoxGeometry( 1, 3, 1 ),  material);
      cube.position.lerp(new THREE.Vector3(playerData.posX, playerData.posY, playerData.posZ), 0.5)
      scene.add(cube);
      playerHashMap.set(it, cube);
    }
  });
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

async function fetchClients(): Promise<ClientsDTD> {
    // rest endpoint from backend
    const response = await fetch('/api/lobbies/clients')
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    return await response.json()
  }
</script>
