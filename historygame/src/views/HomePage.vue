<template>
  <div>
    <div class="titulo-box">
      <h1>Mais Acessados</h1>
    </div>
    <div v-if="loadingAcessados" class="loading-message">Carregando jogos mais acessados...</div>
    <div v-else-if="errorAcessados" class="error-message">{{ errorAcessados }}</div>
    <div v-else-if="gamesAcessados.length === 0" class="empty-message">Nenhum jogo mais acessado encontrado.</div>
    <div class="card-grid">
      <cardComponent
        v-for="(game, index) in gamesAcessados" 
        :key="game.id || index" 
        :id="game.id"
        :nome="game.nome"
        :resumo="game.resumo"
        :modoJogo="game.modoJogo || 'Desconhecido'" 
        :dataLancamento="game.dataLancamento" 
        :capa="game.capa"
        :numeroAcessos="game.numeroAcessos" 
        @card-click="detalharJogos(game.id)" 
        @access-incremented="handleAccessIncremented" />
    </div>
    <div class="titulo-box">
      <h1>Mais Favoritados</h1>
    </div>
    <div v-if="loadingFavoritos" class="loading-message">Carregando jogos mais favoritados...</div>
    <div v-else-if="errorFavoritos" class="error-message">{{ errorFavoritos }}</div>
    <div v-else-if="gamesFavoritos.length === 0" class="empty-message">Nenhum jogo mais favoritado encontrado.</div>
    <div class="card-grid">
<CardComponent
  v-for="jogo in gamesFavoritos"
  :key="jogo.id"
  :id="jogo.id"
  :nome="jogo.nome || ''"
  :resumo="jogo.resumo || ''"
  :capa="jogo.capa || ''"
  :modoJogo="jogo.modoJogo || 'Desconhecido'"
  :dataLancamento="jogo.dataLancamento || 'Indisponível'"
  :numeroAcessos="jogo.numeroAcessos"
  @card-click="detalharJogos(jogo.id)"
  @access-incremented="handleAccessIncremented"
/>


    </div>
    <nav aria-label="Page navigation">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" href="#" @click.prevent="previousPage">Previous</a>
        </li>
        <li class="page-item" v-if="currentPage > 2">
          <a class="page-link" href="#" @click.prevent="changePage(1)">1</a>
        </li>
        <li class="page-item" v-if="currentPage > 3">
          <span class="page-link">...</span>
        </li>
        <li class="page-item" v-for="page in pagesToShow" :key="page" :class="{ active: currentPage === page }">
          <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
        </li>
        <li class="page-item" v-if="currentPage < totalPages - 2">
          <span class="page-link">...</span>
        </li>
        <li class="page-item" v-if="currentPage < totalPages - 1">
          <a class="page-link" href="#" @click.prevent="changePage(totalPages)">{{ totalPages }}</a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" href="#" @click.prevent="nextPage">Next</a>
        </li>
      </ul>
    </nav>
    <!-- Seção de comentários -->
    <section class="comments-section">
    <div class="titulo-box">
      <h1>Últimos Comentários</h1>
    </div>
      <div class="reviews-container">
        <cardComment
          v-for="(review, index) in reviews"
          :key="review.id"
          :estrelas="review.estrelas"
          :texto="review.texto"
          :nome="review.nome"
          :email="review.email"
          :dataCriacao="review.dataCriacao"
        />
      </div>
    </section>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue';
import CardComponent from '@/components/cardComponent.vue';
import cardComment from '@/components/cardComment.vue';
import { useRouter } from 'vue-router';
import UserGameService from '@/services/UserGameService'; // import novo
import ComentarioService from '@/services/ComentarioService';

const userGameService = UserGameService; // instancia do service correto
const gamesAcessados = ref([]);
const gamesFavoritos = ref([]); // ref para os mais favoritados
const currentPage = ref(1);
const pageSize = ref(6);
const router = useRouter();
const reviews = ref([]);
const loadingAcessados = ref(true);
const errorAcessados = ref(null);
const loadingFavoritos = ref(true);
const errorFavoritos = ref(null);

