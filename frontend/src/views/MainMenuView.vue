<template>
    <MenuBackground></MenuBackground>

    <h1 class="title">Snackman</h1>
    <!--
    Buttons will be merged later on,
    right now we need a fast entry into the game to test things (Singpleplayer-Button)
     -->
    <MainMenuButton class="menu-button" id="singleplayer-button">Singleplayer</MainMenuButton>
    <MainMenuButton class="menu-button" id="multiplayer-button" @click="showLobbies">Multiplayer</MainMenuButton>
</template>

<script setup lang="ts">
  import MainMenuButton from '@/components/MainMenuButton.vue';
  import MenuBackground from '@/components/MenuBackground.vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();

  const showLobbies = () => {
      router.push({name: 'LobbyList'});
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
  top: 50%;
}

#multiplayer-button {
  top: 70%;
}
</style>
