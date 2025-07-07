<template>
  <div>
    <!-- Seção Principal -->
    <div class="container">
      <div class="image-section">
        <img :src="fullImageUrl" :alt="game.nome" />
      </div>

      <!-- Seção de Detalhes -->
      <div class="text-section">
        <h1>{{ game.nome }}</h1>
        <p><b>Descrição</b></p>
        <p>{{ game.resumo }}</p>

<!-- Box de Avaliação -->
<div class="rating-box">
  <button
    class="review-button"
    @click="handleOpenCommentModal"
  >
    Avalie o jogo
  </button>

  <CommentComponent
    v-if="showCommentModal && autenticado"
    @close="handleClose"
    :game-id="Number(gameId)"
    @comentarioEnviado="comentarioEnviado"
  />
</div>

        <!-- Estrelas e ícones -->
        <div class="rating-stars">
          <span v-for="star in 5" :key="star" class="star">
            <i class="far fa-star"></i>
            <i
              class="fas fa-star filled"
              :style="{ width: star <= selectedStars ? '100%' : star - selectedStars < 1 ? ((selectedStars % 1) * 100) + '%' : '0%' }"
            ></i>
          </span>

          <div class="stats" v-if="autenticado">
<div class="stat">
  <img
    @click="userGames('favoritos')"
    class="icon"
    :src="isFavorito ? coracaoColor : coracaoPB"
/>
  <span class="icon-label">Favorito</span>
</div>
<div class="stat">
  <img
    @click="userGames('jogados')"
    class="icon"
    :src="isJogado ? controleColor : controlePB"
/>
  <span class="icon-label">Jogado</span>
</div>

<div class="stat">
  <img
    @click="userGames('desejados')"
    class="icon"
    :src="isDesejado ? ampulhetaColor : ampulhetaPB"
/>
  <span class="icon-label">Desejado</span>
