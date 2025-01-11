<template>
  <MenuBackground>
    <MainMenuButton @click="showLobbies">Multiplayer</MainMenuButton>
  </MenuBackground>
</template>

<script setup lang="ts">
  import MenuBackground from '@/components/MenuBackground.vue';
  import MainMenuButton from '@/components/MainMenuButton.vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();

  const showLobbies = () => {
    router.push({ name: 'LobbyListView' });
  };
</script>