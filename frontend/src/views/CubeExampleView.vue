<template>
  <canvas ref="canvasRef"></canvas>
  <!-- <div>
    <h1>Testabfrage</h1>
    <button @click="infoholen">Server-Anfrage</button>
    <p />
    Anfrager-IP-Adresse: {{ anfragerip }}
    <p />
    Server-Zeit: {{ zeitstempel }}
  </div> -->
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import * as THREE from "three"
import { TransformControls } from 'three/addons/controls/TransformControls.js'
import { Client, type Message } from '@stomp/stompjs';

const wsurl = `ws://${window.location.host}/stompbroker`
const DEST = '/topic/coordination'

const stompclient = new Client({ brokerURL: wsurl })
stompclient.onWebSocketError = (event) => { console.log(event) }
stompclient.onStompError = (frame) => { console.log(frame) }
stompclient.onConnect = (frame) => {
  // Callback: erfolgreicher Verbindugsaufbau zu Broker
  stompclient.subscribe(DEST, (message) => {
    // Callback: Nachricht auf DEST empfangen
    // empfangene Nutzdaten in message.body abrufbar,
    // ggf. mit JSON.parse(message.body) zu JS konvertieren
    updateBox(message.body)
    console.log(message.body)
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
    stompclient.publish({
      destination: DEST, headers: {},
      body: `${box.rotation.x};${box.rotation.y}`
    });
  } catch (fehler) {
    console.log(fehler)
  }
  renderer.render(scene, camera)
})

function updateBox(value:string){
  const v =  value.split(";")
  const angleX = parseFloat(v[0])
  const angleY = parseFloat(v[1])
  console.log("angleX:"+angleX)
  console.log("angleY:"+angleY)
  box.rotation.x = angleX
  box.rotation.y = angleY
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





// // Java: record BeispielDTO(String anfragerip, String zeitstempel) {}
// interface IBeispielDTO {
//   anfragerip: string
//   zeitstempel: string
// }

// const anfragerip = ref('???')
// const zeitstempel = ref('??:??')

// async function infoholen() {
//   try {
//     const response = await fetch('/api/getinfo')
//     if (!response.ok) throw new Error(response.statusText)
//     const bspdto: IBeispielDTO = await response.json()

//     anfragerip.value = bspdto.anfragerip
//     zeitstempel.value = bspdto.zeitstempel
//   } catch (e) {
//     anfragerip.value = `ERROR - ${e}`
//     zeitstempel.value = '??:??'
//     alert(e)
//   }
// }
</script>