</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Seção Sobre -->
    <div class="about-section">
      <div class="header">
        <h1>SOBRE</h1>
      </div>
      <table class="info-table">
        <thead>
          <tr>
            <th>Gêneros</th>
            <th>Modo de jogo</th>
            <th>Data de Lançamento</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>
              <span v-for="genero in game.generos" :key="genero.id">
                {{ genero.nome }}<br />
              </span>
            </td>
            <td>
              <span v-if="Array.isArray(game.modosDeJogo)">
                <span v-for="modo in game.modosDeJogo" :key="modo">{{ modo }}<br /></span>
              </span>
              <span v-else>{{ game.modoJogo }}</span>
            </td>
            <td>{{ formatarData(game.dataLancamento) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Avaliações de Usuários -->
    <div class="about-section">
      <div class="header">
        <h1>Avaliações de Usuários</h1>
      </div>
      <div class="reviews-container">
<CardComment
  v-for="comentario in reviews"
  :key="comentario.id"
  :nome="comentario.nome"
  :email="comentario.email"
  :texto="comentario.texto"
  :dataCriacao="comentario.dataCriacao"
  :estrelas="comentario.estrelas"
  :isAdmin="isAdmin"
  @excluir-comentario="deletarComentario(comentario.id)"
/>

      <button
        v-if="temMaisComentarios"
        @click="loadMoreReviews"
        class="load-more-button"
      >
        Carregar mais comentários
      </button>    
    </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';

import JogoService from '@/services/JogoService';
import UserGameService from '@/services/UserGameService';
import ComentarioService from '@/services/ComentarioService';
import CommentComponent from '@/components/commentComponent.vue';
import CardComment from '@/components/cardComment.vue';

import imagemPadrao from '@/assets/jogosSemImagem.jpg';
import coracaoColor from '@/assets/coracaoColor.png';
import coracaoPB from '@/assets/coracaoPB.png';
import controleColor from '@/assets/controle-de-video-gameColor.png';
import controlePB from '@/assets/controle-de-video-gamePB.png';
import ampulhetaColor from '@/assets/ampulhetaColor.png';
import ampulhetaPB from '@/assets/ampulhetaPB.png';

const jogoService = new JogoService();

export default {
  name: 'DetalhesPage',
  components: {
    CommentComponent,
    CardComment
  },
  setup() {
    const route = useRoute();
    const authStore = useAuthStore();

    const gameId = ref(route.params.id);
    const game = ref({});
    const reviews = ref([]);
    const selectedStars = ref(0);
    const paginaAtual = ref(0);
    const tamanhoPagina = 5;
    const temMaisComentarios = ref(true);

    const showCommentModal = ref(false);
    const isFavorito = ref(false);
    const isJogado = ref(false);
    const isDesejado = ref(false);

    const autenticado = computed(() => authStore.autenticado);
const isAdmin = computed(() => {
  return authStore.usuario?.roles?.includes('ROLE_ADMIN');
});

    const formatarData = (data) => {
      if (!data) return 'Não informada';
      const dataFormatada = new Date(data);
      return dataFormatada.toLocaleDateString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    };

    const fullImageUrl = computed(() => {
      const cover = game.value.capa;
      if (!cover || cover.trim() === '') return imagemPadrao;
      return cover.startsWith('http')
        ? cover
        : `https:${cover.replace('t_thumb', 't_cover_big')}`;
    });

    const getGameDetails = async () => {
      try {
        game.value = await jogoService.getGameById(gameId.value);
      } catch (err) {
        console.error("Erro ao buscar jogo:", err);
      }
    };

    const getStars = async () => {
      try {
        selectedStars.value = await ComentarioService.getMediaEstrelas(gameId.value); 
      } catch (err) {
        console.error("Erro ao buscar média de estrelas:", err);
      }
    };
    
    const loadReview = async () => {
      try {
        paginaAtual.value = 0;
        const res = await ComentarioService.listarComentariosPaginado(gameId.value, paginaAtual.value, tamanhoPagina);
        reviews.value = res.content;
        temMaisComentarios.value = !res.last;
      } catch (err) {
        console.error("Erro ao carregar comentários:", err);
      }
    };

    const loadMoreReviews = async () => {
      try {
        paginaAtual.value++;
        const res = await ComentarioService.listarComentariosPaginado(gameId.value, paginaAtual.value, tamanhoPagina);
        reviews.value = [...reviews.value, ...res.content];
        temMaisComentarios.value = !res.last;
      } catch (err) {
        console.error("Erro ao carregar mais comentários:", err);
      }
    };

    const updateUserStatus = async (userId) => {
      try {
        const [favoritos, jogados, desejados] = await Promise.all([
          UserGameService.listarFavoritos(userId),
          UserGameService.listarJogados(userId),
          UserGameService.listarDesejados(userId),
        ]);
        const gameIdParsed = parseInt(gameId.value);
        isFavorito.value = favoritos.some(j => j.id === gameIdParsed);
        isJogado.value = jogados.some(j => j.id === gameIdParsed);
        isDesejado.value = desejados.some(j => j.id === gameIdParsed);
      } catch (err) {
        console.error("Erro ao verificar status do usuário:", err);
      }
    };

    const userGames = async (field) => {
      const userId = authStore.usuario?.id;
      if (!autenticado.value || !userId) {
        alert("Você precisa estar autenticado para isso.");
        return;
      }

      try {
        let isAdded, addMethod, removeMethod;

        switch (field) {
          case 'favoritos':
            isAdded = isFavorito.value;
            addMethod = UserGameService.adicionarFavorito;
            removeMethod = UserGameService.removerFavorito;
            break;
          case 'jogados':
            isAdded = isJogado.value;
            addMethod = UserGameService.adicionarJogado;
            removeMethod = UserGameService.removerJogado;
            break;
          case 'desejados':
            isAdded = isDesejado.value;
            addMethod = UserGameService.adicionarDesejado;
            removeMethod = UserGameService.removerDesejado;
            break;
          default:
            return;
        }

        if (isAdded) {
          await removeMethod(userId, gameId.value);
        } else {
          await addMethod(userId, gameId.value);
        }
        await updateUserStatus(userId);
      } catch (err) {
        console.error("Erro ao atualizar status do jogo:", err);
      }
    };

    const handleClose = () => {
      showCommentModal.value = false;
    };

    const comentarioEnviado = async () => {
      showCommentModal.value = false;
      await getStars();
      await loadReview();
    };

    const deletarComentario = async (comentarioId) => {
      const confirmar = confirm("Tem certeza que deseja excluir este comentário?");
      if (!confirmar) return;

      try {
        await ComentarioService.deletarComentario(comentarioId);
        reviews.value = reviews.value.filter(c => c.id !== comentarioId);
      } catch (err) {
        console.error("Erro ao deletar comentário:", err);
        alert("Erro ao excluir o comentário.");
      }
    };

    const handleOpenCommentModal = () => {
      if (!autenticado.value) {
        alert("Você precisa estar autenticado para avaliar o jogo.");
        return;
      }
      showCommentModal.value = true;
    };

    onMounted(async () => {
      await authStore.verificarAuth();
      console.log("Usuário:", authStore.usuario);
      await getGameDetails();
      await getStars();
      await loadReview();

      if (authStore.usuario?.id) {
        await updateUserStatus(authStore.usuario.id);
      }
    });

    watch(() => authStore.usuario, (usuario) => {
      if (usuario?.id) {
        updateUserStatus(usuario.id);
      }
    });

    return {
      game,
      gameId,
      fullImageUrl,
      selectedStars,
      reviews,
      temMaisComentarios,
      showCommentModal,
      handleClose,
      comentarioEnviado,
      loadMoreReviews,
      formatarData,
      autenticado,
      userGames,
      isFavorito,
      isJogado,
      isDesejado,
      coracaoColor,
      handleOpenCommentModal,
      coracaoPB,
      controleColor,
      controlePB,
      ampulhetaColor,
      ampulhetaPB,
      isAdmin,
      deletarComentario // <-- aqui
    };
  }
};
</script>
  
  <style scoped>
  .container {
    display: flex;
    max-width: 1200px;
    margin: 20px auto;
    background: linear-gradient(80deg, #020021, #8c94f3);
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(2, 82, 255, 0.571);
    overflow: hidden;
  }
  
  .image-section img {
    width: 300px;
    height: auto;
    object-fit: cover;
  }
  
  .text-section {
    padding: 20px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  
  .text-section h1 {
    margin: 0 0 10px;
    color: #fbf9f9;
  }
  
  .text-section p {
    margin: 5px 0;
    color: #f9f9f9;
  }
  
  /* Box de Avaliação */
  .rating-box {
    margin-top: 20px;
  }
  
  .review-button {
    width: 200px;
    padding: 12px;
    background: linear-gradient(90deg, #748cf7,#1948f4, #03109d);
    color: #fff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
    text-align: center;
  }
  
  .review-button:hover {
  transform: translateY(-5px) scale(1.03);
  box-shadow: 0 10px 20px #001dfbaa;
  }
  
  /* Estrelas de Avaliação */
.rating-stars {
  margin-top: 20px;
  display: flex;
  gap: 5px;
}

.star {
  position: relative;
  font-size: 1.5rem;
  color: #ccc;
  cursor: pointer;
}

.far.fa-star {
  color: #ccc;
}

.fas.fa-star.filled {
  position: absolute;
  top: 0;
  left: 0;
  color: #ffc107; /* Gold color for stars */
  overflow: hidden;
  transition: width 0.2s ease;
}

  /* Box de Estatísticas ao lado das Estrelas */
  .stats {
    display: flex;
    gap: 20px;
    margin-left: 20px;
  }
  .stat:hover {
    transform: translateY(-5px) scale(1.03);
  }
  .stat {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .stat .icon {
    font-size: 30px;
    color: #000;
    cursor: pointer;
  }
  .icon-label {
  font-size: 12px;
  margin-top: 4px;
  color: #d9e7fcb5;
  text-align: center;
}

  /* Seção Sobre */
  .about-section {
    width: 90%;
    max-width: 1200px;
    margin: 20px auto;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(1, 60, 255, 0.571);
    overflow: hidden;
  }
  
  .header {
    background-color: #020021;
    color: #fff;
    text-align: center;
    padding: 10px 0;
  }
  
  .header h1 {
    margin: 0;
    color: #d0d0d0;
    font-size: 24px;
  }
  
  .info-table {
    width: 100%;
    border-collapse: unset;
    text-align: center;
    margin: 20px 0;
  }
  
  .info-table th,
  .info-table td {
    border: none;
    padding: 10px;
  }
  
  .info-table th {
    background-color: #fff;
    text-transform: uppercase;
    font-size: 14px;
  }
  
  .info-table tr:nth-child(even) {
    background-color: #f9f9f9;
  }
  
  /* Estilo dos Quadrados de Avaliação de Usuários */
/* Container geral dos comentários */
.reviews-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
}

/* Cartão de comentário */
.comment-card {
  background: #ffffff;
  border-radius: 10px;
  padding: 16px 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-left: 6px solid #4169e1;
  transition: transform 0.2s ease, box-shadow 0.3s ease;
}

.comment-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.15);
}

/* Estrelas no comentário */
.comment-card .stars {
  margin-bottom: 8px;
}

.comment-card .stars span {
  font-size: 18px;
  color: #fbc02d;
}

/* Texto do comentário */
.comment-card p {
  margin: 10px 0;
  font-size: 15px;
  color: #333;
}

/* Informações do usuário */
.comment-card small {
  color: #666;
  font-size: 13px;
}

/* Botão "Carregar mais comentários" */
.load-more-button {
  margin: 20px auto;
  padding: 12px 24px;
  background: linear-gradient(90deg, #4f73ff, #324ad5);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.3s ease;
  display: block;
}

.load-more-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(50, 74, 213, 0.3);
}

#showMessage {
  display: none;
}

#passMouse:hover #showMessage{
  display:block;
}

</style>
  