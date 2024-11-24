import { createRouter, createWebHistory } from 'vue-router'
import MainMenuView from '@/views/MainMenuView.vue'
import GameView from '@/views/LobbyView.vue'


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'MainMenu',
            component: MainMenuView
        },
        {
            path: '/player/:playerId',
            name: 'GameStart',
            component: GameView,
            props: true
        }
    ]
})

export default router
