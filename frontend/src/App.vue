<template>
  <nav>
    <!--Das kann gelöscht werden. Benutzen nur für die Testing.-->
    <router-link to="/">Hauptmenüseite</router-link>
  </nav>
  <main>
    <router-view></router-view>
  </main>
</template>

<script setup lang="ts">
  import GuckDing from './views/MainMenuView.vue'
</script>
