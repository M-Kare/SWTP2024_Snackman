<template>
  <MenuBackground></MenuBackground>

  <h1 class="title">Snackman</h1>
  <!--
  Buttons will be merged later on,
  right now we need a fast entry into the game to test things (Singpleplayer-Button)
   -->
  <MainMenuButton id="singleplayer-button" class="menu-button" @click="startSingleplayer">Singleplayer</MainMenuButton>
  <MainMenuButton id="multiplayer-button" class="menu-button" @click="showLobbies">Multiplayer</MainMenuButton>
  <MainMenuButton id="leaderboard-button" class="menu-button" @click="showLeaderboard">Leaderboard</MainMenuButton>

</template>

<script lang="ts" setup>
import {useLobbiesStore} from '@/stores/Lobby/lobbiesstore'
import MainMenuButton from '@/components/MainMenuButton.vue'
import MenuBackground from '@/components/MenuBackground.vue'
import {useRouter} from 'vue-router'

const router = useRouter()
const lobbiesStore = useLobbiesStore()

const showLobbies = () => {
  router.push({name: 'LobbyListView'})
}

const showLeaderboard = () => {
  router.push({name: 'Leaderboard'})
}

const startSingleplayer = () => {
  lobbiesStore.lobbydata.currentPlayer.role = 'SNACKMAN'

  router.push({
    name: 'GameView',
    query: {role: 'SNACKMAN'}
  })
}

</script>

<style scoped>
.title {
  position: absolute;
  top: 50px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10rem;
  font-weight: bold;
  color: #fff;
  text-align: center;
}

.menu-button {
  position: absolute;
  left: 50%;
  transform: translate(-50%, -50%); /* h & v centered */
}

#singleplayer-button {
  top: 45%
}

#multiplayer-button {
  top: 65%
}

#leaderboard-button {
  top: 85%
}
</style>