const totalPages = computed(() => {
return Math.ceil(gamesFavoritos.value.length / pageSize.value);
});


const pagesToShow = computed(() => {
const startPage = Math.max(currentPage.value - 1, 1);
const endPage = Math.min(currentPage.value + 1, totalPages.value);
const pages = [];
for (let i = startPage; i <= endPage; i++) {
  pages.push(i);
}
return pages;
});

const getGamesAcessados = async () => {
  try {
    loadingAcessados.value = true; 
    errorAcessados.value = null; 
    const response = await userGameService.getMaisAcessados();
    gamesAcessados.value = response; // CORRIGIDO: Removido o .data
    console.log('DEBUG: Jogos Mais Acessados carregados:', gamesAcessados.value);
  } catch (error) {
    console.error('Erro ao buscar jogos mais acessados:', error);
    errorAcessados.value = 'Falha ao carregar jogos mais acessados. Verifique o console.';
  } finally {
    loadingAcessados.value = false;
  }
};

const getGamesFavoritos = async () => {
  try {
    loadingFavoritos.value = true;
    errorFavoritos.value = null; 
    const response = await userGameService.getMaisFavoritados();
    gamesFavoritos.value = response; // CORRIGIDO: Removido o .data
    console.log('DEBUG: Jogos Mais Favoritados carregados:', gamesFavoritos.value);
  } catch (error) {
    console.error('Erro ao buscar jogos mais favoritados:', error);
    errorFavoritos.value = 'Falha ao carregar jogos mais favoritados. Verifique o console.';
  } finally {
    loadingFavoritos.value = false;
  }
};

const previousPage = () => {
if (currentPage.value > 1) {
  currentPage.value--;
}
};

const nextPage = () => {
if (currentPage.value < totalPages.value) {
  currentPage.value++;
}
};

const detalharJogos = (slug, id) => {
router.push({ name: "DetalhesPage", params: { slug, id } });
};

const loadLastReview = async (jogoId) => {
  console.log('Carregando últimos 3 comentários do jogo:', jogoId);
  try {
    reviews.value = await ComentarioService.listarUltimos3(jogoId);
  } catch (error) {
    console.error("Erro ao carregar últimos comentários:", error);
  }
};

const handleAccessIncremented = (gameId) => {
  const gameInAcessados = gamesAcessados.value.find(g => g.id === gameId);
  if (gameInAcessados) {
    gameInAcessados.numeroAcessos++;
  }
  const gameInFavoritos = gamesFavoritos.value.find(g => g.id === gameId);
    if (gameInFavoritos) {
      gameInFavoritos.numeroAcessos++;
  }
};
onMounted(() => {
getGamesAcessados();
getGamesFavoritos();
loadLastReview();
});

</script>


<style scoped>
:root {
  --cor-fundo: #f4f7f6;
  --cor-texto: #333;
  --cor-titulo: #1a1a1a;
  --cor-primaria: #044afc; /* Um azul vibrante */
  --cor-branco: #ffffff;
  --sombra-card: 0 4px 15px rgba(0, 0, 0, 0.1);
}
body {
  font-family: 'Poppins', 'Arial', sans-serif;
  background-color: var(--cor-fundo);
  color: var(--cor-texto);
}
.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.container {
padding: 20px;
}

.card-grid {
display: grid;
grid-template-columns: repeat(3, 1fr); /* 3 colunas */
grid-template-rows: repeat(2, auto); /* 2 linhas */
gap: 20px;
max-width: 80%; /* Reduzir a largura máxima da grade */
margin: 0 auto; /* Centralizar a grade na página */
}

.card-grid > * {
max-width: 95%; /* Ajustar a largura dos cards */
margin: 0 auto; /* Centralizar os cards dentro das colunas */
}

