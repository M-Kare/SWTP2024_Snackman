<template>
  <div v-if="show" id="darken-background"></div>
  <PopUp class="popup-box"
         v-if="show"
         @hidePopUp="$emit('close')">

    <p class="info-heading"> Leaderboard </p>
    <p class="info-text"> This is the current leaderboard: </p>
    <div class="table-container">
      <table>
        <thead>
        <tr>
          <td>Name</td>
          <td>Duration</td>
          <td>Date</td>
        </tr>
        </thead>
        <tbody>
        <tr v-for="entry in leaderboardEntries">
          <td>{{ entry.name }}</td>
          <td>{{ entry.duration }}</td>
          <td>{{ entry.releaseDate }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </PopUp>
</template>

<script setup lang="ts">
import PopUp from "@/components/PopUp.vue";
import {useLeaderboardStore} from "@/stores/Leaderboard/leaderboardStore";
import {computed, onMounted} from "vue";

const leaderboardStore = useLeaderboardStore()

onMounted(async () => {
  await leaderboardStore.initLeaderboardStore()
  await leaderboardStore.startLeaderboardUpdate()
})

const leaderboardEntries = computed(() => leaderboardStore.leaderboard.leaderboardEntries);

defineProps({
  show: {
    type: Boolean,
    required: true
  }
})

defineEmits(['close'])
</script>

<style scoped>

.info-heading {
  font-size: 3rem;
  font-weight: bold;
}

.info-text {
  font-size: 1.8rem;
  padding: 1.2rem;
}

.info-text {
  font-size: 1.8rem;
  padding: 1.2rem;
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

#popup-box {
  z-index: 10;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #ffffff;
  border-radius: 0.3rem;
  text-align: center;
  padding: 2rem;
  color: #000000;
  max-width: 90vw;
  max-height: 90vh;
  width: auto;
  height: auto;
}

table {
  table-layout: fixed;
  width: 100%;
  border-collapse: collapse;
  overflow-x: auto;
}

th,
td {
  padding: 5px;
}

.table-container {
  max-height: 400px;
  overflow-y: auto;
}

</style>
