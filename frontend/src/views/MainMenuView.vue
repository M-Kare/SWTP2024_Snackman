<template>
    <MenuBackground></MenuBackground>

    <div v-if="darkenBackground" id="darken-background"></div>

    <PlayerNameForm
      v-if="showPopUp"
      @hidePopUp="hidePopUp"
      >
    </PlayerNameForm>

    <h1 class="title">Snackman</h1>
    <!-- TODO / REMOVE COMMENT
    Buttons will be merged later on,
    right now we need a fast entry into the game to test things (Singpleplayer-Button)
     -->
    <MainMenuButton class="menu-button" id="singleplayer-button" @click="startSingleplayer">Singleplayer</MainMenuButton>
    <MainMenuButton class="menu-button" id="multiplayer-button" @click="showLobbies">Multiplayer</MainMenuButton>
</template>

<script setup lang="ts">
  import MainMenuButton from '@/components/MainMenuButton.vue';
  import MenuBackground from '@/components/MenuBackground.vue';
  import PlayerNameForm from '@/components/PlayerNameForm.vue';
  import { onMounted, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore';

  // const initialPopUpShown = ref(false);
  const darkenBackground = ref(false);
  const showPopUp = ref(false);

  const router = useRouter();
  const lobbiesStore = useLobbiesStore();

  const hidePopUp = () => {
        showPopUp.value = false;
        darkenBackground.value = false;
  }

  const showLobbies = () => {
      router.push({name: 'LobbyListView'});
  }

  const startSingleplayer = () => {
      lobbiesStore.lobbydata.currentPlayer.role = 'SNACKMAN';

      router.push({
          name: 'GameView',
          query: { role: 'SNACKMAN' }
      });
  }
  
  onMounted(() => {
    darkenBackground.value = true;
    console.log("darkenBackground - ", darkenBackground)
    showPopUp.value = true;
  })

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
  top: 50%;
}

#multiplayer-button {
  top: 70%;
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
