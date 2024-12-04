<template>
    <MenuBackground></MenuBackground>

    <h1 class="title">Lobbies</h1>
    <div class="outer-box">
        <MenuBackButton id="menu-back-button" class="nav-buttons">Back</MenuBackButton>
        <button id="create-lobbyNumber-button" class="nav-buttons" @click="createLobby">Create Lobby</button>

        <div class="inner-box"> <!-- :key for order? -->
            <ul>
                <li class="lobby-list-items" v-for="lobby in lobbies" @click="showTest">
                    <div class="lobby-name">
                        {{ lobby.lobbyNumber }}
                    </div>
                    <div class="playercount">
                        {{ lobby.playerCount }} / {{ maxPlayerCount }}
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import MenuBackButton from '@/components/MenuBackButton.vue';
    import { useRouter } from 'vue-router';
    import { ref } from 'vue';

    const router = useRouter();

    const createLobby = () => {
        router.push({name: 'LobbyView'});
    }

    const showTest = () => {
        alert("Test");
    }

    // TODO - Connection to Backend
    const maxPlayerCount = "4";
    const lobbies = ref([
        {lobbyNumber: "Lobby 1", playerCount: "1"},
        {lobbyNumber: "Lobby 2", playerCount: "3"},
        {lobbyNumber: "Lobby 3", playerCount: "4"},
        {lobbyNumber: "Lobby x", playerCount: "4"},
        {lobbyNumber: "Lobby x", playerCount: "4"},
        {lobbyNumber: "Lobby x", playerCount: "4"},
        {lobbyNumber: "Lobby x", playerCount: "4"},
        {lobbyNumber: "Lobby x", playerCount: "4"},
        {lobbyNumber: "Lobby x", playerCount: "4"},
        {lobbyNumber: "Lobby x", playerCount: "4"},
        {lobbyNumber: "Lobby x", playerCount: "4"},
        {lobbyNumber: "Lobby x", playerCount: "4"}
    ]);



</script>

<style scoped>
:root {
    --button-bottom-spacing: ;
}

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
    top: 12%;
    left: 50%;
    transform: translateX(-50%);
    width: 70%;
    max-width: 1000px;
    height: 65%;
    background: rgba(255, 255, 255, 60%);
    border-radius: 0.5rem;
}

.inner-box {
    position: absolute;
    top: 5%;
    left: 50%;
    transform: translateX(-50%);
    width: 90%;
    height: 80%;
    background: rgba(255, 255, 255, 70%);
    border-radius: 0.3rem;
    overflow-y: scroll;
}

.inner-box > ul {
    list-style: none;
    left: 50%;
    transform: translateX(-50%);
    margin: 0;
    padding: 0;
    vertical-align: middle;
}

.lobby-list-items {
    display: flex;
    justify-content: space-between;
    border: 0.5px solid black;
    border-radius: 0.2rem;
    font-size: 1.2rem;
    padding: 0.5rem;
    margin: 1rem;
}

.nav-buttons {
    position: inherit;
    bottom: 3%;
    font-size: 1.2rem;
    width: auto;
    padding: 1rem;
    height: 3.5rem;
    cursor: pointer;
    border: none;
    border-radius: 0.3rem;
}

#menu-back-button {
    left: 5%;
}

#create-lobbyNumber-button {
    right: 5%;
}

#create-lobbyNumber-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>