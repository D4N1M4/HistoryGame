<template>
  <div>
    <h1 class="text-center">Mais Acessados</h1>
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
    
    <h1 class="text-center">Mais Favoritados</h1>
    <div v-if="loadingFavoritos" class="loading-message">Carregando jogos mais favoritados...</div>
    <div v-else-if="errorFavoritos" class="error-message">{{ errorFavoritos }}</div>
    <div v-else-if="gamesFavoritos.length === 0" class="empty-message">Nenhum jogo mais favoritado encontrado.</div>
    <div class="card-grid">
<CardComponent
  v-for="jogo in gamesFavoritos"
  :key="jogo.id" || jogo.nome" 
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
    <section class="comments-section">
      <h2>Últimos Comentários</h2>
      <cardComment 
        v-for="(review, index) in reviews" 
        :key="index" 
        :stars="review.stars"
        :title="review.title"
        :comment="review.comment"
        :userPhotoURL="review.userPhotoURL"
        :userName="review.userName"
        :timestamp="review.timestamp"
        @click="detalharJogos(review.slug, review.gameID)"
      />
    </section>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue';
import CardComponent from '@/components/cardComponent.vue';
import cardComment from '@/components/cardComment.vue';
import { useRouter } from 'vue-router';
import UserGameService from '@/services/UserGameService'; 

const userGameService = UserGameService; 
const gamesAcessados = ref([]);
const gamesFavoritos = ref([]);
const currentPage = ref(1);
const pageSize = ref(6);
const router = useRouter();
const reviews = ref([]);
const loadingAcessados = ref(true);
const errorAcessados = ref(null);
const loadingFavoritos = ref(true);
const errorFavoritos = ref(null);

// --- NOVAS VARIÁVEIS DE ESTADO ---
const loadingAcessados = ref(true);
const errorAcessados = ref(null);
const loadingFavoritos = ref(true);
const errorFavoritos = ref(null);
// --- FIM NOVAS VARIÁVEIS ---


const totalPages = computed(() => {
  // Nota: paginating gamesFavoritos, mas exibindo gamesAcessados.
  // Se a paginação for para a lista "Mais Acessados" no template, mude `gamesFavoritos.value.length` para `gamesAcessados.value.length`.
  return Math.ceil(gamesFavoritos.value.length / pageSize.value); 
});

const paginatedGames = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  // **Atenção:** Seu paginatedGames está filtrando `gamesFavoritos`, mas o v-for acima usa `gamesAcessados`.
  // Se você quer que a paginação seja para os jogos "Mais Acessados", mude `gamesFavoritos.value` para `gamesAcessados.value`.
  // Por agora, vou manter como está no seu código, mas é um ponto de atenção.
  return gamesFavoritos.value.slice(start, end); 
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

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
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

const detalharJogos = (id) => {
  router.push({ name: "DetalhesPage", params: { id } });
};

const loadLastReview = async () => {
  console.log('Carregando primeira página de reviews...');
  try {
    const response = await userGameService.loadLastestReviews();
    reviews.value = response.data;
  } catch (error) {
    console.error("Erro ao carregar reviews:", error);
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
  --cor-primaria: #007bff; /* Um azul vibrante */
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
  margin: 0 5px;
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
</style>