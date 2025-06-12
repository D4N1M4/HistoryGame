import {createRouter, createWebHashHistory} from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import FavoritosPage from '@/views/FavoritosPage.vue';
import JogosPage from '@/views/JogosView.vue';
import SobrePage from '@/views/SobrePage.vue';
import TelaLogin from '@/views/TelaLogin.vue';
import TelaRegistro from '@/views/RegistroView.vue';
import ForgotPassword from '@/views/ForgotPassword.vue';
import ProfileView from '@/views/ProfileView.vue';
import Comment from '../components/commentComponent.vue'
import EditarJogoPage from '@/views/EditarJogoPage.vue';


const routes = [
  { path: '/', component: HomePage },
  { path: '/jogos', component: JogosPage},
  { path: '/favoritos', component: FavoritosPage },
  { path: '/sobre', component: SobrePage},
  { path: '/login', component: TelaLogin},
  { path: '/registro', component: TelaRegistro},
  {path: '/recuperarsenha', component: ForgotPassword},
  {
    path: '/jogo/:id',
    name: 'DetalhesPage',
    component: () => import('@/views/DetalhesPage.vue')
  },
  {path: '/perfil', component: ProfileView },
  {path: '/comentario', component: Comment},
  {
    path: '/admin/jogos/adicionar',
    name: 'AdicionarJogo',
    component: () => import('@/views/AdicionarJogoPage.vue'),
    meta: { requiresAdmin: true }  // opcional para proteger a rota
  },
  {
    path: '/admin/jogos/editar/:id',
    name: 'EditarJogo',
    component: EditarJogoPage,
    meta: { requiresAuth: true, requiresAdmin: true }
  }

];

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

export default router;
