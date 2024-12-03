<template>
    <MenuBackground></MenuBackground>

    <h1 class="title">Lobbies</h1>
    <div class="outer-box">
        <MenuBackButton class="back-button">Back</MenuBackButton>
        <button id="create-lobbyNumber-button" @click="createLobby">Create Lobby</button>
    </div>
    <div class="inner-box" v-for="lobby in lobbies"> <!-- :key for order? -->
        <ul>
            <li class="lobby-list" v-for="lobby in lobbies">
                <div class="lobby-name">
                    {{ lobby.lobbyNumber }}
                </div>
                <div class="playercount">
                    {{ lobby.playerCount }} / {{ maxPlayerCount }}
                </div>
            </li>
        </ul>
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

    // TODO - Connection to Backend
    const maxPlayerCount = "4";
    const lobbies = ref([
        {lobbyNumber: "Lobby 1", playerCount: "1"}, {lobbyNumber: "Lobby 2", playerCount: "3"}, {lobbyNumber: "Lobby 3", playerCount: "4"}
    ]);



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
}

.outer-box {
    position: absolute;
    top: 180px;
    left: 50%;
    transform: translateX(-50%);
    width: 1000px;
    height: 750px;
    background: rgba(255, 255, 255, 50%);
    border-radius: 0.5rem;
}

.inner-box {
    position: absolute;
    top: 200px;
    left: 50%;
    transform: translateX(-50%);
    padding: 10px auto;
    width: 960px;
    height: 630px;
    background: rgba(255, 255, 255, 15%);
    border-radius: 0.3rem;
}
.inner-box > ul {
    list-style: none;
}

.lobby-list {

}

/* TODO: flexible position needed */
.lobby-name {
    position: inherit;
}

/* TODO: flexible position needed */
.playercount {
    position: inherit;
    right: 30%;
}

/* TODO: flexible position needed */
.back-button {
    top: 84%;
    left: 23.25%;
    font-size: 1.2rem;
    width: 7rem;
    height: 3.5rem;
    cursor: pointer;
}

/* TODO: flexible position needed */
#create-lobbyNumber-button {
    position: absolute;
    top: 84%;
    right: 23.25%;
    font-size: 1.2rem;
    width: 9rem;
    height: 3.5rem;
    cursor: pointer;
    border: none;
    border-radius: 0.3rem;
}

#create-lobbyNumber-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>