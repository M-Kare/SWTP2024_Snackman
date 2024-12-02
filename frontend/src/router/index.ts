import { createRouter, createWebHistory } from 'vue-router'
import MainMenuView from '@/views/MainMenuView.vue'
import GameView from '@/views/GameView.vue'
import LobbyView from '@/views/LobbyViews/LobbyView.vue'
import LobbyListView from '@/views/LobbyViews/LobbyListView.vue'


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'MainMenu',
            component: MainMenuView
        },
        {
            path: '/GameView',
            name: 'GameStart',
            component: GameView,
            props: true
        },
        {
            path: '/LobbyListView',
            name: 'LobbyList',
            component: LobbyListView,
            props: true
        },
        {
            path: '/LobbyView',
            name: 'Lobby',
            component: LobbyView,
            props: true
        }
    ]
})

export default router
