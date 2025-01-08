import {defineStore} from 'pinia';
import {reactive, readonly} from "vue";
import type {IGameMap, IGameMapDTD} from './IGameMapDTD';
import {fetchGameMapDataFromBackend} from "../services/GameMapDataService.js";
import {Client} from "@stomp/stompjs";
import type {
  IFrontendChickenMessageEvent,
  IFrontendGhostMessageEvent,
  IFrontendMessageEvent, IFrontendScriptGhostMessageEvent
} from "@/services/IFrontendMessageEvent";
import type {ISquare} from "@/stores/Square/ISquareDTD";
import * as THREE from "three";
import {Scene} from "three";
import type {IChicken, IChickenDTD} from "@/stores/Chicken/IChickenDTD";
import {Direction} from "@/stores/Chicken/IChickenDTD";
import {GameObjectRenderer} from "@/renderer/GameObjectRenderer";
import type {IGhost, IGhostDTD} from "@/stores/Ghost/IGhostDTD";
import type {IScriptGhost, IScriptGhostDTD} from "@/stores/Ghost/IScriptGhostDTD";
import type { Player } from '@/components/Player';

/**
 * Defines the pinia store used for saving the map from
 * the backend. Updates of the snacks are saved here and
 * updated in the store itself. It holds the scene on
 * which the game is build up on.
 */
