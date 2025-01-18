<template>

  <MenuBackground>
    <div v-if="darkenBackground" id="darken-background"></div>

    <PlayerNameForm
      v-if="showPlayerNameForm && !playerNameSaved"
      @hidePlayerNameForm="hidePlayerNameForm"
    >
    </PlayerNameForm>

    <div id="language-container">
      <img src="@/assets/uk_flag.svg" alt="English" class="language-icon" @click="changeLocale('en')">
      <img src="@/assets/german_flag.svg" alt="German" class="language-icon" @click="changeLocale('de')">
    </div>

    <ChooseDifficultySingleplayer
      v-if="showChooseSingleplayerDifficulty"
      @hideChooseDifficultySingleplayer="hideChooseDifficultySingleplayer">
    </ChooseDifficultySingleplayer>

    <div class="button-container">
      <MainMenuButton v-if="!showChooseSingleplayerDifficulty" @click="startSingleplayer">Singleplayer</MainMenuButton>
      <MainMenuButton v-if="!showChooseSingleplayerDifficulty" @click="showLobbies">Multiplayer</MainMenuButton>
      <MainMenuButton v-if="!showChooseSingleplayerDifficulty" @click="showLeaderboard">Leaderboard</MainMenuButton>
    </div>
  </MenuBackground>
</template>

<script setup lang="ts">
import {useLobbiesStore} from '@/stores/Lobby/lobbiesstore'
import MainMenuButton from '@/components/MainMenuButton.vue'
import MenuBackground from '@/components/MenuBackground.vue'
import PlayerNameForm from '@/components/PlayerNameForm.vue';
import {useRouter} from 'vue-router'
import {onMounted, ref} from 'vue';
import {SoundManager} from "@/services/SoundManager";
import {SoundType} from "@/services/SoundTypes";
import ChooseDifficultySingleplayer from "@/views/ChooseDifficultySingleplayer.vue";
import { i18n } from '@/main';

const router = useRouter()
const lobbiesStore = useLobbiesStore()

const playerNameSaved = lobbiesStore.lobbydata.currentPlayer.playerName;
const darkenBackground = ref(false);
const showPlayerNameForm = ref(false);
const showChooseSingleplayerDifficulty = ref(false);

/**
 * triggered by click von imgs
 * changes locale and therefore the language
 * 
 * @param locale with according language
 */
const changeLocale = (locale: 'en' | 'de') => {
  i18n.global.locale.value = locale;
}

const hidePlayerNameForm = () => {
  showPlayerNameForm.value = false;
  darkenBackground.value = false;
}

const hideChooseDifficultySingleplayer = () => {
  showChooseSingleplayerDifficulty.value = false
}

const showLobbies = () => {
  router.push({name: 'LobbyListView'})
}

const showLeaderboard = () => {
  router.push({name: 'Leaderboard'})
}

const startSingleplayer = () => {
  showChooseSingleplayerDifficulty.value = true
}

onMounted(() => {
  if (!playerNameSaved) {
    darkenBackground.value = true;
    showPlayerNameForm.value = true;
  }

  SoundManager.playSound(SoundType.LOBBY_MUSIC)
})
</script>

<style scoped>
.button-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

.button-container > * {
  width: 100%;
  text-align: center;
}

#language-container {
  display: flex;
  gap: 1.2rem;
  position: fixed;
  top: 1.5rem;
  left: 2rem;
}

.language-icon {
  width: 4vw;
  height: auto;
  cursor: pointer;
  transition: transform 0.1s ease;
}

.language-icon:hover {
  transform: scale(1.1);
}

#darken-background {
  z-index: 1;
  position: fixed;
  top: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 50%);

  transition: background 0.3s ease;
}
</style>
