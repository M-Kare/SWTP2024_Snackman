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
import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore';
import type { IPlayerDTD } from '@/stores/Player/IPlayerDTD';
import type {IPlayerClientDTD} from "@/stores/Lobby/IPlayerClientDTD";

const { lobbydata } = useLobbiesStore();
const gameMapStore = useGameMapStore()
gameMapStore.startGameMapLiveUpdate()


const targetHz = 30
let clients: Array<IPlayerClientDTD>;
let playerHashMap = new Map<String, THREE.Mesh>()

const stompclient = gameMapStore.stompclient

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
    const time = performance.now()
    const delta = (time - prevTime) / 1000
    try {
      //Sende and /topic/player/update
      stompclient.publish({
        destination: `/topic/lobbies/${lobbydata.currentPlayer.joinedLobbyId!}/player/update`, headers: {},
        body: JSON.stringify(Object.assign({}, player.getInput(), {jump: player.getIsJumping()},
          {doubleJump: player.getIsDoubleJumping()}, {
            qX: player.getCamera().quaternion.x,
            qY: player.getCamera().quaternion.y,
            qZ: player.getCamera().quaternion.z,
            qW: player.getCamera().quaternion.w
          }, {delta: delta}, {playerId: lobbydata.currentPlayer.playerId})),
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
    await gameMapStore.initGameMap()
    const mapContent = gameMapStore.mapContent
    createGameMap(mapContent as IGameMap)

  } catch (error) {
    console.error('Error when retrieving the gameMap:', error)
  }
    
    clients = lobbydata.lobbies.find((elem)=>elem.lobbyId===lobbydata.currentPlayer.joinedLobbyId)?.members!
    console.log(clients)
    const playerData = await
    fetchSnackManFromBackend(lobbydata.currentPlayer.joinedLobbyId!, lobbydata.currentPlayer.playerId);
    clients.forEach(it => {
      if(it.playerId === lobbydata.currentPlayer.playerId){
        player = new Player(renderer, playerData.posX, playerData.posY, playerData.posZ, playerData.radius, playerData.speed)
      } else {
        let material = new THREE.MeshBasicMaterial( { color: 0x00ff00 } )
        material.color = new THREE.Color(Math.random(), Math.random(), Math.random());
        let cube = new THREE.Mesh( new THREE.BoxGeometry( 1, 3, 1 ),  material);
        cube.position.lerp(new THREE.Vector3(playerData.posX, playerData.posY, playerData.posZ), 0.5)
        scene.add(cube);
        playerHashMap.set(it.playerId, cube);
      }
    });
    
    gameMapStore.setOtherPlayers(playerHashMap)
    gameMapStore.setPlayer(player)
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
