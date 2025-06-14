<template>
  <div class="games-page">
    <div class="sidebar">
      <GenreFilter @genre-selected="handleGenreSelected" />
    </div>

    <div class="main-content">
      <h1 class="text-center mb-4">Buscar Todos os Jogos</h1>

      <div class="d-flex justify-content-end mb-3" v-if="isAdmin">
        <button @click="adicionarJogo" class="btn btn-success">
          ➕ Adicionar Jogo
        </button>
      </div>

      <div class="search-container d-flex justify-content-between align-items-center mb-4">
        <input 
          type="text" 
          class="form-control search-input" 
          placeholder="Pesquisar..." 
          v-model="searchTerm" 
          @input="getGames(searchTerm)">
      </div>
      
      <div v-if="paginatedGames && paginatedGames.length > 0">
        <div class="card-grid">
          <cardComponent
            v-for="jogo in paginatedGames"
            :key="jogo.id"
            :id="jogo.id"
            :nome="jogo.nome"
            :resumo="jogo.resumo"
            :modoJogo="jogo.modoJogo"
            :dataLancamento="jogo.dataLancamento"
            :capa="jogo.capa"
            :numeroAcessos="jogo.numeroAcessos" @card-click="detalhesPage"
            @edit="editarJogo"
            @delete="deletarJogo"
          />
        </div>
      </div>
      <p v-else>Nenhum jogo encontrado.</p>

      <nav aria-label="Page navigation">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="previousPage">Anterior</button>
          </li>
          <li 
            v-for="page in pagesToShow" 
            :key="page" 
            class="page-item" 
            :class="{ active: currentPage === page }">
            <button class="page-link" @click="changePage(page)">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <button class="page-link" @click="nextPage">Próxima</button>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import cardComponent from '@/components/cardComponent.vue';
import GenreFilter from '@/components/genreFilter.vue';
import JogoService from '@/services/JogoService';
import { useAuthStore } from '@/stores/authStore';

const router = useRouter();
const jogoService = new JogoService();
const authStore = useAuthStore();

const games = ref([]);
const currentPage = ref(1);
const pageSize = ref(9);
const searchTerm = ref('');
const selectedGenre = ref(null);

// Autenticação e jogos ao montar
onMounted(async () => {
  await authStore.verificarAuth();
  await getGames();
});

const isAdmin = computed(() => authStore.isAdmin);
const totalPages = computed(() => Math.ceil(games.value.length / pageSize.value));

const paginatedGames = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return games.value.slice(start, start + pageSize.value);
});

const pagesToShow = computed(() => {
  const total = totalPages.value;
  const current = currentPage.value;
  const delta = 2;
  const pages = [];

  for (let i = Math.max(1, current - delta); i <= Math.min(total, current + delta); i++) {
    pages.push(i);
  }
  return pages;
});

const getGames = async (search = '') => {
  try {
    games.value = search.trim()
      ? await jogoService.searchGamesByName(search)
      : await jogoService.getAllGames();
    currentPage.value = 1;
  } catch (error) {
    console.error('Erro ao buscar jogos:', error);
  }
};

const getGamesByGenre = async (genre) => {
  try {
    if (genre) {
      games.value = await jogoService.getGamesByGenero(genre.nome);
    } else {
      await getGames();
    }
    currentPage.value = 1;
  } catch (error) {
    console.error('Erro ao buscar jogos por gênero:', error);
  }
};

const adicionarJogo = () => {
  router.push('/admin/jogos/adicionar');
};

const editarJogo = (id) => {
  router.push({ name: 'EditarJogo', params: { id } });
};

const deletarJogo = async (id) => {
  try {
    if (confirm('Tem certeza que deseja excluir este jogo?')) {
      await jogoService.deleteGame(id);
      await getGames();
    }
  } catch (error) {
    console.error('Erro ao deletar jogo:', error);
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

const handleGenreSelected = (genero) => {
  selectedGenre.value = genero;
  getGamesByGenre(genero);
};
</script>

<style scoped>
.games-page {
  display: flex;
  gap: 20px;
}

.sidebar {
  flex: 0 0 280px;
}

.main-content {
  flex: 1;
}

.search-input {
  flex: 1;
  background: linear-gradient(90deg, black 50%, gray 80%);
  color: black;
  border: none;
  padding: 10px;
  border-radius: 10px;
  outline: none; /* remove a borda azul padrão */
  transition: all 0.3s ease; /* transição suave para mudanças */
}

.search-input::placeholder {
  color: #555;
}

.search-input:focus {
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.7); /* brilho ao redor */
  background: linear-gradient(90deg, gray, white, black); /* invertendo o degradê ao focar */
  color: black;
}


.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* igual homepage */
  grid-template-rows: repeat(2, auto); /* igual homepage */
  gap: 20px;
  max-width: 80%; /* igual homepage */
  margin: 0 auto; /* igual homepage */
}

.card-grid > * {
  max-width: 95%; 
  margin: 0 auto;
}

.pagination {
  display: flex;
  justify-content: center;
  list-style: none;
  padding: 0;
  margin-top: 20px;
}

.page-item {
  margin: 0 5px;
}

.page-link {
  color: black;
  text-decoration: none;
}

.page-link:hover {
  text-decoration: underline;
}

.page-item.active .page-link {
  font-weight: bold;
}
</style>