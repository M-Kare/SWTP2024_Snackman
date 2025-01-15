import * as THREE from "three";

export function initSnackEatingSound(camera: THREE.Camera): THREE.PositionalAudio {
  const listener = new THREE.AudioListener();
  camera.add(listener)

  const sound = new THREE.PositionalAudio(listener)
  const audioLoader = new THREE.AudioLoader();

  audioLoader.load('src/assets/sounds/eatingSounds/collect_snack_sound.mp3', function (buffer) {
    sound.setBuffer(buffer)
    sound.setRefDistance(20)
  })

  return sound
}
