import type {ISquareDTD} from "@/stores/Square/ISquareDTD";
import type {ISnackDTD} from "@/stores/Snack/ISnackDTD";
import {SnackType} from "@/stores/Snack/ISnackDTD";
import {getSquare} from "@/api/Square";
import {removeSnack} from "@/api/Snack";
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

//NEW
let snackData: ISnackDTD;

export async function removeSnackFromScene(snackToRemove: THREE.Mesh, scene: THREE.Scene) {
  if (snackToRemove) {
    let snackFound = false;
    scene.children.forEach(child => {
      if (child instanceof THREE.Mesh && child.position.equals(snackToRemove.position)) {
        snackData = convertMeshToSnack(snackToRemove);
        child.removeFromParent();
        console.log(`Snack removed`);
        snackFound = true;
      }
    });
    if (!snackFound) {
      console.log(`Snack not found in the scene`);
    }
  } else {
    console.log(`Invalid snack provided`);
  }

  const response = await removeSnack(snackData)
  if (response) {
    console.log('Snack removed from backend.');
  } else {
    console.log('Snack could not be removed.');
  }
}

function convertMeshToSnack(mesh: THREE.Mesh): ISnackDTD {
  const position = {
    x: mesh.position.x,
    z: mesh.position.z,
  };
  const snackType = SnackType.CHERRY;
  const height = 1;
  return { position, snackType, height };
}
//NEW
