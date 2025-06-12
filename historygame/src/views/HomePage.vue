<template>
  <div>
    <h1 class="text-center">Mais Acessados</h1>
    <div class="card-grid">
      <cardComponent
        v-for="(game, index) in gamesAcessados" 
        :key="index"
        :id="game.id"
        :nome="game.name"
        :resumo="game.summary"
        :modoJogo="game.modoJogo || 'Desconhecido'" 
        :dataLancamento="game.dataLancamento || 'Indisponível'"
        :capa="game.coverUrl"
      />
    </div>
    
    <h1 class="text-center">Mais Favoritados</h1>
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
    <!-- Adicionando a seção de comentários -->
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
import UserGameService from '@/services/UserGameService'; // import novo

const userGameService = UserGameService; // instancia do service correto
const gamesAcessados = ref([]);
const gamesFavoritos = ref([]); // ref para os mais favoritados
const currentPage = ref(1);
const pageSize = ref(6);
const router = useRouter();
const reviews = ref([]);

const totalPages = computed(() => {
return Math.ceil(gamesFavoritos.value.length / pageSize.value);
});

const paginatedGames = computed(() => {
const start = (currentPage.value - 1) * pageSize.value;
const end = start + pageSize.value;
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
    // Se quiser manter a chamada DAOService para acessados
    gamesAcessados.value = await UserGameService.getMaisAcessados(); // ou DAOService.getMaisAcess();
  } catch (error) {
    console.error('Erro ao buscar jogos mais acessados:', error);
  }
};

const getGamesFavoritos = async () => {
  try {
    gamesFavoritos.value = await userGameService.getMaisFavoritados();
  } catch (error) {
    console.error('Erro ao buscar jogos mais favoritados:', error);
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

const detalharJogos = (slug, id) => {
router.push({ name: "DetalhesPage", params: { slug, id } });
};

const loadLastReview = async () => {
console.log('Carregando primeira página de reviews...');
try {
  reviews.value = await userGameService.loadLastestReviews();
} catch (error) {
  console.error("Erro ao carregar reviews:", error);
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
</style>


