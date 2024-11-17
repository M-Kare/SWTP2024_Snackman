<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import * as THREE from 'three'
import { Client } from '@stomp/stompjs'
import { PointerLockControls } from 'three/addons/controls/PointerLockControls.js'
import type { IFrontendNachrichtEvent } from '@/services/IFrontendNachrichtEvent'
import {addSnacksToScene} from "@/services/SnackService";

const GROUNDSIZE = 1000
const DECELERATION = 20.0
const ACCELERATION = 200.0
const WSURL = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/cube'

// Booleans for checking movement-input
let moveForward = false
let moveBackward = false
let moveLeft = false
let moveRight = false
let canJump = false

let prevTime = performance.now() // time of last render update

const velocity = new THREE.Vector3()
const cameraDirection = new THREE.Vector3()

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
    const event: IFrontendNachrichtEvent = JSON.parse(message.body)
    console.log('ERHALTENES CHANGE: ')
    console.log(event)
  })
}
stompclient.activate()

const canvasRef = ref()
let renderer: THREE.WebGLRenderer
let controls: PointerLockControls
const scene = new THREE.Scene()

// camera setup
const camera = new THREE.PerspectiveCamera(
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

const axesHelper = new THREE.AxesHelper(5);
const gridHelper = new THREE.GridHelper(10, 10);

scene.add(axesHelper);
scene.add(gridHelper);

document.addEventListener('keypress', e => {
  if (e.code === 'KeyD') {
  }
  if (e.code === 'KeyW') {
  }
  if (e.code === 'KeyA') {
  }
  if (e.code === 'KeyS') {
  }
  // try {

  //   //Sende and /topic/cube/update
  //   stompclient.publish({
  //     destination: DEST+"/update", headers: {},
  //     // body: JSON.stringify(cubeData)
  //   });
  // } catch (fehler) {
  //   console.log(fehler)
  // }
  // renderer.render(scene, camera)
})

document.addEventListener('click', function () {
  controls.lock()
})
document.addEventListener('keydown', onKeyDown)
document.addEventListener('keyup', onKeyUp)

function onKeyDown(event: any) {
  switch (event.code) {
    case 'ArrowUp':
    case 'KeyW':
      moveForward = true
      break

    case 'ArrowLeft':
    case 'KeyA':
      moveLeft = true
      break

    case 'ArrowDown':
    case 'KeyS':
      moveBackward = true
      break

    case 'ArrowRight':
    case 'KeyD':
      moveRight = true
      break
  }
}

function onKeyUp(event: any) {
  switch (event.code) {
    case 'ArrowUp':
    case 'KeyW':
      moveForward = false
      break

    case 'ArrowLeft':
    case 'KeyA':
      moveLeft = false
      break

    case 'ArrowDown':
    case 'KeyS':
      moveBackward = false
      break

    case 'ArrowRight':
    case 'KeyD':
      moveRight = false
      break
  }
}

// is called every frame, changes camera position and velocity
function animate() {
  box.position.set(camera.position.x + 3, camera.position.y - 1, camera.position.z)
  console.log(camera.position.y)

  const time = performance.now()
  const delta = (time - prevTime) / 1000

  velocity.x -= velocity.x * DECELERATION * delta
  velocity.z -= velocity.z * DECELERATION * delta
  cameraDirection.z = Number(moveForward) - Number(moveBackward)
  cameraDirection.x = Number(moveRight) - Number(moveLeft)
  cameraDirection.normalize()
  if (moveForward || moveBackward)
    velocity.z -= cameraDirection.z * ACCELERATION * delta
  if (moveLeft || moveRight)
    velocity.x -= cameraDirection.x * ACCELERATION * delta
  controls.moveRight(-velocity.x * delta)
  controls.moveForward(-velocity.z * delta)
  prevTime = time
  renderer.render(scene, camera)
}

//Examplebox
const BOX_SIZE = 1
const boxGeometry = new THREE.BoxGeometry(BOX_SIZE, BOX_SIZE, BOX_SIZE)
const boxMaterial = new THREE.MeshMatcapMaterial({color: 'green'})
const box = new THREE.Mesh(boxGeometry, boxMaterial)
box.position.set(camera.position.x,camera.position.y,camera.position.z)
scene.add(box)

onMounted(async () => {
  await addSnacksToScene(scene)

  renderer = new THREE.WebGLRenderer({
    canvas: canvasRef.value,
    alpha: true,
    antialias: true,
  })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(window.devicePixelRatio)

  renderer.shadowMap.enabled = true

  controls = new PointerLockControls(camera, renderer.domElement)
  scene.add(controls.object)

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
