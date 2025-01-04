<template>
    <MenuBackground></MenuBackground>

    <h1 class="title">Snackman</h1>
      <div class="custom-map">
        <span>Custom Map</span>
        <CustomMapButton v-model="toggleState"></CustomMapButton>
      </div>
      <MapButton v-if="toggleState" class="map-importiren-button" @click="importierenMap">Map Importieren</MapButton>
    <!--
    Buttons will be merged later on,
    right now we need a fast entry into the game to test things (Singpleplayer-Button)
     -->
    <MainMenuButton class="menu-button" id="singleplayer-button">Singleplayer</MainMenuButton>
    <MainMenuButton class="menu-button" id="multiplayer-button" @click="showLobbies">Multiplayer</MainMenuButton>
</template>

<script setup lang="ts">
    import CustomMapButton from '@/components/CustomMapButton.vue';
  import MainMenuButton from '@/components/MainMenuButton.vue';
    import MapButton from '@/components/MapButton.vue';
    import { ref } from 'vue';
  import MenuBackground from '@/components/MenuBackground.vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();

  const showLobbies = () => {
      router.push({name: 'LobbyListView'});
  }

    const toggleState = ref(false);

    const importierenMap = () => {

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

  .custom-map{
    position: absolute;
    right: 20px;
    top: 30px;
    display: flex;
    gap: 25px;
    justify-content: center;
    align-items: center;
    font-size: 3rem;
    color: #fff;
    z-index: 3;
  }

  .map-importiren-button{
    position: absolute;
    top: 30%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 3;
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
