import { createRouter, createWebHistory } from 'vue-router'
import MainMenuView from '@/views/MainMenuView.vue'
import GameView from '@/views/GameView.vue'
import EndGameView from '@/views/EndGameView.vue'


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
            path: '/EndGameView',
            name: 'EndGameView',
            component: EndGameView,
            props: true
        }
    ]
})

export default router
