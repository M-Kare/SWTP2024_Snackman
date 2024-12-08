<template>
    <MenuBackground></MenuBackground>

    <h1 class="title"> {{ lobbyName }} </h1>
    <div class="outer-box">
        <SmallNavButton
            id="menu-back-button"
            class="small-nav-buttons"
            @click="backToLobbyList">
            
            Back to Lobbies
        </SmallNavButton>
        <SmallNavButton
            id="start-game-button"
            class="small-nav-buttons"
            @click="startGame">

            Start Game
        </SmallNavButton>

        <div id="player-count">
                {{ playerCount }} / {{ maxPlayerCount }} Players
        </div>

        <div class="inner-box">
            <ul>
                <li
                    v-for="player in players"
                    class="player-list-items">

                    <div class="player-name">
                        {{ player.name }}
                    </div>

                    <div class="player-character">
                        {{ player.character }}
                    </div>

                </li>
            </ul>

        </div>
    </div>

</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';

    import { useRouter } from 'vue-router';
    import { ref } from 'vue';

    const router = useRouter();

    // Backend-Connection
    const lobbyName = ref('LobbyName'); // TODO - Placeholder
    const playerCount = ref(3);
    const maxPlayerCount = ref(4);
    const players = ref([
        {name: "Player 1", character: "Snackman"},
        {name: "Player 2", character: "Ghost"},
        {name: "Player 3", character: "Ghost"},
        {name: "Player 4", character: "Ghost"}
    ]);

    const backToLobbyList = () => {
        router.push({name: "LobbyList"});
    }

    const startGame = () => {
        router.push({name: "GameStart"});
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
    height: 500px;
    max-height: 45vh;
    background: rgba(255, 255, 255, 60%);
    border-radius: 0.5rem;
}

#player-count {
    top: 1%;
    left: 50%;
    transform: translateX(-50%);
    text-align: center;
    font-size: 1.8rem;
    font-weight: bold;
    color: #000000;
    padding: 1rem;
}

.inner-box {
    position: relative;
    margin-top: 1vh;
    margin-bottom: 1vh;
    left: 50%;
    transform: translateX(-50%);
    width: 90%;
    height: auto;
    background: rgba(255, 255, 255, 70%);
    border-radius: 0.3rem;
    color: #000000;
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

.player-list-items {
    display: flex;
    justify-content: space-between;
    border: 0.5px solid black;
    border-radius: 0.2rem;
    font-size: 1.2rem;
    padding: 0.5rem;
    margin: 0.6rem;
}

.small-nav-buttons {
    bottom: 3%;
    font-weight: bold;
}

#menu-back-button {
    left: 5%;
}

#start-game-button {
    right: 5%;
}

#menu-back-button:hover, #start-game-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}

</style>