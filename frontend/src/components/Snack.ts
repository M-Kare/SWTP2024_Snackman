import * as THREE from "three";

export function createSnack(xPos: number,
                            yPos: number,
                            zPos: number,
                            color: string = 'red',
                            visible: boolean = true) {

  const BOX_SIZE = 1
  const boxGeometry = new THREE.BoxGeometry(BOX_SIZE, BOX_SIZE, BOX_SIZE)
  const boxMaterial = new THREE.MeshMatcapMaterial({color: color})
  const snack = new THREE.Mesh(boxGeometry, boxMaterial)
  snack.position.set(xPos, yPos, zPos)

  return snack
}
