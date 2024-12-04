import {defineStore} from 'pinia';
import {reactive, readonly} from "vue";
import type {IGameMapDTD} from './IGameMapDTD';
import {fetchGameMapDataFromBackend} from "../services/GameMapDataService.js";
import {Client} from "@stomp/stompjs";
import type {IFrontendMessageEvent} from "@/services/IFrontendMessageEvent";

export const useGameMapStore = defineStore('gameMap', () => {
  let stompclient: Client

  const mapData = reactive({
      DEFAULT_SQUARE_SIDE_LENGTH: 0,
      DEFAULT_WALL_HEIGHT: 0,
      gameMap: []
  } as IGameMapDTD);

  async function initGameMap() {
    try {

      const response = await fetchGameMapDataFromBackend()
      console.log(response)
      mapData.DEFAULT_SQUARE_SIDE_LENGTH = response.DEFAULT_SQUARE_SIDE_LENGTH
      mapData.DEFAULT_WALL_HEIGHT = response.DEFAULT_WALL_HEIGHT
      mapData.gameMap = response.gameMap

    } catch (reason) {
      throw reason //Throw again to pass to execution function
    }
  }

  async function startGameMapLiveUpdate() {
    const wsurl = `ws://${window.location.host}/stompbroker`
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

          console.log(change)
        })
      }

      stompclient.onDisconnect = () => {
        console.log('Stompclient disconnected.')
      }

      stompclient.activate()
    }

  }

  return {
    mapContent: readonly(mapData as IGameMapDTD),
    initGameMap,
    startGameMapLiveUpdate
  };
})
