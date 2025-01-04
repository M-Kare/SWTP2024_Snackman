<template>
  <MenuBackground></MenuBackground>

  <h1 class="title">Snackman</h1>
  <!--
  Buttons will be merged later on,
  right now we need a fast entry into the game to test things (Singpleplayer-Button)
   -->
  <MainMenuButton class="menu-button" id="singleplayer-button" @click="toGameView">Singleplayer</MainMenuButton>
  <MainMenuButton class="menu-button" id="multiplayer-button" @click="showLobbies">Multiplayer</MainMenuButton>
  <MainMenuButton class="menu-button" id="leaderboard-button" @click="showLeaderboard">Leaderboard</MainMenuButton>
  <MainMenuButton class="menu-button" @click="showCreateNewLeaderboardEntryForm">Create new leaderboard entry</MainMenuButton>

  <Leaderboard
    v-if="showLeaderboardPopUp"
    :show="showLeaderboardPopUp"
    @close="hideLeaderboard"
  />

  <CreateNewLeaderboardEntryForm
    v-if="showCreateNewLeaderboardEntry"
    @cancelNewLeaderboardEntryCreation="cancelNewLeaderboardEntryCreation">
  </CreateNewLeaderboardEntryForm>
</template>

<script setup lang="ts">
import MainMenuButton from '@/components/MainMenuButton.vue';
import MenuBackground from '@/components/MenuBackground.vue';
import {useRouter} from 'vue-router';
import {ref} from "vue";
import Leaderboard from "@/components/Leaderboard.vue";
import CreateNewLeaderboardEntryForm from "@/components/CreateNewLeaderboardEntryForm.vue";

const router = useRouter();
const showLeaderboardPopUp = ref(false);
const showCreateNewLeaderboardEntry = ref(false);

const toGameView = () => {
  router.push({name: 'GameView'});
};

const showLobbies = () => {
  router.push({name: 'LobbyListView'});
}

const showLeaderboard = () => {
  showLeaderboardPopUp.value = true;
}

const hideLeaderboard = () => {
  showLeaderboardPopUp.value = false;
}

const cancelNewLeaderboardEntryCreation = () => {
  showCreateNewLeaderboardEntry.value = false;
  console.log(showCreateNewLeaderboardEntry.value)
}

const showCreateNewLeaderboardEntryForm = () => {
  showCreateNewLeaderboardEntry.value = true;
  console.log(showCreateNewLeaderboardEntry.value)
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
  top: 45%;
}

#multiplayer-button {
  top: 65%;
}

#leaderboard-button {
  top: 85%
}
</style>
