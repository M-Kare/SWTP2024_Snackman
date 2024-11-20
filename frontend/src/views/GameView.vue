<template>
    <div class="text"> Player {{ playerId }} started Game</div>
    <div class="return-button" @click="returnToMainMenuView">Main Menu</div>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router';

  const router = useRouter();

  const props = defineProps({
    playerId: {
      type: String,
      required: true,
    }
  });
  
  const returnToMainMenuView = () => {
    router.push({name: 'MainMenu'});
  }
</script>

<style scoped>
  .text {
    margin: 50px 50px; 
    font-size: 36px;
    font-weight: bold;
  }

  .return-button {
    height: 50px;
    width: 200px;
    margin-top: 20px;
    margin-left: 100px;
    padding: 10px 20px;
    font-size: 18px;
    cursor: pointer;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    text-align: center;
  }
</style>