import {defineStore} from 'pinia';
import {reactive, readonly} from "vue";
import type {IGameMap, IGameMapDTD} from './IGameMapDTD';
import {fetchGameMapDataFromBackend} from "../services/GameMapDataService.js";
import {Client} from "@stomp/stompjs";
import type {IFrontendChickenMessageEvent, IFrontendMessageEvent} from "@/services/IFrontendMessageEvent";
import type {ISquare} from "@/stores/Square/ISquareDTD";
import * as THREE from "three";
import {Scene} from "three";
import type {IChicken, IChickenDTD} from "@/stores/Chicken/IChickenDTD";
import {Direction} from "@/stores/Chicken/IChickenDTD";
import {GameObjectRenderer} from "@/renderer/GameObjectRenderer";
import {useLobbiesStore} from "@/stores/Lobby/lobbiesstore";

/**
 * Defines the pinia store used for saving the map from
 * the backend. Updates of the snacks are saved here and
 * updated in the store itself. It holds the scene on
 * which the game is build up on.
 */
export const useGameMapStore = defineStore('gameMap', () => {
  let snackStompclient: Client
  let chickenStompclient: Client
  const scene = new THREE.Scene()
  const gameObjectRenderer = GameObjectRenderer()
  const CHICKEN_MOVEMENT_SPEED = 0.1    // step size of the interpolation: between 0 and 1

  const mapData = reactive({
    DEFAULT_SQUARE_SIDE_LENGTH: 0,
    DEFAULT_WALL_HEIGHT: 0,
    gameMap: new Map<number, ISquare>(),
    chickens: []
  } as IGameMap);

  async function initGameMap() {
    try {

      const lobbyStore = useLobbiesStore()
      const response: IGameMapDTD = await fetchGameMapDataFromBackend(lobbyStore.lobbydata.currentPlayer.joinedLobbyId!)
      mapData.DEFAULT_SQUARE_SIDE_LENGTH = response.DEFAULT_SQUARE_SIDE_LENGTH
      mapData.DEFAULT_WALL_HEIGHT = response.DEFAULT_WALL_HEIGHT

      for (const square of response.gameMap) {
        mapData.gameMap.set(square.id, square as ISquare)
      }

      for (const chicken of response.chickens) {
        mapData.chickens.push(chicken as IChicken)
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
              updateLookingDirection(currentChicken, chickenUpdate)
            } else {
              updateWalkingDirection(currentChicken, chickenUpdate, DEFAULT_SIDE_LENGTH, OFFSET)
            }
          }
        })
      }

      chickenStompclient.onDisconnect = () => {
        console.log('Chicken Stompclient disconnected.')
      }

      chickenStompclient.activate()
    }
  }

  function updateThickness(currentChicken: IChicken, chickenUpdate: IChickenDTD) {
    console.log("Chicken thickness updated")
    const chickenMesh = scene.getObjectById(currentChicken.meshId)
    currentChicken.thickness = chickenUpdate.thickness

    // todo update chicken thickness with new geometry
  }

  function updateLookingDirection(currentChicken: IChicken, chickenUpdate: IChickenDTD) {
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

  function updateWalkingDirection(currentChicken: IChicken, chickenUpdate: IChickenDTD, DEFAULT_SIDE_LENGTH: number, OFFSET: number) {
    console.log("Chicken moved")
    const chickenMesh = scene.getObjectById(currentChicken.meshId)

    currentChicken.chickenPosX = chickenUpdate.chickenPosX
    currentChicken.chickenPosZ = chickenUpdate.chickenPosZ

    //chickenMesh!.position.lerp(new THREE.Vector3(currentChicken.posX * DEFAULT_SIDE_LENGTH + OFFSET, 0, currentChicken.posZ * DEFAULT_SIDE_LENGTH + OFFSET), CHICKEN_MOVEMENT_SPEED)  // interpolates between original point and new point
    chickenMesh!.position.set(currentChicken.chickenPosX * DEFAULT_SIDE_LENGTH + OFFSET, 0, currentChicken.chickenPosZ * DEFAULT_SIDE_LENGTH + OFFSET)
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
    getScene
  };
})
