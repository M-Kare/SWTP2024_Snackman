<template>
  <div id="individual-outer-box-size" class="outer-box">
    <div class="inner-box">
      <h1 class="title"> {{ $t('difficulty.title') }} </h1>
      <div id="button-pair">
        <MainMenuButton id="easy-button"
                        :class="{ 'selected': difficulty === ScriptGhostDifficulty.EASY }"
                        @click="setDifficulty(ScriptGhostDifficulty.EASY)">
          {{ $t('difficulty.easy') }}
        </MainMenuButton>
        <MainMenuButton id="difficult-button"
                        :class="{ 'selected': difficulty === ScriptGhostDifficulty.DIFFICULT }"
                        @click="setDifficulty(ScriptGhostDifficulty.DIFFICULT)">
          {{ $t('difficulty.difficult') }}
        </MainMenuButton>
      </div>
        <p id="description">
          {{ $t('difficulty.description1') }}
          <span id="highlight-difficulty-easy">'{{ $t('difficulty.easy') }}'</span>
          {{ $t('difficulty.description2') }}
          <span id="highlight-difficulty-difficult">'{{ $t('difficulty.difficult') }}'</span>
          {{ $t('difficulty.description3') }}
          </p>
      <div id="button-box">
        <SmallNavButton id="menu-back-button" class="small-nav-buttons" @click="backToMainMenu">
          {{ $t('button.back') }} 
        </SmallNavButton>
        <SmallNavButton id="start-game-button" class="small-nav-buttons" @click="startSingleplayer">
          {{ $t('button.startGame') }} 
        </SmallNavButton>
      </div>
    </div>
  </div>

</template>

<script lang="ts" setup>
import SmallNavButton from "@/components/SmallNavButton.vue";
import {ref} from "vue";
import {useRouter} from 'vue-router'
import {ScriptGhostDifficulty} from "@/stores/Player/IDifficulty";
import type {ILobbyDTD} from "@/stores/Lobby/ILobbyDTD";
import {useLobbiesStore} from "@/stores/Lobby/lobbiesstore";
import MainMenuButton from "@/components/MainMenuButton.vue";

const router = useRouter()
const difficulty = ref(ScriptGhostDifficulty.EASY)    // standardmäßig ist easy ausgewählt -> das kann man dann ändern
const lobbiesStore = useLobbiesStore()

const emit = defineEmits<{
  (e: 'hideChooseDifficultySingleplayer'): void;
}>();

const setDifficulty = (newDifficulty: ScriptGhostDifficulty) => {
  difficulty.value = newDifficulty
}

const backToMainMenu = () => {
  emit('hideChooseDifficultySingleplayer');
}

const startSingleplayer = async () => {
  await lobbiesStore.createPlayer("Single Player")
  const player = lobbiesStore.lobbydata.currentPlayer
  const lobby = await lobbiesStore.startSingleplayerGame(player, difficulty.value.toString()) as ILobbyDTD

  if (!player.playerId || !lobby) {
    console.error('Player or Lobby not found')
    return
  }

  if (player.playerId === lobby.adminClient.playerId) {
    await router.push({
      name: 'GameView',
      query: {
        role: lobbiesStore.lobbydata.currentPlayer.role,
        lobbyId: lobbiesStore.lobbydata.currentPlayer.joinedLobbyId,
      },
    })
  }
}
</script>

<style scoped>
#individual-outer-box-size {
  width: 80%;
  height: 80%;
  padding: 2%;
  top: 10%;
}

.title {
  color: var(--primary-highlight-color);
  font-size: 60px;
  text-align: center;
  margin-bottom: 2rem;
  font-weight: bold;
}

#description {
  color: var(--main-text-color);
  text-align: center;
  font-size: 28px;
}

#button-pair {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  gap: 20px;
  padding: 3em 0;
}

#button-box {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  gap: 20px;
  margin-top: 10%;
}

#highlight-difficulty-easy {
  color: var(--primary-sprint-bar-color);
}

#highlight-difficulty-difficult {
  color: var(--accent-color);
}

#easy-button:hover,
#easy-button.selected {
  background-color: var(--primary-sprint-bar-color);
}

#difficult-button:hover,
#difficult-button.selected {
  background-color: var(--accent-color);
}

@media (max-width: 1000px) {
  .title {
    font-size: 60px;
  }
}
</style>
