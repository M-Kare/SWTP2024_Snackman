<template>
  <MenuBackground></MenuBackground>

  <h1 class="title">Choose Your Character</h1>
  <div class="outer-box">
    <div class="inner-box">
      <div class="character-grid">
        <div
          v-for="character in characters"
          :key="character.id"
          @click="selectCharacter(character)"
          class="character-item"
          :class="{ 'selected': selectedCharacter === character }">
          <div class="image-container">
            <img :src="character.image" :alt="character.name" class="character-image">
          </div>
          <p class="character-name">{{ character.name }}</p>
        </div>
      </div>
    </div>

    <SmallNavButton
      id="confirm-button"
      class="small-nav-buttons"
      @click="toGame"
      :disabled="!selectedCharacter"
    >
      Finish Character Selection
    </SmallNavButton>
  </div>
</template>

<script setup lang="ts">
    import { ref } from 'vue';
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import {useRouter} from "vue-router";

    const router = useRouter();
    const characters = ref([
      { id: 1, name: 'Snackman', image: '/packman.png' },
      { id: 2, name: 'Ghost', image: '/ghost.jpg' },
      { id: 3, name: 'Ghost', image: '/ghost.jpg' },
      { id: 4, name: 'Ghost', image: '/ghost.jpg' }
    ]);

    const selectedCharacter = ref(null);    // TODO make responsive

    const selectCharacter = (character) => {    // TODO API Call to backend to see if you can pick this character
      console.log("Trying to select this character", character)
      selectedCharacter.value = character;
    };

    /**
     * Route to gameview when characters have been selected
     */
    const toGame = () => {      // TODO enable this when the snackman character has been chosen + currently not routing to gameview correctly
      console.log("Routing to game view")
      router.push({ name: 'LobbyListView' });
    }

</script>

<style scoped>
.title {
    position: absolute;
    top: 3rem;
    left: 50%;
    transform: translateX(-50%);
    font-size: 3rem;
    font-weight: bold;
    color: #fff;
    text-align: center;
}

.outer-box {
    position: absolute;
    top: 30%;
    left: 50%;
    transform: translateX(-50%);
    width: 70vw;
    max-width: 1000px;
    height: 30rem;
    max-height: 45rem;
    background: rgba(255, 255, 255, 60%);
    border-radius: 0.5rem;
}

.inner-box > ul {
    list-style: none;
    display: flex;
    flex-direction: column;
    left: 50%;
    transform: translateX(-50%);
    margin: 0;
    padding: 0;
    width: 100%;
}

.character-grid {
  display: grid;
  grid-template-columns: repeat(2, 5fr);
  grid-template-rows: repeat(2, 18vw);
  grid-gap: 15px;
}

#confirm-button {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
}

#confirm-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.character-item {
  background: rgba(255, 255, 255, 70%);
  text-align: center;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.character-item.selected {
  opacity: 0.3;
  cursor: not-allowed;
}

.character-item:hover {
  transform: scale(1.05);
}

.image-container {
  flex-grow: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.character-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.character-name {
  font-weight: bold;
  color: #000000;
}


</style>
