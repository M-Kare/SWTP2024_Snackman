<template>
    <button class="menu-button" id="back-button" @click="backToMain">Back</button>
</template>

<script setup lang="ts">
    import { useRouter } from 'vue-router';

    const router = useRouter();

    const backToMain = () => {
        router.push({name: 'MainMenu'});
    }

</script>

<style scoped>
#back-button {
    position: absolute;
    top: 70%;
    left: 20%;
    transform: translateX(-50%);
    text-align: center;
}
</style>