import {defineStore} from 'pinia';
import {reactive, readonly} from "vue";
import type {IGameMapDTD} from './IGameMapDTD';
import {fetchGameMapDataFromBackend} from "../services/GameMapDataService.js";

export const useGameMapStore = defineStore('gameMap', () => {
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

  return {
    mapContent: readonly(mapData as IGameMapDTD),
    initGameMap
  };
})
