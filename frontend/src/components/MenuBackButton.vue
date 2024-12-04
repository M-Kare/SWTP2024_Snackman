<template>
    <button class="back-button" @click="backToMain">Back</button>
</template>

<script setup lang="ts">
    import { useRouter } from 'vue-router';

    const router = useRouter();

    const backToMain = () => {
        router.push({name: 'MainMenu'});
    }

</script>

<style scoped>
.back-button {
    position: absolute;
    text-align: center;
    cursor: pointer;
    border: none;
    border-radius: 0.3rem;
}
.back-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.2);
}
</style>