import * as THREE from "three";

export interface IMobUpdateDTD {
  position: THREE.Vector3,
  rotation: THREE.Quaternion,
  radius: number,
  speed: number,
  playerId: string,
  sprintTimeLeft: number,
  isSprinting: boolean,
  isInCooldown: boolean,
}
