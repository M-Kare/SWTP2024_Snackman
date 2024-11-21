import type {ISquareDTD} from "@/stores/Square/ISquareDTD";
import {getSquare} from "@/api/Square";
import * as THREE from "three";
import {createSquare} from "@/components/Square";
import {createSnack} from "@/components/Snack";


export async function addSquareAndSnackToScene(scene: THREE.Scene){
  const squareData : ISquareDTD  = await getSquare()
  console.log(squareData)

  const square = createSquare(squareData)
  scene.add(square)

  squareData.snacks.forEach(snack => {
    scene.add(createSnack(snack))
  })
}

