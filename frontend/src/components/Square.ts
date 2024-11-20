import * as THREE from "three"

export  function createSquare(
  xPos: number,
  zPos: number,
  color: string = 'red',
  visible: boolean = true) {

  const BOX_SIZE = 1
  const boxGeometry = new THREE.BoxGeometry(1,0.1, 1)
  const boxMaterial = new THREE.MeshMatcapMaterial({color: color})
  const square = new THREE.Mesh(boxGeometry, boxMaterial)
  square.position.set(xPos-1,0, zPos-1)

  return square
}
