import {defineStore} from 'pinia';
import {reactive, readonly} from "vue";
import type {IGameMap, IGameMapDTD} from './IGameMapDTD';
import {fetchGameMapDataFromBackend} from "../services/GameMapDataService.js";
import {Client} from "@stomp/stompjs";
import type {ISquare} from "@/stores/Square/ISquareDTD";
import * as THREE from "three";
import {Scene} from "three";
import type {IChicken, IChickenDTD} from "@/stores/Chicken/IChickenDTD";
import {ChickenThickness, Direction} from "@/stores/Chicken/IChickenDTD";
import {GameObjectRenderer} from "@/renderer/GameObjectRenderer";
import {useLobbiesStore} from "@/stores/Lobby/lobbiesstore";
import {Player} from '@/components/Player';
import {EventType, type IMessageDTD} from './messaging/IMessageDTD';
import type {ISnackmanUpdateDTD} from './messaging/ISnackmanUpdateDTD';
import type {ISquareUpdateDTD} from './messaging/ISquareUpdateDTD';
import {SnackType} from './Snack/ISnackDTD';
import type {IGhost, IGhostDTD} from "@/stores/Ghost/IGhostDTD";
import type {IScriptGhost, IScriptGhostDTD} from "@/stores/Ghost/IScriptGhostDTD";
import type {IGhostUpdateDTD} from "@/stores/messaging/IGhostUpdateDTD";

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
  const scene = new THREE.Scene()
  const gameObjectRenderer = GameObjectRenderer()
  const {lobbydata} = useLobbiesStore()
  const CHICKEN_MOVEMENT_SPEED = 0.1    // step size of the interpolation: between 0 and 1
  let player: Player
  let otherPlayers: Map<String, THREE.Mesh>
  let OFFSET: number
  let DEFAULT_SIDE_LENGTH: number


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

      // todo working for ghost and snackman??
      const response: IGameMapDTD = await fetchGameMapDataFromBackend(lobbydata.currentPlayer.joinedLobbyId!)
      mapData.DEFAULT_SQUARE_SIDE_LENGTH = response.DEFAULT_SQUARE_SIDE_LENGTH
      mapData.DEFAULT_WALL_HEIGHT = response.DEFAULT_WALL_HEIGHT

      OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH / 2
      DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH

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

  function startGameMapLiveUpdate() {

    const DEST_UPDATES = `/topic/lobbies/${lobbydata.currentPlayer.joinedLobbyId}/update`
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
          for (const mess of content) {
            switch (mess.event) {
              // TODO GhostUpdate / ScriptGhostUpdate Rolle Unterschieden
              case EventType.SnackManUpdate:
                const mobUpdate: ISnackmanUpdateDTD = mess.message
                if (mobUpdate.playerId === lobbydata.currentPlayer.playerId) {
                  if (player == undefined) {
                    continue;
                  }

                  player.setCalories(mobUpdate.calories)

                  player.sprintData.sprintTimeLeft = mobUpdate.sprintTimeLeft
                  player.sprintData.isSprinting = mobUpdate.isSprinting
                  player.sprintData.isCooldown = mobUpdate.isInCooldown

                  if (mobUpdate.message != null) {
                    player.message.value = mobUpdate.message
                  }

                  player.setPosition(mobUpdate.position);
                } else {
                  if (otherPlayers == undefined || otherPlayers.size == 0) {
                    continue;
                  }
                  otherPlayers.get(mobUpdate.playerId)?.position.lerp(mobUpdate.position, 0.3)
                  otherPlayers.get(mobUpdate.playerId)?.setRotationFromQuaternion(mobUpdate.rotation)
                }
                break;

              case EventType.GhostUpdate:
                const ghostUpdate: IGhostUpdateDTD = mess.message
                if (ghostUpdate.playerId === lobbydata.currentPlayer.playerId) {
                  if (player == undefined) {
                    continue;
                  }

                  player.setPosition(ghostUpdate.position);
                  break;
                } else {
                  if (otherPlayers == undefined || otherPlayers.size == 0) {
                    continue;
                  }
                  otherPlayers.get(ghostUpdate.playerId)?.position.lerp(ghostUpdate.position, 0.3)
                  otherPlayers.get(ghostUpdate.playerId)?.setRotationFromQuaternion(ghostUpdate.rotation)
                }
                break;
              case EventType.SquareUpdate:
                const squareUpdate: ISquareUpdateDTD = mess.message
                if (squareUpdate.square.snack.snackType == SnackType.EMPTY) {
                  const savedMeshId = mapData.gameMap.get(squareUpdate.square.id)!.snack.meshId
                  removeMeshFromScene(scene, savedMeshId)
                  mapData.gameMap.set(squareUpdate.square.id, squareUpdate.square)
                } else {
                  spawnSnack(squareUpdate)
                }
                break;
              case EventType.ChickenUpdate:
                const chickenUpdate: IChickenDTD = mess.message
                updateChicken(chickenUpdate)
                break;
              default:
                console.log(mess.message)

              // todo add ghst and script ghost update
            }
          }
        })
      }
      //TODO funktioniert bereits, da squares einen property change listener haben?
      /*
              snackStompclient.subscribe(DEST_SQUARE, async message => {
          const change: IFrontendMessageEvent = JSON.parse(message.body)

          if (change.changeType == 'CREATE') {
            const OFFSET = mapData.DEFAULT_SQUARE_SIDE_LENGTH / 2
            const DEFAULT_SIDE_LENGTH = mapData.DEFAULT_SQUARE_SIDE_LENGTH

            const square = change.square as ISquare
            const currentSquareInPinia = mapData.gameMap.get(square.id)
            const eggToAdd = gameObjectRenderer.createSnackOnFloor(
              square.indexX * DEFAULT_SIDE_LENGTH + OFFSET,
              square.indexZ * DEFAULT_SIDE_LENGTH + OFFSET,
              DEFAULT_SIDE_LENGTH,
              square.snack?.snackType,
            )

            currentSquareInPinia!.snack = square.snack
            scene.add(eggToAdd)
            setSnackMeshId(currentSquareInPinia!.id, eggToAdd.id)

            mapData.gameMap.set(change.square.id, change.square as ISquare)
          } else if (change.changeType == 'UPDATE') {
            // TODO fix bug "Unhandled Promise Rejection: TypeError: null is not an object (evaluating 'mapData.gameMap.get(change.square.id).snack.meshId')"
            if (mapData.gameMap.get(change.square.id)!.snack.meshId != null) {
              const savedMeshId = mapData.gameMap.get(change.square.id)!.snack
                .meshId

              removeMeshFromScene(scene, savedMeshId)

              mapData.gameMap.set(change.square.id, change.square as ISquare)
            }
          }
       */

      stompclient.onDisconnect = () => {
        console.log('Stompclient disconnected.')
      }

      stompclient.activate()
    }
  }

  function updateChicken(change: IChickenDTD) {
    const chickenUpdate: IChickenDTD = change
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

  function spawnSnack(squareUpdate: ISquareUpdateDTD) {
    const savedMeshId = mapData.gameMap.get(squareUpdate.square.id)!.snack.meshId
    removeMeshFromScene(scene, savedMeshId)
    mapData.gameMap.set(squareUpdate.square.id, squareUpdate.square)
    const snackToAdd = gameObjectRenderer.createSnackOnFloor(
      squareUpdate.square.indexX * DEFAULT_SIDE_LENGTH + OFFSET,
      squareUpdate.square.indexZ * DEFAULT_SIDE_LENGTH + OFFSET,
      DEFAULT_SIDE_LENGTH,
      squareUpdate.square.snack?.snackType,
    )
    scene.add(snackToAdd)
    setSnackMeshId(squareUpdate.square.id, snackToAdd.id)
  }

  /*
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

todo logic here for ghosts
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
   */

  function setPlayer(p: Player) {
    player = p
  }

  function setOtherPlayers(other: Map<String, THREE.Mesh>) {
    otherPlayers = other
  }

  function updateThickness(
    currentChicken: IChicken,
    chickenUpdate: IChickenDTD,
  ) {
    const chickenMesh = scene.getObjectById(currentChicken.meshId)
    currentChicken.thickness = chickenUpdate.thickness

    if (!chickenMesh) {
      console.warn('Chicken mesh not found in the scene.')
      return
    }

    const thicknessValue =
      ChickenThickness[chickenUpdate.thickness as unknown as keyof typeof ChickenThickness]

    switch (thicknessValue) {
      case ChickenThickness.THIN:
        chickenMesh!.scale.set(1, 1, 1)
        break
      case ChickenThickness.SLIGHTLY_THICK:
        chickenMesh!.scale.set(1.25, 1.25, 1.25)
        break
      case ChickenThickness.MEDIUM:
        chickenMesh!.scale.set(1.5, 1.5, 1.5)
        break
      case ChickenThickness.HEAVY:
        chickenMesh!.scale.set(1.75, 1.75, 1.75)
        break
      case ChickenThickness.VERY_HEAVY:
        chickenMesh!.scale.set(2, 2, 2)
        break
      default:
        console.log('ETWAS IST SCHIED GELAUFEN...')
    }
  }

  function updateLookingDirection(
    currentChicken: IChicken,
    chickenUpdate: IChickenDTD,
  ) {
    const chickenMesh = scene.getObjectById(currentChicken.meshId)

    currentChicken.lookingDirection = chickenUpdate.lookingDirection
    switch (
      currentChicken.lookingDirection // rotates the chicken depending on what its looking direction is
      ) {
      case Direction.NORTH || Direction.SOUTH:
        chickenMesh!.setRotationFromEuler(new THREE.Euler(0))
        break
      case Direction.EAST || Direction.WEST:
        chickenMesh!.setRotationFromEuler(new THREE.Euler(Math.PI / 2))
        break
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

  function updateWalkingDirection(
    currentChicken: IChicken,
    chickenUpdate: IChickenDTD,
    DEFAULT_SIDE_LENGTH: number,
    OFFSET: number,
  ) {
    const chickenMesh = scene.getObjectById(currentChicken.meshId)

    currentChicken.chickenPosX = chickenUpdate.chickenPosX
    currentChicken.chickenPosZ = chickenUpdate.chickenPosZ

    //chickenMesh!.position.lerp(new THREE.Vector3(currentChicken.posX * DEFAULT_SIDE_LENGTH + OFFSET, 0, currentChicken.posZ * DEFAULT_SIDE_LENGTH + OFFSET), CHICKEN_MOVEMENT_SPEED)  // interpolates between original point and new point
    chickenMesh!.position.set(
      currentChicken.chickenPosX * DEFAULT_SIDE_LENGTH + OFFSET,
      0,
      currentChicken.chickenPosZ * DEFAULT_SIDE_LENGTH + OFFSET,
    )
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

    ghostMesh!.position.set(currentGhost.posX, currentGhost.posY, currentGhost.posZ)
  }

  function setSnackMeshId(squareId: number, meshId: number) {
    const square = mapData.gameMap.get(squareId)
    if (square != undefined && square.snack.snackType != SnackType.EMPTY)
      square.snack.meshId = meshId
  }

  function setChickenMeshId(meshId: number, chickenId: number) {
    const chicken = mapData.chickens.find(chicken => chicken.id === chickenId)
    if (chicken != undefined) chicken.meshId = meshId
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
    getScene,
    setPlayer,
    setOtherPlayers,
    stompclient: stompclient,
    setGhostMeshId,
    setScriptGhostMeshId,
  };
})
