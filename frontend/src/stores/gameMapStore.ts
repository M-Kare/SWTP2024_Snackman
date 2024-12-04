import {defineStore} from 'pinia';
import {reactive, readonly} from "vue";
import type {IGameMap, IGameMapDTD} from './IGameMapDTD';
import {fetchGameMapDataFromBackend} from "../services/GameMapDataService.js";
import {Client} from "@stomp/stompjs";
import type {IFrontendMessageEvent} from "@/services/IFrontendMessageEvent";
import type {ISquare} from "@/stores/Square/ISquareDTD";
import {Scene} from "three";
import * as THREE from "three";

export const useGameMapStore = defineStore('gameMap', () => {
  let stompclient: Client
  const scene = new THREE.Scene()

  const mapData = reactive({
    DEFAULT_SQUARE_SIDE_LENGTH: 0,
    DEFAULT_WALL_HEIGHT: 0,
    gameMap: new Map<number, ISquare>()
  } as IGameMap);

  async function initGameMap() {
    try {

      const response: IGameMapDTD = await fetchGameMapDataFromBackend()
      mapData.DEFAULT_SQUARE_SIDE_LENGTH = response.DEFAULT_SQUARE_SIDE_LENGTH
      mapData.DEFAULT_WALL_HEIGHT = response.DEFAULT_WALL_HEIGHT

      for (const square of response.gameMap) {
        mapData.gameMap.set(square.id, square as ISquare)
      }

    } catch (reason) {
      throw reason //Throw again to pass to execution function
    }
  }

  async function startGameMapLiveUpdate() {
    const protocol = window.location.protocol.replace('http', 'ws')
    const wsurl = `${protocol}//${window.location.host}/stompbroker`
    const DEST = '/topic/square'

    if (!stompclient) {
      stompclient = new Client({brokerURL: wsurl})

      stompclient.onWebSocketError = (event) => {
        throw new Error('Websocket wit message: ' + event)
      }

      stompclient.onStompError = (frameElement) => {
        throw new Error('Stompclient with message: ' + frameElement)
      }

      stompclient.onConnect = (frameElement) => {
        console.log('Stompclient connected')

        stompclient.subscribe(DEST, async (message) => {
          const change: IFrontendMessageEvent = JSON.parse(message.body)

          const savedMeshId = mapData.gameMap.get(change.square.id)!.snack.meshId

          removeSnackMeshFromScene(scene, savedMeshId)

          mapData.gameMap.set(change.square.id, change.square as ISquare)

        })
      }

      stompclient.onDisconnect = () => {
        console.log('Stompclient disconnected.')
      }

      stompclient.activate()
    }
  }

  function setSnackMeshId(squareId: number, meshId: number) {
    const square = mapData.gameMap.get(squareId)
    if(square != undefined && square.snack != null)
      square.snack.meshId = meshId
  }

  function removeSnackMeshFromScene(scene: Scene, meshId: number) {
    const snackMesh = scene.getObjectById(meshId)
    if(snackMesh != undefined){
      scene.remove(snackMesh!)
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
    getScene
  };
})
