import * as THREE from "three"
import type {ISquareDTD} from "@/stores/Square/ISquareDTD";

export function createSquare(square: ISquareDTD,
                             color: string = 'green') {

  const squareGeometry = new THREE.BoxGeometry(square.sideLength, 0.1, square.sideLength)
  const squareMaterial = new THREE.MeshMatcapMaterial({color: color})
  const squareMesh = new THREE.Mesh(squareGeometry, squareMaterial)
  squareMesh.position.set(square.position.x, 0, square.position.z)

  return squareMesh
}
