import { createRouter, createWebHistory } from 'vue-router'
import MainMenuView from '@/views/MainMenuView.vue'
import GameView from '@/views/GameView.vue'
import LobbyListView from '@/views/LobbyViews/LobbyListView.vue'
import LobbyView from '@/views/LobbyViews/LobbyView.vue'


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
            name: 'GameView',
            component: GameView,
            props: true
        },
        {
            path: '/LobbyListView',
            name: 'LobbyListView',
            component: LobbyListView,
            props: true
        },
        {
            path: '/LobbyView/:lobbyId',
            name: 'LobbyView',
            component: LobbyView,
            props: true
        }
    ]
})

export default router
