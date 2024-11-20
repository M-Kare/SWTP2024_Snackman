import type {ISquareDTD} from "@/stores/Square/ISquareDTD";
import {getSquare} from "@/api/Square";
import * as THREE from "three";
import {createSquare} from "@/components/Square";


export async function addSquareToScene(scene: THREE.Scene){
  const squareData : ISquareDTD  = await getSquare()
  console.log(squareData)

  scene.add(createSquare(squareData.widthX ,squareData.depthZ ))


}

