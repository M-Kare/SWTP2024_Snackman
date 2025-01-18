<template>
  <div id="individual-outer-box-size" class="outer-box">

    <div class="inner-box">
      <h1 class="result-title">Choose the difficulty level for the ghosts!</h1>
      <div id="button-pair">
        <SmallNavButton id="menu-back-button" @click="setDifficulty(ScriptGhostDifficulty.EASY)">
          EASY
        </SmallNavButton>
        <SmallNavButton id="menu-back-button" @click="setDifficulty(ScriptGhostDifficulty.DIFFICULT)">
          DIFFICULT
        </SmallNavButton>
      </div>
      <p class="end-reason">Choosing 'EASY' will result in the ghosts not chasing you.
        If you however choose 'DIFFICULT', they will chase you (even when they can't see you)!</p>

      <div id="button-pair">
        <SmallNavButton id="menu-back-button" class="small-nav-buttons" @click="backToMainMenu">
          Back
        </SmallNavButton>
        <SmallNavButton id="menu-back-button" class="small-nav-buttons" @click="startSingleplayer">
          Start Game
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

const router = useRouter()
const difficulty = ref(ScriptGhostDifficulty.EASY)    // TODO standardmäßig ist easy ausgewählt -> das kann man dann ändern
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
  width: 60%;
  height: 60%;
  max-height: 70%;
  padding: 2%;
}

.result-title {
  color: var(--primary-highlight-color);
  line-height: 1.1;
  margin-bottom: 3rem;
  font-weight: bold;
}

.end-reason {
  color: var(--background-for-text-color);
  font-size: 2rem;
  margin-bottom: 2rem;
}

#button-pair {
  width: 100%;
  justify-content: space-around;
  display: flex;
  flex-direction: row;
  padding-top: 2em;
}

#menu-back-button:hover {
  background: var(--primary-highlight-color);
}

.outer-box {
  position: relative;
  min-height: 100vh;
}

.inner-box {
  position: relative;
  left: 50%;
  transform: translateX(-50%);
  height: 65%;
  border-radius: 0.3rem;
  color: var(--primary-text-color);
  overflow-y: auto;
}

/* Fade-in animation */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (max-width: 1000px) {
  .result-title {
    font-size: 60px;
  }
}
</style>