.pagination-container {
  margin-top: 40px;
}

.pagination {
  display: flex;
  justify-content: center;
  list-style: none;
  padding: 0;
}

.page-item {
  margin: 0 4px;
}

.page-link {
  display: block;
  padding: 10px 16px;
  color: var(--cor-primaria);
  background-color: var(--cor-branco);
  border: 1px solid #dee2e6;
  border-radius: 50px;  
  text-decoration: none;
  transition: all 0.3s ease;
  font-weight: 600;
}

.page-link:hover {
  background-color: #e9ecef;
  border-color: #ced4da;
  transform: translateY(-4px);
}

.page-item.active .page-link {
  background-color: var(--cor-primaria);
  color: var(--cor-branco);
  border-color: var(--cor-primaria);
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.4);
}

.page-item.disabled .page-link {
  color: #6c757d;
  pointer-events: none;
  background-color: var(--cor-branco);
  border-color: #dee2e6;
}
.loading-message {
  text-align: center;
  font-size: 1.1em;
  color: #888;
  margin-top: 20px;
  margin: 0 4px;
}
.error-message {
  color: red;
  text-align: center;
  margin-top: 20px;
}

.empty-message {
  color: #888;
  text-align: center;
  margin-top: 20px;
}
.titulo-box {
  background: #020021;
  padding: 10px 30px;
  margin: 30px auto 20px;
  border: 1px solid #ccc;
  border-left: 6px solid var(--cor-primaria);
  border-radius: 50px;
  box-shadow: var(--sombra-card);
  text-align: center;
  max-width: 600px;
  width: 90%;
}

.titulo-box h1 {
  margin: 0;
  font-size: 1.3rem;
  color: #fefefe;
  font-weight: bold;
}

/* Container geral dos comentários */
.reviews-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 24px 16px;
  background-color: #f9fafb; /* fundo claro e neutro */
  border-radius: 12px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.05);
  max-width: 900px;
  margin: 0 auto;
}

/* Cartão de comentário */
.comment-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-left: 6px solid #044afc; /* cor azul forte e alinhada com tema */
  transition: transform 0.25s ease, box-shadow 0.3s ease;
}

.comment-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

/* Estrelas no comentário */
.comment-card .stars {
  margin-bottom: 12px;
}

.comment-card .stars span {
  font-size: 20px;
  color: #fbc02d;
  margin-right: 4px;
}

/* Texto do comentário */
.comment-card p {
  margin: 14px 0 12px;
  font-size: 16px;
  line-height: 1.5;
  color: #222;
  font-weight: 400;
  font-family: 'Poppins', sans-serif;
}

/* Informações do usuário */
.comment-card small {
  color: #666;
  font-size: 14px;
  font-weight: 500;
  font-style: normal;
  display: block;
  margin-top: 6px;
}

/* Exibir nome e email lado a lado, se quiser */
.comment-card .user-info {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: #555;
  margin-top: 4px;
}

/* Responsividade */
@media (max-width: 768px) {
  .reviews-container {
    padding: 16px 12px;
    max-width: 100%;
  }

  .comment-card {
    padding: 16px 18px;
  }

  .comment-card p {
    font-size: 15px;
  }

  .comment-card .stars span {
    font-size: 18px;
  }
}

/* Responsividade */
@media (max-width: 768px) {
  .comments-section {
    max-width: 95%;
    margin: 20px auto 40px;
  }

  .comments-section h2 {
    font-size: 1.5rem;
  }
}

@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .profile-pic-box {
    margin-right: 0;
    margin-bottom: 1rem;
  }

  .search-container {
    flex-direction: column;
    align-items: stretch;
  }

  .btn-group .btn {
    width: 100%;
    margin-bottom: 0.5rem;
  }

  .card-grid {
    margin: 0;
    grid-template-columns: 1fr;
  }
}
</style>


