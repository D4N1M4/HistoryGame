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
          <button class="review-button" @click="showCommentModal = true">Avalie o jogo</button>
          <comment-component 
            v-if="showCommentModal" 
            @close="handleClose" 
            :game-id="gameId" 
            :slug="slug"
          />
        </div>

        <!-- Estrelas e ícones -->
        <div class="rating-stars">
          <span
            v-for="star in 5"
            :key="star"
            class="star"
          >
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
            </div>
            <div class="stat">
              <img
                @click="userGames('jogados')"
                class="icon"
                :src="isJogado ? controleColor : controlePB"
              />
            </div>
            <div class="stat">
              <img
                @click="userGames('desejados')"
                class="icon"
                :src="isDesejado ? ampulhetaColor : ampulhetaPB"
              />
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
        <!-- Gêneros -->
        <td>
          <span v-for="genero in game.generos" :key="genero.id">
            {{ genero.nome }}<br />
          </span>
        </td>

        <!-- Modo de Jogo -->
        <td>
          <span v-if="Array.isArray(game.modosDeJogo)">
            <span v-for="modo in game.modosDeJogo" :key="modo">{{ modo }}<br /></span>
          </span>
          <span v-else>{{ game.modoJogo }}</span>
        </td>

        <!-- Data de Lançamento -->
        <td>
          {{ formatarData(game.dataLancamento) }}
        </td>
      </tr>
    </tbody>
  </table>
</div>

    <!-- Avaliações de Usuários -->
    <div class="about-section">
      <div class="header">
        <h1>Avaliações de Usuários</h1>
      </div>
      <cardComment 
        v-for="(review, index) in reviews" 
        :key="index"
        :stars="review.stars"
        :nome="review.title"
        :comment="review.comment"
        :userPhotoURL="review.userPhotoURL"
        :userName="review.userName"
        :timestamp="review.timestamp"
      />
      <button @click="loadMoreReviews">Carregar mais comentários</button>
    </div>
  </div>
</template>

