import { defineStore } from 'pinia';
import api from '@/assets/js/axios';
import router from '@/routes/index'; // Importando o router

export const useAuthStore = defineStore('auth', {
  state: () => ({
    autenticado: false,
    usuario: null,
  }),
  getters: {
    isAdmin(state) {
      return state.usuario?.roles?.includes('ROLE_ADMIN'); // CORRIGIDO
    }
  },
  actions: {
    async login(credentials, redirecionar = true) {
      try {
        const { data } = await api.post('/usuarios/login', credentials);
        this.usuario = data; // O backend retorna os dados do usuário
        this.autenticado = true;
        
        if (redirecionar) {
          router.push('/favoritos');
        }
      } catch (error) {
        console.error("Falha no login:", error);
        console.log("Detalhes do erro:", error.response?.data);
        this.usuario = null;
        this.autenticado = false;
        throw error; // Propaga o erro para o componente poder exibir uma mensagem
      }
    },

async register(userInfo) {
  try {
    await api.post('/usuarios/register', userInfo);
    alert("Registrado com sucesso! Faça login para continuar.");
    router.push('/login'); // Redireciona para a página de login
  } catch (error) {
    console.error("Erro ao registrar:", error);
    const msg = error.response?.data || "Erro ao registrar.";
    alert(msg);
  }
},

    googleLogin() {
      // O backend cuidará da autenticação e do redirecionamento de volta
      window.location.href = "http://localhost:8080/oauth2/authorization/google";
    },

    /**
     * Verifica se existe uma sessão de usuário ativa no backend.
     * Ideal para ser chamado quando o app é carregado pela primeira vez.
     */
    async verificarAuth() {
      try {
        const { data } = await api.get('/usuarios/me');
        this.usuario = data;
        this.autenticado = true;
      } catch (error) {
        // Se a chamada a /me falhar, significa que não há sessão válida
        this.usuario = null;
        this.autenticado = false;
      }
    },
async atualizarPerfil(dados) {
  try {
    const { data } = await api.put('/usuarios/me', dados);
    this.usuario = data;
  } catch (error) {
    console.error("Erro ao atualizar perfil:", error);
    throw error;
  }
},
    async logout() {
      try {
        await api.post('usuarios/logout', {}); 
      } catch (error) {
        console.error('Erro ao fazer logout:', error);
      }
      this.autenticado = false;
      this.usuario = null;
      router.push('/');
    }
  }
});

