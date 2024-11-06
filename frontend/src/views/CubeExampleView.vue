<template>
  <canvas ref="canvasRef"></canvas>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as THREE from "three"
import { TransformControls } from 'three/addons/controls/TransformControls.js'
import { Client } from '@stomp/stompjs';
import type {IFrontendNachrichtEvent} from "@/services/IFrontendNachrichtEvent";

const wsurl = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/cube'

const stompclient = new Client({ brokerURL: wsurl })
stompclient.onWebSocketError = (event) => { console.log(event) }
stompclient.onStompError = (frame) => { console.log(frame) }
stompclient.onConnect = (frame) => {
  // Callback: erfolgreicher Verbindugsaufbau zu Broker
  stompclient.subscribe (DEST, (message) => {
    // Callback: Nachricht auf DEST empfangen
    // empfangene Nutzdaten in message.body abrufbar,
    // ggf. mit JSON.parse(message.body) zu JS konvertieren
    const event: IFrontendNachrichtEvent = JSON.parse(message.body)
    console.log("ERHALTENES CHANGE: ")
    console.log(event)
    updateBox(event)
  });
};
stompclient.activate()


const canvasRef = ref()
let renderer: THREE.WebGLRenderer;
let controls: TransformControls
const scene = new THREE.Scene()

const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 100)
camera.position.set(0, 2, 10)
camera.lookAt(0, 0, 0)
scene.add(camera)

const light = new THREE.DirectionalLight()
light.position.set(0, 10, 10)
light.castShadow = true
scene.add(light)


const boxGeometry = new THREE.BoxGeometry(1, 1, 1)
const boxMaterial = new THREE.MeshMatcapMaterial({ color: "blue" })
const box = new THREE.Mesh(boxGeometry, boxMaterial)
box.castShadow = true
box.receiveShadow = true
scene.add(box)


document.addEventListener("keypress", (e) => {
  const angle = 0.1
  if (e.code === "KeyD") {
    box.rotation.y += angle
  }
  if (e.code === "KeyW") {
    box.rotation.x -= angle
  }
  if (e.code === "KeyA") {
    box.rotation.y -= angle
  }
  if (e.code === "KeyS") {
    box.rotation.x += angle
  }
  try {
    const cubeData = {
      width: box.geometry.parameters.width,
      height: box.geometry.parameters.height,
      depth: box.geometry.parameters.depth,
      rotationAngleX: box.rotation.x,
      rotationAngleY: box.rotation.y
    };

    //Sende and /topic/cube/update
    stompclient.publish({
      destination: DEST+"/update", headers: {},
      body: JSON.stringify(cubeData)
    });
  } catch (fehler) {
    console.log(fehler)
  }
  renderer.render(scene, camera)
})

function updateBox(event : IFrontendNachrichtEvent){
  box.rotation.x = event.cubeDTO.rotationAngleX
  box.rotation.y = event.cubeDTO.rotationAngleY
  renderer.render(scene, camera)
}

onMounted(() => {
  renderer = new THREE.WebGLRenderer({
    canvas: canvasRef.value,
    alpha: true,
    antialias: true,
  })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(window.devicePixelRatio)

  renderer.shadowMap.enabled = true

  renderer.render(scene, camera)
  window.addEventListener("resize", resizeCallback)
})

function resizeCallback(){
        renderer.setSize(window.innerWidth, window.innerHeight)
        camera.aspect = window.innerWidth/window.innerHeight
        camera.updateProjectionMatrix()
    }
</script>
