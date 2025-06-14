import { createApp } from 'vue';
import App from './App.vue';
import router from './routes';
import './assets/styles/global.css';
import 'swiper/swiper-bundle.css';
import { createPinia } from 'pinia';

const fontAwesomeLink = document.createElement('link');
fontAwesomeLink.rel = 'stylesheet';
fontAwesomeLink.href = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css';
document.head.appendChild(fontAwesomeLink);

// Criação da app
const app = createApp(App);

// Criar e usar o Pinia
const pinia = createPinia();
app.use(pinia); // <-- Esta linha é essencial

// Usar o router
app.use(router);

// Montar a aplicação
app.mount('#app');
