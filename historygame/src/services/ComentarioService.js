import api from '@/assets/js/axios';

const COMENTARIO_ENDPOINT = '/comentarios';

export default {

  async listarComentarios(jogoId) {
    const res = await api.get(`${COMENTARIO_ENDPOINT}/jogos/${jogoId}`);
    return res.data;
  },

  async listarComentariosPaginado(jogoId, page, size) {
    const res = await api.get(`${COMENTARIO_ENDPOINT}/jogos/${jogoId}/paginado`, {
      params: { page, size }
    });
    return res.data;
  },

  async listarUltimos3(jogoId) {
    const res = await api.get(`${COMENTARIO_ENDPOINT}/ultimos-gerais`);
    return res.data;
  },

  async getMediaEstrelas(jogoId) {
    const res = await api.get(`${COMENTARIO_ENDPOINT}/media-estrelas/${jogoId}`);
    return res.data;
  },

async criarComentario({ jogoId, texto, estrelas }) {
  try {
    const res = await api.post(
      `${COMENTARIO_ENDPOINT}/jogos/${jogoId}`,
      { texto, estrelas }, // remove jogoId do corpo
      { withCredentials: true }
    );
    return res.data;
  } catch (error) {
    console.error('Erro ao criar comentário:', error.response?.data || error.message);
    throw new Error('Não foi possível enviar o comentário. Sua sessão pode ter expirado.');
  }
},

  async deletarComentario(comentarioId) {
    return api.delete(`${COMENTARIO_ENDPOINT}/${comentarioId}`, {withCredentials: true});
  }
};