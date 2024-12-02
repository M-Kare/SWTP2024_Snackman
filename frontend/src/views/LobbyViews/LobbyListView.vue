<template>
    <MenuBackground></MenuBackground>

    <h1 class="title">Lobbies</h1>
    <MenuBackButton>Back</MenuBackButton>
</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import MenuBackButton from '@/components/MenuBackButton.vue';
    import { useRouter } from 'vue-router';

    const router = useRouter();
</script>

<style scoped>
.title {
    position: absolute;
    top: 50px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 3rem;
    font-weight: bold;
    color: #fff;
    text-align: center;
    z-index: 3;
}
</style>