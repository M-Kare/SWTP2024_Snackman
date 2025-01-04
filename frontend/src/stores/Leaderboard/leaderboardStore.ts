import {defineStore} from 'pinia';
import {reactive, readonly} from "vue";
import {Client} from "@stomp/stompjs";
import type {IFrontendLeaderboardMessageEvent} from "@/services/IFrontendMessageEvent";
import type {Leaderboard, LeaderboardDTD} from "@/stores/Leaderboard/LeaderboardEntryDTD";
import {fetchLeaderboardDataFromBackend} from "@/services/LeaderboardInitDataService";
import type {LeaderboardEntry} from "@/stores/Leaderboard/LeaderboardDTD";

/**
 * Defines the pinia store used for saving the leaderboard from
 * the backend. Updates of the leaderboard entries are saved here and
 * updated in the store itself.
 */
export const useLeaderboardStore = defineStore('leaderboard', () => {
  let leaderboardStompClient: Client

  const leaderboard = reactive({
    leaderboardEntries: []
  } as Leaderboard)

  async function initLeaderboardStore() {
    try {
      const response: LeaderboardDTD = await fetchLeaderboardDataFromBackend()

      for (const entry of response.leaderboardEntries) {
        leaderboard.leaderboardEntries.push(entry as LeaderboardEntry)
      }

      console.log("Initialised leaderboard data {}", leaderboard)
    } catch (reason) {
      throw reason
    }
  }

  async function startLeaderboardUpdate() {
    const protocol = window.location.protocol.replace('http', 'ws')
    const wsurl = `${protocol}//${window.location.host}/stompbroker`
    const DEST_SQUARE = '/topic/leaderboard'

    if (!leaderboardStompClient) {
      leaderboardStompClient = new Client({brokerURL: wsurl})

      leaderboardStompClient.onWebSocketError = (event) => {
        throw new Error('Websocket wit message: ' + event)
      }

      leaderboardStompClient.onStompError = (frameElement) => {
        throw new Error('Stompclient with message: ' + frameElement)
      }

      leaderboardStompClient.onConnect = (frameElement) => {
        console.log('Stompclient connected')

        leaderboardStompClient.subscribe(DEST_SQUARE, async (message) => {
          const change: IFrontendLeaderboardMessageEvent = JSON.parse(message.body)

          leaderboard.leaderboardEntries = []

          for (const entry of change.leaderboardEntries) {
            leaderboard.leaderboardEntries.push(entry as LeaderboardEntry)
          }
        })
      }

      leaderboardStompClient.onDisconnect = () => {
        console.log('Stompclient disconnected.')
      }

      leaderboardStompClient.activate()
    }
  }

  return {
    leaderboard: readonly(leaderboard as Leaderboard),
    initLeaderboardStore,
    startLeaderboardUpdate,
  }
})
