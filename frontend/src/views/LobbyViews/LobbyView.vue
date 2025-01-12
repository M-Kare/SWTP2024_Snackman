<template>
    <MenuBackground></MenuBackground>
    <h1 class="title"> {{ lobby?.name || 'Lobby Name' }} </h1>
    <div class="outer-box">
        <SmallNavButton
            id="menu-back-button"
            class="small-nav-buttons"
            @click="leaveLobby">

            Leave Lobby
        </SmallNavButton>
        <SmallNavButton
            id="start-game-button"
            class="small-nav-buttons"
            @click="startGame">

            Start Game
        </SmallNavButton>

        <div id="player-count">
                {{ playerCount }} / {{ MAX_PLAYER_COUNT }} Players
        </div>

        <div class="inner-box">
            <ul>
                <li
                    v-for="member in members"
                    class="player-list-items">

                    <div class="player-name">
                        {{ member.playerName }}
                    </div>

                    <div class="player-character">
                        {{ member.role }}
                    </div>

                </li>
            </ul>

        </div>
        <SmallNavButton
            id="menu-back-button"
            class="small-nav-buttons"
            @click="leaveLobby">

            Leave Lobby
        </SmallNavButton>
        <SmallNavButton
            id="start-game-button"
            class="small-nav-buttons"
            @click="startGame">

            Start Game
        </SmallNavButton>
        <SmallNavButton
        id="copyToClip"
        class="small-nav-buttons"
        @click="copyToClip()">
            Copy Link
        </SmallNavButton>
    </div>

    <div v-if="darkenBackground" id="darken-background"></div>

    <PopUp class="popup-box"
        v-if="errorBox"
        @hidePopUp="hidePopUp"
        @click="backToLobbyListView()"
        >

        <p class="info-heading"> - {{ infoHeading }} -  </p>
        <p class="info-text"> {{ infoText }} </p>
    </PopUp>

    <PopUp class="popup-box"
        v-if="showPopUp"
        @hidePopUp="hidePopUp">

        <p class="info-heading"> - Can't start the game -  </p>
        <p class="info-text"> {{ infoText }} </p>
    </PopUp>

    <div id="infoBox" v-show="showInfo"> {{ infoText }}</div>

