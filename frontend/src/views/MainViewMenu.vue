<template>
    <div>
        <button @click="startGame">Spiel Starten</button>
    </div>
</template>

<script setup lang="ts">
    import { useRouter } from 'vue-router';

    const router = useRouter();

    const startGame = () => {
        const playerId = 1;
        router.push({name: 'GameStart', params: { playerId }});
    }
</script>

<style>
    button{
        position: absolute;
        top: 300px;        
        left: 50%; 
        z-index: 10;
        padding: 10px 20px;
        font-size: 36px;
        background: #1261a6;
        color: aliceblue;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }
</style>