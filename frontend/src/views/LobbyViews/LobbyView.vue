<template>
    <MenuBackground></MenuBackground>
    <h1 class="title"> {{ lobby?.name || 'Lobby Name' }} </h1>

    <div v-if="playerId === adminClientId" class="custom-map">
        <span>Custom Map</span>
        <CustomMapButton :toggleState="toggleState" @updateToggleState="updateToggleState"></CustomMapButton>
    </div>
    
    <div v-if="toggleState" class="map-importiren">
        <MapButton @click="triggerFileInput">Map Importieren</MapButton>
        <input class="input-feld"
            ref="fileInput" 
            type="file" 
            accept=".txt" 
            @change="handleFileImport"
        />

        <div class="map-list" v-for="map in mapList" :key="map.mapName">
            <div class="map-list-item">
                <input class="map-choose" 
                    type="radio" 
                    :value="map.mapName" 
                    :checked="selectedMap === map.mapName" 
                    @change="selectMap(map.mapName)"
                />
                <span>{{ map.mapName }}</span>
            </div>
        </div>

        <div v-if="feedbackMessage" :class="['feedback-message', feedbackClass]">
            {{ feedbackMessage }}
        </div>
    </div>

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
                {{ playerCount }} / {{ maxPlayerCount }} Players
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

        <p class="info-heading"> {{ infoHeading }}  </p>
        <p class="info-text"> {{ infoText }} </p>
    </PopUp>

    <div id="infoBox" v-show="showInfo"> {{ infoText }}</div>

</template>

