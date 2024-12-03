<template>
    <div class="text"> Player {{ playerId }} started Game</div>
    <!-- TODO: replace with MenuBackButton -->
    <MenuBackButton class="return-button">Main Menu</MenuBackButton>
</template>

<script setup lang="ts">
  import MenuBackButton from '@/components/MenuBackButton.vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();

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

.back-button {
  
}
</style>