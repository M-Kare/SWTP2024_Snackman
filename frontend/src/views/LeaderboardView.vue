<template>
  <ViewBackground></ViewBackground>
  <div id="individual-outer-box-size" class="outer-box">
    <h1 class="info-heading">Leaderboard</h1>
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <td></td>
            <td>Name</td>
            <td>Date</td>
            <td>Duration</td>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(entry, index) in leaderboardEntries">
            <td>{{ index + 1 }}</td>
            <td>{{ entry.name }}</td>
            <td>{{ entry.releaseDate }}</td>
            <td>{{ entry.duration }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div id="button-pair">
      <SmallNavButton
        id="menu-back-button"
        class="small-nav-buttons"
        @click="backToMainMenu"
      >
        Back to main menu
      </SmallNavButton>
    </div>
  </div>
  <img
    id="snackman"
    :src="
      gameResult === 'SNACKMAN'
        ? '/src/assets/characters/kirby.png'
        : '/src/assets/characters/kirby-monochrome.png'
    "
    alt="representation of snackman"
    class="character-image"
  />
  <img
    id="ghost"
    :src="
      gameResult === 'GHOST'
        ? '/src/assets/characters/ghost.png'
        : '/src/assets/characters/ghost-monochrome.png'
    "
    alt="representation of ghost"
    class="character-image"
  />
</template>

<script lang="ts" setup>
import {useLeaderboardStore} from '@/stores/Leaderboard/leaderboardStore'
import {computed, onMounted, ref} from 'vue'
import SmallNavButton from '@/components/SmallNavButton.vue'
import {useRoute, useRouter} from 'vue-router'
import ViewBackground from "@/components/ViewBackground.vue";

const leaderboardStore = useLeaderboardStore()
const router = useRouter()
const route = useRoute()
const gameResult = ref<'SNACKMAN' | 'GHOST' | null>(null)

const backToMainMenu = () => {
  router.push({ name: 'MainMenu' })
}

/**
 * Load the leaderboard data, initialize stomp message updates and look for winner
 */
onMounted(async () => {
  await leaderboardStore.initLeaderboardStore()
  await leaderboardStore.startLeaderboardUpdate()

  const winningRole = route.query.winningRole as string | undefined

  switch (winningRole) {
    case 'SNACKMAN':
      gameResult.value = 'SNACKMAN'
      break
    case 'GHOST':
      gameResult.value = 'GHOST'
      break
    default:
      gameResult.value = null
      break
  }
})

const leaderboardEntries = computed(
  () => leaderboardStore.leaderboard.leaderboardEntries,
)
</script>

<style scoped>
.info-heading {
  font-size: 3rem;
  font-weight: bold;
  color: var(--background-for-text-color);
  text-align: center;
}

#individual-outer-box-size {
  top: 10%;
  width: 70vw;
  max-width: 1000px;
  height: 40rem;
  max-height: 50rem;
  padding: 30px;
}

table {
  table-layout: fixed;
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 15px;
  overflow-x: auto;
}

tr {
  font-size: 1.2rem;
}

thead tr td {
  background: transparent;
  color: var(--background-for-text-color);
  font-weight: bold;
  border: none;
  padding: 0 10px;
}

thead tr td {
  background: transparent;
  vertical-align: bottom;
  color: var(--background-for-text-color);
  font-weight: bold;
  border: none;
  padding: 5px 10px;
  box-shadow: none;
}

thead tr td:first-child,
thead tr td:last-child {
  border: none;
}

thead tr td:not(:first-child):not(:last-child) {
  border: none;
}

tbody tr td {
  margin: 5px 0;
  padding: 5px 10px;
  background: var(--background-for-text-color);
  box-shadow: 10px 8px 0 var(--primary-text-color);
  color: var(--primary-text-color);
}

tr td:first-child,
td:last-child {
  border: 4px solid var(--primary-text-color);
}

tr td:first-child {
  width: 7%;
  text-align: right;
  border-right: none;
  border-top-left-radius: 0.1rem;
  border-bottom-left-radius: 0.1rem;
}

tr td:last-child {
  width: 15%;
  text-align: center;
  border-left: none;
  border-top-right-radius: 0.1rem;
  border-bottom-right-radius: 0.1rem;
}

tr td:nth-child(3) {
  width: 20%;
  text-align: center;
}

tr td:not(:first-child):not(:last-child) {
  border-top: 4px solid var(--primary-text-color);
  border-bottom: 4px solid var(--primary-text-color);
}

.table-container {
  max-height: 400px;
  overflow-y: auto;
}

#button-pair {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 2em;
}

#menu-back-button:hover {
  background: var(--primary-highlight-color);
}

.character-image {
  position: absolute;
  bottom: 3%;
  width: 280px;
  height: auto;
  z-index: 10;
}

#snackman {
  left: 6%;
}

#ghost {
  right: 6%;
}
</style>
