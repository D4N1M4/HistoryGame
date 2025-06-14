
import api from '@/assets/js/axios';

export default {
async adicionarFavorito(usuarioId, jogoId) {
  return api.post(`/usuarios/${usuarioId}/favoritos/${Number(jogoId)}`, {}, {
    withCredentials: true
  });
},

async removerFavorito(usuarioId, jogoId) {
  return api.delete(`/usuarios/${usuarioId}/favoritos/${Number(jogoId)}`, {
    withCredentials: true
  });
},

  async listarFavoritos(usuarioId) {
    const res = await api.get(`/usuarios/${usuarioId}/favoritos`);
    return res.data;
  },

// Jogados
async adicionarJogado(usuarioId, jogoId) {
  return api.post(`/usuarios/${usuarioId}/jogados/${Number(jogoId)}`, {}, {
    withCredentials: true
  });
},

async removerJogado(usuarioId, jogoId) {
  return api.delete(`/usuarios/${usuarioId}/jogados/${Number(jogoId)}`, {
    withCredentials: true
  });
},

  async listarJogados(usuarioId) {
    const res = await api.get(`/usuarios/${usuarioId}/jogados`);
    return res.data;
  },

// Desejados
async adicionarDesejado(usuarioId, jogoId) {
  return api.post(`/usuarios/${usuarioId}/desejados/${Number(jogoId)}`, {}, {
    withCredentials: true
  });
},

async removerDesejado(usuarioId, jogoId) {
  return api.delete(`/usuarios/${usuarioId}/desejados/${Number(jogoId)}`, {
    withCredentials: true
  });
},

  async listarDesejados(usuarioId) {
    const res = await api.get(`/usuarios/${usuarioId}/desejados`);
    return res.data;
  },

  // Outros
  async registrarUsuario(nome, email) {
    return api.post(`/usuarios/register`, {
      nome, email
    });
  },

  async getMaisFavoritados() {
    const res = await api.get(`/jogos/favoritos/mais`);
    return res.data;
  },

  async getMaisAcessados(limit = 3) { // Adicione um limite padrão, 
    const res = await api.get(`/jogos/mais-acessados?limit=${limit}`);
    return res.data;
  },
};

