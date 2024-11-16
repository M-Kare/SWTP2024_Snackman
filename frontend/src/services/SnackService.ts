import {type ISnackDTD, SnackType} from "@/stores/Snack/ISnackDTD";
import {getSnacks} from "@/api/Snack";
import * as THREE from "three";
import {createSnack} from "@/components/Snack";

export async function addSnacksToScene(scene: THREE.Scene) {
  const snackData: Array<ISnackDTD> = await getSnacks()
  const snackMap: Map<number, THREE.Mesh> = new Map();

  snackData.forEach((snack: ISnackDTD) => {
    if (snack.snackType == SnackType.STRAWBERRY) {
      const createdSnack = createSnack(snack.position.x, snack.position.y, snack.position.z, 'purple')
      snackMap.set(snack.id, createdSnack)
      scene.add(createdSnack)
    } else if (snack.snackType == SnackType.ORANGE) {
      const createdSnack = createSnack(snack.position.x, snack.position.y, snack.position.z, 'orange')
      snackMap.set(snack.id, createdSnack)
      scene.add(createdSnack)
    } else {
      const createdSnack = createSnack(snack.position.x, snack.position.y, snack.position.z)
      snackMap.set(snack.id, createdSnack)
      scene.add(createdSnack)
    }
  })

  //entfernt cube.
  snackMap.get(3)?.removeFromParent()

  console.log(snackData)
}
