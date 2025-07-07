<template>
  <div class="games-page">
    <div class="sidebar">
      <GenreFilter @genre-selected="handleGenreSelected" />
    </div>

    <div class="main-content">
      <div class="titulo-box">
        <h1 class="text-center mb-4">Jogos</h1>
      </div>
      <div class="d-flex justify-content-end mb-3" v-if="isAdmin">
        <button @click="adicionarJogo" class="btn btn-success">
          ➕ Adicionar Jogo
        </button>
      </div>

<div class="search-container d-flex align-items-center mb-4">
  <div class="search-wrapper">
    <i class="fas fa-search search-icon-inside"></i>
    <input 
      type="text" 
      class="form-control search-input" 
      placeholder="Pesquisar..." 
      v-model="searchTerm" 
      @input="getGames(searchTerm)">
  </div>
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
            :numeroAcessos="jogo.numeroAcessos" 
            @card-click="detalhesPage"
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
.search-wrapper {
  position: relative;
  width: 90%;
}

.search-input {
  width: 100%;
  background: linear-gradient(90deg, #f0f4ff, #dbe4ff);
  color: #000;
  border: 2px solid var(--cor-primaria);
  padding: 10px 20px 10px 40px; /* espaço à esquerda para o ícone */
  border-radius: 50px;
  margin-top: 20px;
  margin-bottom: 30px;
  outline: none;
  transition: all 0.3s ease;
}

.search-icon-inside {
  position: absolute;
  top: 45%;
  left: 15px;
  transform: translateY(-50%);
  color: #555; /* mesma cor do placeholder */
  font-size: 16px;
  pointer-events: none;
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
button {
  border: none;
  border-radius: 50px;
  padding: 10px 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

button:hover {
  transform: translateY(-2px);
}

/* ➕ Botão Adicionar */
.btn-success {
  background: #b1baf6;
  color: white;
  box-shadow: 0 4px 8px rgba(40, 61, 167, 0.4);
  padding: 50px 5px;
  font-weight: 600;
  border: none;
  border-radius: 100%;
  min-width: 10px;
  display: block;
  margin: 0 auto; /* Centraliza */
}

.btn-success:hover {
  background: linear-gradient(90deg, #748cf7,#1948f4, #03109d);
  box-shadow: 0 6px 12px rgba(4, 71, 255, 0.5);
}


.btn-edit {
  background: linear-gradient(90deg, #ffc107, #e0a800);
  color: white;
  box-shadow: 0 4px 8px rgba(255, 193, 7, 0.4);
}

.btn-edit:hover {
  background: linear-gradient(90deg, #e0a800, #c69500);
  box-shadow: 0 6px 12px rgba(255, 193, 7, 0.5);
}


.btn-delete {
  background: linear-gradient(90deg, #dc3545, #c82333);
  color: white;
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.4);
}

.btn-delete:hover {
  background: linear-gradient(90deg, #c82333, #bd2130);
  box-shadow: 0 6px 12px rgba(220, 53, 69, 0.5);
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