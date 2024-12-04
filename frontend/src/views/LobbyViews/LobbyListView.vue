<template>
    <MenuBackground></MenuBackground>

    <!-- <Test></Test> -->

    <h1 class="title">Lobbies</h1>
    <div class="outer-box">
        <SmallNavButton
            id="menu-back-button"
            class="nav-buttons"
            @click="backToMainMenu">
            
            Back
        </SmallNavButton>
        <SmallNavButton
            id="show-lobby-creation-button"
            class="nav-buttons"
            @click="showCreateLobbyForm">

            Create new Lobby
        </SmallNavButton>

        <div class="inner-box"> <!-- :key for order? -->
            <ul>
                <li
                    v-for="lobby in lobbies"
                    class="lobby-list-items"
                    @click="showTest">

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

    <div v-if="showNewLobbyForm" id="darken-background"></div>

    <CreateLobbyForm
        v-if="showNewLobbyForm"
        @cancelLobbyCreation = "cancelLobbyCreation">
    </CreateLobbyForm>

</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import CreateLobbyForm from '@/components/CreateLobbyForm.vue';

    import { useRouter } from 'vue-router';
    import { ref } from 'vue';

    const router = useRouter();
    const showNewLobbyForm = ref(true); // TODO - change to false, true for testing

    const backToMainMenu = () => {
        router.push({name: "MainMenu"});
    }

    const showCreateLobbyForm = () => {
        showNewLobbyForm.value = true;
    }

    const cancelLobbyCreation = () => {
        showNewLobbyForm.value = false;
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

#darken-background {
    z-index: 1;
    position: fixed;
    top: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 50%);

    transition: background 0.3s ease;
}

#menu-back-button {
    left: 5%;
}

#show-lobby-creation-button {
    right: 5%;
}

#menu-back-button:hover, #show-lobby-creation-button:hover {
  box-shadow: 0px 0px 35px 5px rgba(255, 255, 255, 0.5);
}
</style>