<script>
import { watch, ref, computed, onMounted, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import JogoService from '@/services/JogoService';
import DAOService from '@/services/DAOService';
import UserGameService from '@/services/UserGameService';
import CommentComponent from '@/components/commentComponent.vue';
import cardComment from '@/components/cardComment.vue';

import imagemPadrao from '@/assets/jogosSemImagem.jpg';
import coracaoColor from '@/assets/coracaoColor.png';
import coracaoPB from '@/assets/coracaoPB.png';
import controleColor from '@/assets/controle-de-video-gameColor.png';
import controlePB from '@/assets/controle-de-video-gamePB.png';
import ampulhetaColor from '@/assets/ampulhetaColor.png';
import ampulhetaPB from '@/assets/ampulhetaPB.png';

const daoService = new DAOService();
const jogoService = new JogoService();

export default {
  name: 'DetalhesPage',
  components: {
    CommentComponent,
    cardComment,
  },
  setup() {
    const route = useRoute();
    const authStore = useAuthStore();

    const gameId = ref(route.params.id);
    const slug = ref(route.params.slug);
    const game = ref({
      id: null,
      nome: '',
      resumo: '',
      capa: '/img/jogoPadrao.png',
      modoJogo: '',
      dataLancamento: '',
      generos: [],
      modosDeJogo: [] // opcional, dependendo do que sua API retorna
    });
    const selectedStars = ref(0);
    const reviews = ref([]);
    const showCommentModal = ref(false);

    const isFavorito = ref(false);
    const isJogado = ref(false);
    const isDesejado = ref(false);

const formatarData = (data) => {
  if (!data) return 'Não informada';
  const dataFormatada = new Date(data);
  return dataFormatada.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
};

const modosDeJogoNormalizado = computed(() => {
  return Array.isArray(game.value.modosDeJogo)
    ? game.value.modosDeJogo
    : game.value.modoJogo
      ? [game.value.modoJogo]
      : [];
});

    const fullImageUrl = computed(() => {
      const cover = game.value.capa;
      if (!cover || cover.trim() === '') {
        return imagemPadrao;
      }
      return cover.startsWith('http')
        ? cover
        : `https:${cover.replace('t_thumb', 't_cover_big')}`;
    });

    const autenticado = computed(() => authStore.autenticado);

    const getGameDetailsFromAPI = async () => {
      try {
        game.value = await jogoService.getGameById(gameId.value);
      } catch (error) {
        console.error("Erro ao buscar jogo:", error);
      }
    };

    const getStars = async () => {
      try {
        selectedStars.value = await daoService.getStars(gameId.value);
      } catch (error) {
        console.error("Erro ao buscar estrelas:", error);
      }
    };

    const loadReview = async () => {
      try {
        reviews.value = await daoService.loadFirstPageReviews(gameId.value);
      } catch (error) {
        console.error("Erro ao carregar reviews:", error);
      }
    };

    const loadMoreReviews = async () => {
      try {
        const newReviews = await daoService.loadNextPageReviews(gameId.value);
        reviews.value = [...reviews.value, ...newReviews];
      } catch (error) {
        console.error("Erro ao carregar mais reviews:", error);
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
    // Assumindo que listarFavoritos/Jogados/Desejados retorna List<AlgoJogoDTO>
    // e AlgoJogoDTO tem um campo 'id' que é o ID do Jogo.
    isFavorito.value = favoritos.some(j => j.id === gameIdParsed); // AJUSTADO
    isJogado.value = jogados.some(j => j.id === gameIdParsed);   // AJUSTADO
    isDesejado.value = desejados.some(j => j.id === gameIdParsed); // AJUSTADO
  } catch (error) { /* ... */ }
};

const userGames = async (field) => {
  const userId = authStore.usuario?.id;
  if (!authStore.autenticado || !userId) {
    alert("Você precisa estar autenticado para isso."); return;
  }
  try {
    let isCurrentlyAdded;
    let addMethod;
    let removeMethod;

    switch (field) {
      case 'favoritos':
        isCurrentlyAdded = isFavorito.value;
        addMethod = UserGameService.adicionarFavorito;
        removeMethod = UserGameService.removerFavorito; // Adicionar este método ao UserGameService
        break;
      case 'jogados':
        isCurrentlyAdded = isJogado.value;
        addMethod = UserGameService.adicionarJogado;
        removeMethod = UserGameService.removerJogado;   // Adicionar este método ao UserGameService
        break;
      case 'desejados':
        isCurrentlyAdded = isDesejado.value;
        addMethod = UserGameService.adicionarDesejado;
        removeMethod = UserGameService.removerDesejado; // Este já existe no UserGameService
        break;
      default: return;
    }

    if (isCurrentlyAdded) {
      await removeMethod(userId, gameId.value);
    } else {
      await addMethod(userId, gameId.value);
    }
    await updateUserStatus(userId); // Atualiza o estado da UI
  } catch (error) { /* ... */ }
};


    const handleClose = () => {
      showCommentModal.value = false;
    };

    onMounted(async () => {
      await authStore.verificarAuth();
      await getGameDetailsFromAPI();
      await getStars();
      await loadReview();

      if (authStore.autenticado && authStore.usuario?.id) {
        await updateUserStatus(authStore.usuario.id);
      }
    });

    // Garante atualização caso auth seja carregado depois
    watch(() => authStore.usuario, (usuario) => {
      if (usuario?.id) {
        updateUserStatus(usuario.id);
      }
    });

    return {
      game,
      gameId,
      slug,
      fullImageUrl,
      selectedStars,
      reviews,
      loadMoreReviews,
      showCommentModal,
      handleClose,
      userGames,
      formatarData,
      modosDeJogoNormalizado,
      isFavorito,
      isJogado,
      isDesejado,
      autenticado,
      coracaoColor,
      coracaoPB,
      controleColor,
      controlePB,
      ampulhetaColor,
      ampulhetaPB
    };
  }
};
</script>

  
  <style scoped>
  .container {
    display: flex;
    max-width: 1200px;
    margin: 20px auto;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(110, 194, 253, 0.571);
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
    color: #555;
  }
  
  .text-section p {
    margin: 5px 0;
    color: #555;
  }
  
  /* Box de Avaliação */
  .rating-box {
    margin-top: 20px;
  }
  
  .review-button {
    width: 200px;
    padding: 12px;
    background-color: #000;
    color: #fff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
    text-align: center;
  }
  
  .review-button:hover {
    background-color: #333;
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
  
  /* Seção Sobre */
  .about-section {
    width: 90%;
    max-width: 1200px;
    margin: 20px auto;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(110, 194, 253, 0.571);
    overflow: hidden;
  }
  
  .header {
    background-color: #000;
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
  .reviews-container {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .review-card {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    width: 250px;
    text-align: center;
  }
  
  .profile-img {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    margin-bottom: 10px;
  }
  
  .review-content {
    color: #555;
  }
  
  .review-content h3 {
    margin: 10px 0;
    font-size: 18px;
  }
  
  .review-content .stars {
    display: flex;
    gap: 5px;
    justify-content: center;
  }
  
  .review-content p {
    margin-top: 10px;
    font-size: 14px;
  }

  .comment-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 20px;
  z-index: 1000;
}
#showMessage {
  display: none;
}

#passMouse:hover #showMessage{
  display:block;
}

</style>
  