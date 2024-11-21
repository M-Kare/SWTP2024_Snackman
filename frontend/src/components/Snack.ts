import * as THREE from "three";
import type {ISnackDTD} from "@/stores/Snack/ISnackDTD";
import {SnackType} from "@/stores/Snack/ISnackDTD";

export function createSnack(snack: ISnackDTD) {
 //default color
  let color = 'blue';

  console.log(snack.snackType)

  if (snack.snackType == SnackType.STRAWBERRY) {
    color = 'purple'
  } else if (snack.snackType == SnackType.ORANGE) {
    color = 'orange'
  } else if (snack.snackType == SnackType.CHERRY) {
    color = 'red'
  }

  const BOX_SIZE = 0.5
  const boxGeometry = new THREE.BoxGeometry(BOX_SIZE, BOX_SIZE, BOX_SIZE)
  const boxMaterial = new THREE.MeshMatcapMaterial({color: color})
  const snackMesh = new THREE.Mesh(boxGeometry, boxMaterial)
  snackMesh.position.set(snack.position.x, BOX_SIZE/2 + 0.1, snack.position.z)

  return snackMesh
}
