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
import { Player } from '@/components/Player';
import { EventType, type IMessageDTD } from './messaging/IMessageDTD';
import { MeshSSSNodeMaterial } from 'three/webgpu';
import type { IMobUpdateDTD } from './messaging/IMobUpdateDTD';
import type { ISquareUpdateDTD } from './messaging/ISquareUpdateDTD';

/**
 * Defines the pinia store used for saving the map from
 * the backend. Updates of the snacks are saved here and
 * updated in the store itself. It holds the scene on
 * which the game is build up on.
 */
export const useGameMapStore = defineStore('gameMap', () => {
  const protocol = window.location.protocol.replace('http', 'ws')
  const wsurl = `${protocol}//${window.location.host}/stompbroker`
  let stompclient = new Client({brokerURL: wsurl})
  let chickenStompclient: Client
  const scene = new THREE.Scene()
  const gameObjectRenderer = GameObjectRenderer()
  const { lobbydata } = useLobbiesStore()
  const CHICKEN_MOVEMENT_SPEED = 0.1    // step size of the interpolation: between 0 and 1
  let player: Player
  let otherPlayers: Map<String, THREE.Mesh>

  const mapData = reactive({
    DEFAULT_SQUARE_SIDE_LENGTH: 0,
    DEFAULT_WALL_HEIGHT: 0,
    gameMap: new Map<number, ISquare>(),
    chickens: [],
  } as IGameMap);

  async function initGameMap() {
    try {

      const response: IGameMapDTD = await fetchGameMapDataFromBackend(lobbydata.currentPlayer.joinedLobbyId!)
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

  function startGameMapLiveUpdate() {

    const DEST_UPDATES = `/topic/lobbies/${lobbydata.currentPlayer.joinedLobbyId}/update`
    const DEST_CHICKEN = `/topic/lobbies/${lobbydata.currentPlayer.joinedLobbyId}/chicken`
    if (!stompclient.active) {

      stompclient.onWebSocketError = (event) => {
        throw new Error('Websocket wit message: ' + event)
      }

      stompclient.onStompError = (frameElement) => {
        throw new Error('Stompclient with message: ' + frameElement)
      }

      stompclient.onConnect = (frameElement) => {
        console.log('Stompclient connected')

        stompclient.subscribe(DEST_UPDATES, message => {
          const content: Array<IMessageDTD> = JSON.parse(message.body)
          for(const mess of content){
            switch(mess.event){
              case EventType.MobUpdate:
                const mobUpdate: IMobUpdateDTD = mess.message
                if(mobUpdate.playerId === lobbydata.currentPlayer.playerId){
                  if(player == undefined){
                   continue; 
                  }
                  player.setPosition(mobUpdate.position);
                } else {
                  if(otherPlayers.size == 0){
                    continue;
                  }
                  otherPlayers.get(mobUpdate.playerId)?.position.lerp(mobUpdate.position, 0.3)
                  otherPlayers.get(mobUpdate.playerId)?.setRotationFromQuaternion(mobUpdate.rotation)
                }
                break;
              case EventType.SquareUpdate:
                const squareUpdate: ISquareUpdateDTD = mess.message
                if(squareUpdate.square.snack == null){
                  const savedMeshId = mapData.gameMap.get(squareUpdate.square.id)!.snack.meshId
                  removeMeshFromScene(scene, savedMeshId)
                  mapData.gameMap.set(squareUpdate.square.id, squareUpdate.square)
                } else {
                  //TODO: Wenn Square einen Snack besitz: den spezifischen Snack erstellen und in die Scene hinzufügen
                }
                break;
              case EventType.ChickenUpdate:
                console.log(mess.message)
                const chickenUpdate: IChickenDTD = mess.message
                updateChicken(chickenUpdate)
                break;
              default: 
                console.log(mess.message)
            }
          }
        })
      }

      stompclient.onDisconnect = () => {
        console.log('Stompclient disconnected.')
      }

      stompclient.activate()
    }


    // if (!chickenStompclient) {
    //   chickenStompclient = new Client({brokerURL: wsurl})

    //   chickenStompclient.onWebSocketError = (event) => {
    //     throw new Error('Chicken Websocket with message: ' + event)
    //   }

    //   chickenStompclient.onStompError = (frameElement) => {
    //     throw new Error('Chicken Stompclient with message: ' + frameElement)
    //   }

    //   chickenStompclient.onConnect = (frameElement) => {
    //     console.log('Stompclient for chicken connected')

    //     chickenStompclient.subscribe(DEST_CHICKEN, async (message) => {
    //       const change: IFrontendChickenMessageEvent = JSON.parse(message.body)
    //       console.log("Received a chicken update: {}", change)

    //       const chickenUpdate: IChickenDTD = change.chicken
    //       const OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH / 2
    //       const DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH
    //       const currentChicken = mapData.chickens.find(chicken => chicken.id == chickenUpdate.id)
    //       console.log("chicken update {}", chickenUpdate)

    //       if (currentChicken == undefined) {
    //         console.error("A chicken is undefined in pinia")
    //       } else {
    //         if (currentChicken.thickness != chickenUpdate.thickness) {
    //           updateThickness(currentChicken, chickenUpdate)
    //         }
    //         if (chickenUpdate.chickenPosX == currentChicken!.chickenPosX && chickenUpdate.chickenPosZ == currentChicken!.chickenPosZ) {
    //           updateLookingDirection(currentChicken, chickenUpdate)
    //         } else {
    //           updateWalkingDirection(currentChicken, chickenUpdate, DEFAULT_SIDE_LENGTH, OFFSET)
    //         }
    //       }
    //     })
    //   }

    //   chickenStompclient.onDisconnect = () => {
    //     console.log('Chicken Stompclient disconnected.')
    //   }

    //   chickenStompclient.activate()
    // }
  }

  function updateChicken(change: IChickenDTD){
    const chickenUpdate: IChickenDTD = change
    const OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH / 2
    const DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH
    const currentChicken = mapData.chickens.find(chicken => chicken.id == chickenUpdate.id)
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
  }

  function setPlayer(p: Player){
    player = p
  }

  function setOtherPlayers(other: Map<String, THREE.Mesh>){
    otherPlayers = other
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

    // chickenMesh!.position.lerp(new THREE.Vector3(currentChicken.chickenPosX * DEFAULT_SIDE_LENGTH + OFFSET, 0, currentChicken.chickenPosZ * DEFAULT_SIDE_LENGTH + OFFSET), CHICKEN_MOVEMENT_SPEED)  // interpolates between original point and new point
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
    getScene,
    setPlayer,
    setOtherPlayers,
    stompclient: stompclient
  };
})