<script setup lang="ts">
    import MenuBackground from '@/components/MenuBackground.vue';
    import SmallNavButton from '@/components/SmallNavButton.vue';
    import PopUp from '@/components/PopUp.vue';
    import CustomMapButton from '@/components/CustomMapButton.vue';
    import MapButton from '@/components/MapButton.vue';

    import { useRoute, useRouter } from 'vue-router';
    import { computed, onMounted, ref, watchEffect } from 'vue';
    import { useLobbiesStore } from '@/stores/Lobby/lobbiesstore';
    import type { IPlayerClientDTD } from '@/stores/Lobby/IPlayerClientDTD';
    import type { ILobbyDTD } from '@/stores/Lobby/ILobbyDTD';
    import LobbyListView from './LobbyListView.vue';

    const router = useRouter();
    const route = useRoute();
    const lobbiesStore = useLobbiesStore();

    const playerId = lobbiesStore.lobbydata.currentPlayer.playerId;
    const lobbyId = route.params.lobbyId as string;

    let lobby = computed(() => lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === route.params.lobbyId));
    const adminClientId = lobby.value?.adminClient.playerId;
    const members = computed(() => lobby.value?.members || [] as Array<IPlayerClientDTD>);
    const playerCount = computed(() => members.value.length);
    const maxPlayerCount = ref(5);

    const darkenBackground = ref(false);
    const showPopUp = ref(false);
    const  errorBox = ref(false);
    const infoText = ref();
    const infoHeading = ref();
    const showInfo = ref(false);

    const mouseX = ref(0);
    const mouseY = ref(0)

    const mouseInfoBox = ref(document.getElementById("infoBox"))


    const MAX_PLAYER_COUNT = 4;

    const TIP_TOP_DIST = 30;
    const TIP_SIDE_DIST = 20;

    const hidePopUp = () => {
        showPopUp.value = false;
        darkenBackground.value = false;
    }

    const mapList = ref<{ mapName: string; fileName: string }[]>([
        { mapName: `Maze`, fileName: `Maze.txt` },
    ]);

    const selectedMap = ref<string | null>(null);

    const selectMap = (mapName: string) => {
        selectedMap.value = mapName;
    };

    const feedbackMessage = ref('');
    const feedbackClass = ref('');
    const toggleState = ref(false);

    const updateToggleState = (state: boolean) => {
        toggleState.value = state;
    }

    const triggerFileInput = () => {
        fileInput.value?.click();
    }

    const fileInput = ref<HTMLInputElement | null>(null);

    /**
     * This function processes the file selected by the user in an input field. 
     * It ensures the file is a `.txt` file
     * and validates its content to match a specific pattern for render map 
     * If the file is valid, it triggers an upload to the server.
     * Otherwise, it displays a popup with error information.
     * 
     * @param event - The event triggered by the file input change.
     */
    const handleFileImport = (event: Event) => {
        const input = event.target as HTMLInputElement;

        if (input.files && input.files.length > 0) {
            const file = input.files[0];
            if (file.name.endsWith('.txt')) {
                const reader = new FileReader();
                reader.onload = () => {
                    const fileContent = reader.result as string;
                    const validPattern = /^[SGCo#\s]*$/;

                    if (validPattern.test(fileContent)){
                        console.log('File Content:\n', fileContent);
                        uploadFileToServer(file, lobbyId);
                    } else {
                        showPopUp.value = true;
                        darkenBackground.value = true;
                        infoHeading.value = "- File Map is not valid -"
                        infoText.value = "The map file is only allowed to contain the following characters: \nS, G, C, o, #, and spaces."
                    }
                };
                reader.readAsText(file);
            } else {
                showPopUp.value = true;
                darkenBackground.value = true;
                infoHeading.value = "- File Map is not valid -"
                infoText.value = "Please upload file .txt"
            }
        }
    }

    /**
     * Upload file to server
     * @param file 
     */
    const uploadFileToServer = (file: File, lobbyId: string) => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('lobbyId', lobbyId);

        fetch('/api/upload', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                // Success feedback
                feedbackMessage.value = 'Map saved';
                feedbackClass.value = 'success';

                const mapName = 'Snack Man Map';
                const fileName = `SnackManMap_${lobbyId}.txt`;

                if (mapList.value.length > 1) {
                    mapList.value[1] = { mapName, fileName };
                } else {
                    mapList.value.push({ mapName, fileName });
                }

                selectedMap.value = mapName;
            } else {
                // Failure feedback
                feedbackMessage.value = 'Map not saved';
                feedbackClass.value = 'error';
            }
        })
        .catch(error => {
            console.error('Error uploading file:', error);
            // Failure feedback
            feedbackMessage.value = 'Map not saved';
            feedbackClass.value = 'error';
        });

        // Clear feedback after 3 seconds
        setTimeout(() => {
            feedbackMessage.value = '';
            feedbackClass.value = '';
        }, 3000);
    }

    /**
     * Delete uploaded File, when the lobby doesn't exist anymore.
     * @param lobbyId 
     */
    const deleteUploadedFile = async (lobbyId: string) => {
        const formData = new FormData();
        formData.append('lobbyId', lobbyId);

        fetch('/api/deleteMap', {
            method: 'DELETE',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                console.error('Error deleting file:', response.text());
            }
            else {
                console.log('File deleted successfully');
            }
        })
        .catch(error => {
            console.error('Error deleting file:', error);
        });
    }

    watchEffect(() => {
        if (lobbiesStore.lobbydata) {
            const lobbyId = route.params.lobbyId as string;
            
            const updatedLobby = lobbiesStore.lobbydata.lobbies.find(l => l.lobbyId === lobbyId);

            if (updatedLobby) {
                if (updatedLobby.gameStarted){
                    router.push({ 
                        name: 'GameView', 
                        query: { role: lobbiesStore.lobbydata.currentPlayer.role } 
                    });
                }
            }
            else {
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
            alert("Error join Lobby!");
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
            // If Admin-Player leave Lobby, delete the uploaded map
            deleteUploadedFile(lobbyId);
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
            infoHeading.value = "- Can't start the game -"
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

.custom-map{
  position: absolute;
  right: 20px;
  top: 30px;
  display: flex;
  gap: 25px;
  justify-content: center;
  align-items: center;
  font-size: 3rem;
  color: #fff;
  z-index: 3;
}

.map-importiren{
  position: absolute;
  right: 20px;
  top: 150px;
  z-index: 3;
}

.map-list{
    font-size: 2rem;
    margin-top: 30px;
    gap: 25px;
    justify-content: center;
    align-items: center;
}

.map-choose{
    width: 20px;
    height: 20px;
    transform: scale(1.5);
    margin-right: 10px; 
}

.input-feld{
  display: none;
}

.feedback-message {
    margin-top: 30px;
    font-size: 2rem;
    font-weight: bold;
    padding: 10px 20px;
    border-radius: 5px;
    text-align: center;
    animation: fadeIn 0.5s;
}

.feedback-message.success {
    color: #fff;
    background-color: #50C878;
}

.feedback-message.error {
    color: #fff;
    background-color: #C70039;
}

/* Fade-in animation */
@keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
}
</style>