export const useGameMapStore = defineStore('gameMap', () => {
  let snackStompclient: Client
  let chickenStompclient: Client
  let ghostStompclient: Client
  let scriptGhostStompclient: Client
  const scene = new THREE.Scene()
  const gameObjectRenderer = GameObjectRenderer()
  const CHICKEN_MOVEMENT_SPEED = 0.1    // step size of the interpolation: between 0 and 1

  const mapData = reactive({
    DEFAULT_SQUARE_SIDE_LENGTH: 0,
    DEFAULT_WALL_HEIGHT: 0,
    gameMap: new Map<number, ISquare>(),
    chickens: [],
    ghosts: [],
    scriptGhosts: []
  } as IGameMap);

  async function initGameMap() {
    try {
      const response: IGameMapDTD = await fetchGameMapDataFromBackend()
      mapData.DEFAULT_SQUARE_SIDE_LENGTH = response.DEFAULT_SQUARE_SIDE_LENGTH
      mapData.DEFAULT_WALL_HEIGHT = response.DEFAULT_WALL_HEIGHT

      for (const square of response.gameMap) {
        mapData.gameMap.set(square.id, square as ISquare)
      }

      for (const chicken of response.chickens) {
        mapData.chickens.push(chicken as IChicken)
      }

      for (const ghost of response.ghosts) {
        mapData.ghosts.push(ghost as IGhost)
      }

      for (const ghost of response.scriptGhosts) {
        mapData.scriptGhosts.push(ghost as IScriptGhost)
      }
    } catch (reason) {
      throw reason //Throw again to pass to execution function
    }
  }

  async function startGameMapLiveUpdate() {
    const protocol = window.location.protocol.replace('http', 'ws')
    const wsurl = `${protocol}//${window.location.host}/stompbroker`
    const DEST_SQUARE = '/topic/square'
    const DEST_CHICKEN = '/topic/chicken'
    const DEST_GHOST = '/topic/ghost'
    const DEST_SCRIPT_GHOST = '/topic/scriptGhost'

    if (!snackStompclient) {
      snackStompclient = new Client({brokerURL: wsurl})

      snackStompclient.onWebSocketError = (event) => {
        throw new Error('Websocket wit message: ' + event)
      }

      snackStompclient.onStompError = (frameElement) => {
        throw new Error('Stompclient with message: ' + frameElement)
      }

      snackStompclient.onConnect = (frameElement) => {
        console.log('Stompclient connected')

        snackStompclient.subscribe(DEST_SQUARE, async (message) => {
          const change: IFrontendMessageEvent = JSON.parse(message.body)

          const savedMeshId = mapData.gameMap.get(change.square.id)!.snack.meshId

          removeMeshFromScene(scene, savedMeshId)

          mapData.gameMap.set(change.square.id, change.square as ISquare)
        })
      }

      snackStompclient.onDisconnect = () => {
        console.log('Stompclient disconnected.')
      }

      snackStompclient.activate()
    }

    if (!chickenStompclient) {
      chickenStompclient = new Client({brokerURL: wsurl})

      chickenStompclient.onWebSocketError = (event) => {
        throw new Error('Chicken Websocket with message: ' + event)
      }

      chickenStompclient.onStompError = (frameElement) => {
        throw new Error('Chicken Stompclient with message: ' + frameElement)
      }

      chickenStompclient.onConnect = (frameElement) => {
        console.log('Stompclient for chicken connected')

        chickenStompclient.subscribe(DEST_CHICKEN, async (message) => {
          const change: IFrontendChickenMessageEvent = JSON.parse(message.body)
          console.log("Received a chicken update: {}", change)

          const chickenUpdate: IChickenDTD = change.chicken
          const OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH / 2
          const DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH
          const currentChicken = mapData.chickens.find(chicken => chicken.id == chickenUpdate.id)
          console.log("chicken update {}", chickenUpdate)

          if (currentChicken == undefined) {
            console.error("A chicken is undefined in pinia")
          } else {
            if (currentChicken.thickness != chickenUpdate.thickness) {
              updateThickness(currentChicken, chickenUpdate)
            }
            if (chickenUpdate.chickenPosX == currentChicken!.chickenPosX && chickenUpdate.chickenPosZ == currentChicken!.chickenPosZ) {
              updateLookingDirectionChicken(currentChicken, chickenUpdate)
            } else {
              updateWalkingDirectionChicken(currentChicken, chickenUpdate, DEFAULT_SIDE_LENGTH, OFFSET)
            }
          }
        })
      }

      chickenStompclient.onDisconnect = () => {
        console.log('Chicken Stompclient disconnected.')
      }

      chickenStompclient.activate()
    }

    if (!ghostStompclient) {
      ghostStompclient = new Client({brokerURL: wsurl})

      ghostStompclient.onWebSocketError = (event) => {
        throw new Error('Ghost Websocket with message: ' + event)
      }

      ghostStompclient.onStompError = (frameElement) => {
        throw new Error('Ghost Stompclient with message: ' + frameElement)
      }

      ghostStompclient.onConnect = () => {
        console.log('Stompclient for ghost connected')

        ghostStompclient.subscribe(DEST_GHOST, async (message) => {
          const change: IFrontendGhostMessageEvent = JSON.parse(message.body)
          console.log("Received a ghost update: {}", change)

          // todo only display ghosts which are not you -> fix when lobby ready

          const ghostUpdate: IGhostDTD = change.ghost
          const OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH / 2
          const DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH
          const currentGhost = mapData.ghosts.find(ghost => ghost.id == ghostUpdate.id)
          console.log("ghost update {}", ghostUpdate)

          if (currentGhost == undefined) {
            console.error("A ghost is undefined in pinia")
          } else {
            updateGhost(currentGhost, ghostUpdate, DEFAULT_SIDE_LENGTH, OFFSET)
          }
        })
      }

      ghostStompclient.onDisconnect = () => {
        console.log('Chicken Stompclient disconnected.')
      }

      ghostStompclient.activate()
    }

    if (!scriptGhostStompclient) {
      scriptGhostStompclient = new Client({brokerURL: wsurl})

      scriptGhostStompclient.onWebSocketError = (event) => {
        throw new Error('Script ghost Websocket with message: ' + event)
      }

      scriptGhostStompclient.onStompError = (frameElement) => {
        throw new Error('Script ghost Stompclient with message: ' + frameElement)
      }

      scriptGhostStompclient.onConnect = () => {
        console.log('Stompclient for script ghost connected')

        scriptGhostStompclient.subscribe(DEST_SCRIPT_GHOST, async (message) => {
          const change: IFrontendScriptGhostMessageEvent = JSON.parse(message.body)
          console.log("Received a script ghost update: {} ghost: {}", change, change.scriptGhost)

          const ghostUpdate: IScriptGhostDTD = change.scriptGhost
          const OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH / 2
          const DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH
          const currentScriptGhost = mapData.scriptGhosts.find(ghost => ghost.id == ghostUpdate.id)
          console.log("script ghost update {}", ghostUpdate)

          if (currentScriptGhost == undefined) {
            console.error("A script ghost is undefined in pinia")
          } else {
            if (ghostUpdate.ghostPosX == currentScriptGhost!.ghostPosX && ghostUpdate.ghostPosZ == currentScriptGhost!.ghostPosZ) {
              updateLookingDirectionScriptGhost(currentScriptGhost, ghostUpdate)
            } else {
              updateWalkingDirectionScriptGhost(currentScriptGhost, ghostUpdate, DEFAULT_SIDE_LENGTH, OFFSET)
            }
          }
        })
      }

      scriptGhostStompclient.onDisconnect = () => {
        console.log('Scriptghost Stompclient disconnected.')
      }

      scriptGhostStompclient.activate()
    }
  }

  function updateThickness(currentChicken: IChicken, chickenUpdate: IChickenDTD) {
    console.log("Chicken thickness updated")
    const chickenMesh = scene.getObjectById(currentChicken.meshId)
    currentChicken.thickness = chickenUpdate.thickness

    // todo update chicken thickness with new geometry
  }

  function updateLookingDirectionChicken(currentChicken: IChicken, chickenUpdate: IChickenDTD) {
    console.log("Chicken looking direction updated")
    const chickenMesh = scene.getObjectById(currentChicken.meshId)

    currentChicken.lookingDirection = chickenUpdate.lookingDirection
    switch (currentChicken.lookingDirection) {    // rotates the chicken depending on what its looking direction is
      case Direction.NORTH || Direction.SOUTH:
        chickenMesh!.setRotationFromEuler(new THREE.Euler(0))
        break;
      case Direction.EAST || Direction.WEST:
        chickenMesh!.setRotationFromEuler(new THREE.Euler(Math.PI / 2))
        break;
    }
  }

  function updateLookingDirectionScriptGhost(currentScriptGhost: IScriptGhost, scriptGhostUpdate: IScriptGhostDTD) {
    console.log("ScriptGhost looking direction updated")
    const scriptGhostMesh = scene.getObjectById(currentScriptGhost.meshId)

    currentScriptGhost.lookingDirection = scriptGhostUpdate.lookingDirection
    switch (currentScriptGhost.lookingDirection) {
      case Direction.NORTH || Direction.SOUTH:
        scriptGhostMesh!.setRotationFromEuler(new THREE.Euler(0))
        break;
      case Direction.EAST || Direction.WEST:
        scriptGhostMesh!.setRotationFromEuler(new THREE.Euler(Math.PI / 2))
        break;
    }
  }

  function updateWalkingDirectionChicken(currentChicken: IChicken, chickenUpdate: IChickenDTD, DEFAULT_SIDE_LENGTH: number, OFFSET: number) {
    const chickenMesh = scene.getObjectById(currentChicken.meshId)

    currentChicken.chickenPosX = chickenUpdate.chickenPosX
    currentChicken.chickenPosZ = chickenUpdate.chickenPosZ

    //chickenMesh!.position.lerp(new THREE.Vector3(currentChicken.posX * DEFAULT_SIDE_LENGTH + OFFSET, 0, currentChicken.posZ * DEFAULT_SIDE_LENGTH + OFFSET), CHICKEN_MOVEMENT_SPEED)  // interpolates between original point and new point
    chickenMesh!.position.set(currentChicken.chickenPosX * DEFAULT_SIDE_LENGTH + OFFSET, 0, currentChicken.chickenPosZ * DEFAULT_SIDE_LENGTH + OFFSET)
  }

  function updateWalkingDirectionScriptGhost(currentScriptGhost: IScriptGhost, scriptGhostUpdate: IScriptGhostDTD, DEFAULT_SIDE_LENGTH: number, OFFSET: number) {
    const scriptGhostMesh = scene.getObjectById(currentScriptGhost.meshId)

    currentScriptGhost.ghostPosX = scriptGhostUpdate.ghostPosX
    currentScriptGhost.ghostPosZ = scriptGhostUpdate.ghostPosZ

    scriptGhostMesh!.position.set(currentScriptGhost.ghostPosX * DEFAULT_SIDE_LENGTH + OFFSET, 0, currentScriptGhost.ghostPosZ * DEFAULT_SIDE_LENGTH + OFFSET)
  }

  function updateGhost(currentGhost: IGhost, ghostUpdate: IGhostDTD, DEFAULT_SIDE_LENGTH: number, OFFSET: number) {
    const ghostMesh = scene.getObjectById(currentGhost.meshId)

    currentGhost.posX = ghostUpdate.posX
    currentGhost.posZ = ghostUpdate.posZ
    currentGhost.posY = ghostUpdate.posY

    ghostMesh!.position.set(currentGhost.posX ,currentGhost.posY, currentGhost.posZ )
  }

  function setSnackMeshId(squareId: number, meshId: number) {
    const square = mapData.gameMap.get(squareId)
    if (square != undefined && square.snack != null)
      square.snack.meshId = meshId
  }

  function setChickenMeshId(meshId: number, chickenId: number) {
    const chicken = mapData.chickens.find(chicken => chicken.id === chickenId);
    if (chicken != undefined)
      chicken.meshId = meshId
  }

  function setGhostMeshId(meshId: number, ghostId: number) {
    const ghost = mapData.ghosts.find(ghost => ghost.id === ghostId);
    if (ghost != undefined)
      ghost.meshId = meshId
  }

  function setScriptGhostMeshId(meshId: number, ghostId: number) {
    const ghost = mapData.scriptGhosts.find(ghost => ghost.id === ghostId);
    if (ghost != undefined)
      ghost.meshId = meshId
  }

  function removeMeshFromScene(scene: Scene, meshId: number) {
    const mesh = scene.getObjectById(meshId)
    if (mesh != undefined) {
      scene.remove(mesh!)
    }
  }

  function getScene() {
    return scene
  }

  return {
    mapContent: readonly(mapData as IGameMap),
    initGameMap,
    startGameMapLiveUpdate,
    setSnackMeshId,
    setChickenMeshId,
    setGhostMeshId,
    setScriptGhostMeshId,
    getScene
  };
})
