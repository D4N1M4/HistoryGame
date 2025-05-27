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

        <!-- 5 Estrelas de Avaliação e Ícones ao lado -->
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
          <!-- Ícones de Avaliação ao lado das Estrelas -->
  <div class="stats">
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
            <th>Nomes alternativos</th>
            <th>Gêneros</th>
            <th>Modo de jogo</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><span v-for="nome in game.nomesAlternativos" :key="nome">{{ nome }} <br/></span></td>
            <td><span v-for="genero in game.generos" :key="genero.id">{{ genero.nome }} <br/></span></td>
            <td><span v-for="modo in game.modosDeJogo" :key="modo">{{ modo }} <br/></span></td>
          </tr>
        </tbody>
      </table>
      <div class="history-section">
        <h2>História</h2>
        <p>{{ game.resumo }}</p>
      </div>
    </div>

    <!-- Seção de Avaliações de Usuários -->
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
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import JogoService from '@/services/JogoService';
import { getAuth } from "firebase/auth";
import CommentComponent from '@/components/commentComponent.vue';
import cardComment from '@/components/cardComment.vue';
import DAOService from '@/services/DAOService'; // Ainda usado para favoritos, estrelas e reviews
import imagemPadrao from '@/assets/jogosSemImagem.jpg'; // mesma imagem usada no Card
import UserGameService from '@/services/UserGameService';

import coracaoColor from '@/assets/coracaoColor.png'
import coracaoPB from '@/assets/coracaoPB.png'
import controleColor from '@/assets/controle-de-video-gameColor.png'
import controlePB from '@/assets/controle-de-video-gamePB.png'
import ampulhetaColor from '@/assets/ampulhetaColor.png'
import ampulhetaPB from '@/assets/ampulhetaPB.png'
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
    const gameId = ref(route.params.id);
    const slug = ref(route.params.slug);
    const game = ref({});
    const fullImageUrl = computed(() => {
      const cover = game.value.capa;

      if (!cover || cover.trim() === '') {
        return imagemPadrao;
      }

      return cover.startsWith('http')
        ? cover
      : `https:${cover.replace('t_thumb', 't_cover_big')}`;
    
    });

    const showCommentModal = ref(false);
    const selectedStars = ref(0);
    const reviews = ref([]);
    const isFavorito = ref(false);
    const isJogado = ref(false);
    const isDesejado = ref(false);

    const pegarIdUsuario = () => {
      const auth = getAuth();
      return auth.currentUser?.uid || null;
    };

    const getGameDetailsFromAPI = async () => {
      try {
        game.value = await jogoService.getGameById(gameId.value);
      } catch (error) {
        console.error("Erro ao buscar detalhes do jogo via JogoService:", error);
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

const updateUserStatus = async (uid) => {
  try {
    const [favoritosResponse, jogadosResponse, desejadosResponse] = await Promise.all([
      UserGameService.listarFavoritos(uid),
      UserGameService.listarJogados(uid),
      UserGameService.listarDesejados(uid),
    ]);

    // Extrai os arrays de jogos das respostas
    const favoritos = Array.isArray(favoritosResponse) ? favoritosResponse : [];
    const jogados = Array.isArray(jogadosResponse) ? jogadosResponse : [];
    const desejados = Array.isArray(desejadosResponse) ? desejadosResponse : [];

    const gameIdParsed = parseInt(gameId.value);

    isFavorito.value = favoritos.some(j => j.jogo?.id === gameIdParsed || j.id === gameIdParsed);
    isJogado.value = jogados.some(j => j.jogo?.id === gameIdParsed || j.id === gameIdParsed);
    isDesejado.value = desejados.some(j => j.jogo?.id === gameIdParsed || j.id === gameIdParsed);
  } catch (error) {
    console.error("Erro detalhado:", error);
    console.error("Response data:", error.response?.data);
    isFavorito.value = false;
    isJogado.value = false;
    isDesejado.value = false;
  }
};


const userGames = async (field) => {
  const uid = pegarIdUsuario();
  if (!uid) return alert("Usuário não autenticado");

  try {
    let alreadyAdded;
    let serviceMethodAdd;

    switch(field) {
      case 'favoritos':
        alreadyAdded = isFavorito.value;
        serviceMethodAdd = UserGameService.adicionarFavorito;
        break;
      case 'jogados':
        alreadyAdded = isJogado.value;
        serviceMethodAdd = UserGameService.adicionarJogado;
        break;
      case 'desejados':
        alreadyAdded = isDesejado.value;
        serviceMethodAdd = UserGameService.adicionarDesejado;
        break;
      default:
        return;
    }

    if (!alreadyAdded) {
      await serviceMethodAdd(uid, gameId.value);
      await updateUserStatus(uid); // atualiza ícones
    }

  } catch (error) {
    console.error("Erro ao adicionar jogo à lista:", error);
  }
};


const handleClose = () => {
  showCommentModal.value = false;
};

  onMounted(async () => {
    await getGameDetailsFromAPI();
    await getStars();
    await loadReview();

    const uid = pegarIdUsuario();
    if (uid) {
      await updateUserStatus(uid);
    }
  });
    return {
      game,
      fullImageUrl,
      showCommentModal,
      gameId,
      slug,
      selectedStars,
      reviews,
      loadMoreReviews,
      isFavorito,
      isJogado,
      isDesejado,
      userGames,
      handleClose,
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
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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
    font-size: 24px;
  }
  
  .info-table {
    width: 100%;
    border-collapse: unset;
    text-align: left;
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
  
  .history-section {
    padding: 20px;
  }
  
  .history-section h2 {
    font-size: 20px;
    margin-bottom: 10px;
  }
  
  .history-section p {
    font-size: 16px;
    color: #555;
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
  