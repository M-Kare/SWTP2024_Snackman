import './assets/base.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createI18n } from 'vue-i18n'

import App from './App.vue'
import router from './router'

export const i18n = createI18n({
  legacy: false,
  locale: 'en' as 'en' | 'de',
  fallbackLocale: 'en' as 'en' | 'de',
  messages: {
    en: {
      message: {
        hello: 'hello world'
      }
    },
    de: {
      message: {
        hello: 'Hallo Welt'
      }
    }
  }
})

createApp(App)
  .use(router)
  .use(createPinia())
  .use(i18n)
  .mount('#app');
