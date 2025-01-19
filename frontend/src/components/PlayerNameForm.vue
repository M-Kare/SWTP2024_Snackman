<template>
  <div class="overlay"></div>
  <div id="form-box">
    <form id="form" @submit.prevent="savePlayerName">
      <label>
        {{ $t('savePlayerName.label') }} 
      </label>
      <input
        v-model.trim="playerName"
        autofocus
        maxlength="16"
        placeholder="max. 16 characters"
        type="text">
      <p
        v-if="errorMessage"
        id="error-message">
        {{ errorMessage }}
      </p>
    </form>

    <SmallNavButton
      id="save-name-button"
      class="small-nav-button"
      @click="savePlayerName">
      {{ $t('button.save') }} 
    </SmallNavButton>
  </div>
</template>

<script setup lang="ts">
import SmallNavButton from '@/components/SmallNavButton.vue';
import {ref} from 'vue';
import {useLobbiesStore} from '@/stores/Lobby/lobbiesstore';
import {SoundManager} from "@/services/SoundManager";
import {SoundType} from "@/services/SoundTypes";
import { useI18n } from 'vue-i18n';

const lobbiesStore = useLobbiesStore();

const playerName = ref('');
const errorMessage = ref('');

const { t } = useI18n();

const emit = defineEmits<{
  (e: 'hidePlayerNameForm'): void;
  (e: 'playerNameSaved', value: string): void;
}>();


/**
 * Saves the name of a player.
 * Validates the admin client and playerName before attempting to save the playerName.
 * Alerts the user if there are any validation errors or if the save fails.
 * On success, hides the popup and shows the main menu.
 *
 * @async
 * @function savePlayerName
 * @throws {Error} Shows a popup if there is an error while saving the playerName.
 * @returns {void}
 */
const savePlayerName = async () => {
  if (!playerName.value.trim()) {
    errorMessage.value = t('savePlayerName.error.playerNameEmpty');
    return;
  }

  try {
    lobbiesStore.createPlayer(playerName.value);
    errorMessage.value = "";

    emit('hidePlayerNameForm');
    emit('playerNameSaved', playerName.value);

  } catch (error) {
    alert("Error saving playername");
    console.error(error);
  }

  try {
    await SoundManager.initBackgroundMusicManager()
    SoundManager.stopAllInGameSounds()
    SoundManager.playSound(SoundType.LOBBY_MUSIC)
  } catch (error) {
    console.error(error);
  }
}

</script>

<style scoped>
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #0000007F;
  backdrop-filter: blur(5px);
  z-index: 1;
}

#form-box {
  z-index: 2;
  position: absolute;
  left: 50%;
  top: 40%;
  transform: translateX(-50%);
  width: 60%;
  max-width: 600px;
  height: 12rem;
  border-radius: 0.3rem;
}

input::placeholder {
  color: var(--secondary-text-color);
  font-weight: bold;
}

#form-box {
  z-index: 2;
  position: absolute;
  left: 50%;
  top: 25%;
  transform: translateX(-50%);
  width: 70%;
  max-width: 600px;
  height: 30rem;

  background-image: url('@/assets/background-design-lobbies.png');
  background-size: cover;
  background-position: center;

  border: var(--background-for-text-color) solid 4px;
  border-radius: 0.5rem;
  box-shadow: 10px 8px 0 var(--background-for-text-color);
}

#form-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #000000dd;
  border-radius: 0.5rem;
}

#form {
  top: 35%;
  width: 100%;
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--background-for-text-color);
  display: flex;
  flex-direction: column;
  align-items: center;
}

#form > input {
  font-size: 1.2rem;
  width: 70%;
  height: 2rem;
  margin-top: 0.7rem;
  margin-bottom: 2rem;
  padding: 1.2rem;
}

#form > label {
  display: block;
  margin-bottom: 1rem;
  text-align: center;
}

#form > label > input {
  font-size: 1.2rem;
  width: 90%;
  height: 2rem;
  padding: 1.2rem;
}


#error-message {
  font-size: 1.1rem;
  font-style: italic;
  margin-top: -1.6rem;
  color: var(--accent-color);
}

#save-name-button {
  bottom: 15%;
  left: 50%;
  transform: translate(-50%);
  font-size: 1.1rem;
  font-weight: bold;
  padding: 0.5rem;
  height: auto;
  width: auto;
}

#save-name-button:hover {
  background-color: var(--primary-highlight-color);
}
</style>