</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import PopUp from '@/components/PopUp.vue';

    import { useRoute, useRouter } from 'vue-router';
    import { computed, onMounted, ref, watchEffect } from 'vue';
    import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore';
    import type { IPlayerClientDTD } from '@/stores/Lobby/IPlayerClientDTD';
    import type { ILobbyDTD } from '@/stores/Lobby/ILobbyDTD';

    const router = useRouter();
    const route = useRoute();
    const lobbiesStore = useLobbiesStore();

    const lobbyUrl = route.params.lobbyId
    let lobbyLoaded = false
    let lobby = computed(() => lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === lobbyUrl));
    const members = computed(() => lobby.value?.members || [] as Array<IPlayerClientDTD>);
    const playerCount = computed(() => members.value.length);

    const darkenBackground = ref(false);
    const showPopUp = ref(false);
    const  errorBox = ref(false);
    const infoText = ref();
    const infoHeading = ref();
    const showInfo = ref(false);

    const mouseX = ref(0);
    const mouseY = ref(0)

    const mouseInfoBox = ref(document.getElementById("infoBox"))


    const MAX_PLAYER_COUNT = 5;

    const TIP_TOP_DIST = 30;
    const TIP_SIDE_DIST = 20;

    const hidePopUp = () => {
        showPopUp.value = false;
        darkenBackground.value = false;
    }

    watchEffect(() => {
        if (lobbiesStore.lobbydata && lobbiesStore.lobbydata.lobbies) {

            const updatedLobby = lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === lobbyUrl);
            if (updatedLobby) {
                lobbyLoaded = true
                if (updatedLobby.gameStarted){
                    router.push({ 
                        name: 'GameView', 
                        query: { 
                            role: lobbiesStore.lobbydata.currentPlayer.role,
                            lobby: lobbiesStore.lobbydata.currentPlayer.joinedLobbyId,
                        } 
                    });
                }
            }
            else if(lobbyLoaded){
                router.push({ name: 'LobbyListView' });
            }
        }
    });

    onMounted(async () => {
        await lobbiesStore.fetchLobbyList()

        if(!lobby.value){
            infoHeading.value = "Lobby does not exist"
            infoText.value = "Please choose or create another one!"
            errorBox.value = true;
        }
        await lobbiesStore.startLobbyLiveUpdate();
        if (!lobbiesStore.lobbydata.currentPlayer || lobbiesStore.lobbydata.currentPlayer.playerId === '' || lobbiesStore.lobbydata.currentPlayer.playerName === '') {
            if(lobby.value!.members.length >= MAX_PLAYER_COUNT){
                infoHeading.value = "Lobby full"
                infoText.value = "Please choose or create another one!"
                errorBox.value = true
                darkenBackground.value = true
            } else {
                await lobbiesStore.createPlayer("Mr. Late");
                await joinLobby(lobby.value!)
            }
        }

    })

    const joinLobby = async (lobby: ILobbyDTD) => {

        // if(lobby.members.length >= 4){
        //     showPopUp.value = true;
        //     darkenBackground.value = true;
        //     return;
        // }

        try{
            const joinedLobby = await lobbiesStore.joinLobby(lobby.lobbyId, lobbiesStore.lobbydata.currentPlayer.playerId);

            if(joinedLobby) {
                router.push({ name: "LobbyView", params: { lobbyId: lobby.lobbyId } });
            }
        } catch (error: any){
            console.error('Error:', error);
        }
    }

    function backToLobbyListView(){
        router.push({ name: "LobbyListView"})
    }

    /**
     * Leaves the current lobby. If the player is the admin, it will remove other members from the lobby first.
     * After leaving the lobby, the user is redirected to the Lobby List View.
     *
     * @async
     * @function leaveLobby
     * @throws {Error} If the player or lobby is not found.
     */
    const leaveLobby = async () => {
        const playerId = lobbiesStore.lobbydata.currentPlayer.playerId;
        if (!playerId || !lobby.value) {
            console.error('Player or Lobby not found');
            return;
        }

        if(playerId === lobby.value.adminClient.playerId){
            for (const member of lobby.value.members) {
                if (member.playerId !== playerId) {
                    await lobbiesStore.leaveLobby(lobby.value.lobbyId, member.playerId);
                }
            }
        }

        await lobbiesStore.leaveLobby(lobby.value.lobbyId, playerId);
        router.push({ name: 'LobbyListView' });
    }

    /**
     * Starts the game if the player is the admin and there are enough members in the lobby.
     * If the player is not the admin or there are not enough members, a popup will be shown.
     *
     * @async
     * @function startGame
     * @throws {Error} If the player or lobby is not found.
     */
    const startGame = async () => {
        const playerId = lobbiesStore.lobbydata.currentPlayer.playerId;

        if (!playerId ||!lobby.value) {
            console.error('Player or Lobby not found');
            return;
        }

        if(lobby.value.members.length < 2){
            showPopUp.value = true;
            darkenBackground.value = true;
            infoText.value = "Not enough players to start the game!"
        }

        if(playerId === lobby.value.adminClient.playerId){
            await lobbiesStore.startGame(lobby.value.lobbyId);
        } else {
            showPopUp.value = true;
            darkenBackground.value = true;
            infoText.value = "Only SnackMan can start the game!"
        }
    }

    function copyToClip(){
        navigator.clipboard.writeText(document.URL);
        infoText.value = "Link copied to clipboard"
        showInfo.value = true;
        mouseInfoBox.value = document.getElementById("infoBox")
        moveToMouse(mouseInfoBox.value!)
        setTimeout(()=>{
            showInfo.value = false
        },
        1000)
    }

    window.onmousemove = function(e) {
        mouseX.value = e.clientX
        mouseY.value = e.clientY
        if(showInfo.value){
            moveToMouse(mouseInfoBox.value!)
        }
    }

    function moveToMouse(element: HTMLElement){
        const offset = mouseInfoBox.value!.parentElement!.getBoundingClientRect();
        element.style.top = (mouseY.value - offset.top - TIP_TOP_DIST) + 'px';
        element.style.left = (mouseX.value - offset.left + TIP_SIDE_DIST) + 'px';
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
    height: 35rem;
    max-height: 45rem;
    background: rgba(255, 255, 255, 60%);
    border-radius: 0.5rem;
}

#infoBox {
    position: absolute;
    border-radius: 0.5rem;
    background: rgba(255, 255, 255, 60%);
    color: #000000;
    padding-left: 0.5rem;
    padding-right: 0.5rem;
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

.hidden {
    display: none;
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

#copyToClip{
    top: 3%;
    right: 1.5%;
    width: 8%;
    height: 3rem;
    font-size: 0.8rem;
    padding: 0;
}

.info-heading {
    font-size: 3rem;
    font-weight: bold;
}

.info-text {
    font-size: 1.8rem;
    padding: 1.2rem;
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

#darken-background {
    z-index: 1;
    position: fixed;
    top: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 50%);

    transition: background 0.3s ease;
}

</style>
