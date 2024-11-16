<template>
    <div class="text"> Player {{ playerId }} started Game</div>
</template>

<script setup lang="ts">
    const props = defineProps({
        playerId: {
            type: String,
            required: true,
        }
    });
</script>

<style scoped>
  .text {
    margin: 50px 50px; 
    font-size: 36px;
    font-weight: bold;
  }
</style>