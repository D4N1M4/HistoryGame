<template>
  <div>
    <!-- Perfil -->
    <div class="profile-container d-flex justify-content-center align-items-center mb-4">
      <div class="profile-pic-box">
        <img :src="user?.photoURL || defaultAvatar" alt="Foto de Perfil" class="profile-pic" />
      </div>
      <div class="profile-info-box">
        <div class="profile-info">
          <h2 class="edit-input">{{ username }}</h2>
          <p class="profile-description">Counter-Striker Player e Casual Gamer.</p>
        </div>
      </div>
    </div>

    <!-- Título -->
    <div>
      <h1 class="text-center">Meus Jogos</h1>

      <!-- Campo de busca -->
      <div class="search-container d-flex justify-content-between align-items-center mb-4">
        <input 
          type="text" 
          class="form-control search-input" 
          placeholder="Pesquisar..." 
          v-model="searchTerm" 
        />
      </div>

      <!-- Filtros -->
      <div class="btn-group d-flex justify-content-center mb-4" role="group">
        <button type="button" @click="getUserGames('favoritos')" class="btn btn-dark">Favoritos</button>
        <button type="button" @click="getUserGames('jogados')" class="btn btn-dark">Jogados</button>
        <button type="button" @click="getUserGames('desejados')" class="btn btn-dark">Lista de Desejos</button>
      </div>

      <!-- Lista de jogos -->
      <div class="card-grid">
<cardComponent
  v-for="jogo in paginatedGames"
  :key="jogo.id"
  :id="jogo.id"
  :nome="jogo.nome"
  :modoJogo="jogo.modoJogo"
  :resumo="jogo.resumo"
  :dataLancamento="jogo.dataLancamento"
  :capa="jogo.capa"
>
  <template v-if="['favoritos', 'jogados', 'desejados'].includes(currentFilter)" #footer>
    <button 
      class="btn-remover mt-2" 
      @click.stop="removerJogo(jogo.id, currentFilter)"
    >
      Remover da Lista
    </button>
  </template>
</cardComponent>
      </div>

      <!-- Paginação -->
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

          <li
            class="page-item"
            v-for="page in pagesToShow"
            :key="page"
            :class="{ active: currentPage === page }"
          >
            <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
          </li>

          <li class="page-item" v-if="currentPage < totalPages - 2">
            <span class="page-link">...</span>
          </li>
          <li class="page-item" v-if="currentPage < totalPages - 1">
            <a class="page-link" href="#" @click.prevent="changePage(totalPages)">
              {{ totalPages }}
            </a>
          </li>

          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <a class="page-link" href="#" @click.prevent="nextPage">Next</a>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script setup>
import defaultAvatar from '@/assets/default_avatar.jpg';
import cardComponent from '@/components/cardComponent.vue';
import { ref, computed, onMounted } from 'vue';
import UserGameService from '@/services/UserGameService';
import { getAuth } from 'firebase/auth';

const allGames = ref([]);
const searchTerm = ref('');
const currentPage = ref(1);
const itemsPerPage = 6;
const currentFilter = ref('favoritos'); // manter o filtro atual
const auth = getAuth();
const user = ref(null);
const username = computed(() => user.value?.displayName || 'Usuário');

// Computeds
const paginatedGames = computed(() => {
  const filtered = allGames.value.filter(game =>
    game.nome?.toLowerCase().includes(searchTerm.value?.toLowerCase() || '')
  );
  const start = (currentPage.value - 1) * itemsPerPage;
  return filtered.slice(start, start + itemsPerPage);
});

const totalPages = computed(() => {
  return Math.ceil(
    allGames.value.filter(game =>
  game.nome?.toLowerCase().includes(searchTerm.value.toLowerCase())
    ).length / itemsPerPage
  );
});

const pagesToShow = computed(() => {
  const range = [];
  const total = totalPages.value;
  const current = currentPage.value;
  const delta = 2;
  for (let i = Math.max(1, current - delta); i <= Math.min(total, current + delta); i++) {
    range.push(i);
  }
  return range;
});

function previousPage() {
  if (currentPage.value > 1) currentPage.value--;
}

function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++;
}

function changePage(page) {
  currentPage.value = page;
}

// Função principal para buscar jogos de acordo com o filtro
async function getUserGames(filtro = 'favoritos') {
  try {
    const uid = auth.currentUser?.uid;
    if (!uid) {
      console.warn('Usuário não autenticado');
      return;
    }

    currentFilter.value = filtro;
    let jogos = [];

    switch (filtro) {
      case 'favoritos':
        jogos = await UserGameService.listarFavoritos(uid);
        console.log('Favoritos retornados:', jogos);
        break;
      case 'jogados':
        jogos = await UserGameService.listarJogados(uid);
        break;
      case 'desejados':
        jogos = await UserGameService.listarDesejados(uid);
        break;
      case 'todos':
        jogos = await UserGameService.listarTodos(uid);
        break;
    }

    allGames.value = jogos;
    currentPage.value = 1;
  } catch (error) {
    console.error('Erro ao buscar jogos do usuário:', error);
  }
}

// Função genérica para remover jogo de uma das listas
async function removerJogo(jogoId, tipo) {
  try {
    const uid = getAuth().currentUser?.uid;

    if (tipo === 'favoritos') {
      await UserGameService.removerFavorito(uid, jogoId);
    } else if (tipo === 'jogados') {
      await UserGameService.removerJogado(uid, jogoId);
    } else if (tipo === 'desejados') {
      await UserGameService.removerDesejado(uid, jogoId);
    }

    await getUserGames(currentFilter.value); // Atualiza a lista com base no filtro atual
  } catch (error) {
    console.error(`Erro ao remover jogo da lista ${tipo}:`, error);
  }
}

onMounted(() => {
  user.value = auth.currentUser;
  getUserGames(); // padrão = 'favoritados'
});

</script>


<style scoped>
body {
  font-family: 'Arial', sans-serif;
}

.container {
  padding: 20px;
}

.profile-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.profile-pic-box {
  border: 2px solid #ccc;
  border-radius: 10px;
  padding: 10px;
  margin-right: 20px;
}

.profile-pic {
  width: 150px; /* Ajuste o tamanho conforme necessário */
  height: 150px; /* Ajuste o tamanho conforme necessário */
}

.profile-info-box {
  border: 1px solid #ccc;
  border-radius: 10px;
  padding: 30px;
  background-color: #f9f9f9;
}

.profile-info {
  display: flex;
  flex-direction: column;
}

.profile-name {
  font-size: 2em; /* Ajuste o tamanho conforme necessário */
  margin: 0;
}

.profile-description {
  margin: 0;
  color: gray;
  font-size: 1.2em; /* Ajuste o tamanho conforme necessário */
}

.search-container {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 20px;
}

.search-input {
  flex: 0.5;
  margin-right: 10px;
}

.btn-group .btn {
  margin-right: 5px;
  background: black;
  color: white;
  background-color: black;
  border: 1px solid white;
  padding: 10px 20px;
  cursor: pointer;
  border-radius: 10px;
}

.btn-group .btn:hover {
  opacity: 0.7;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  justify-content: center;
  margin: 0 50px; /* Aumenta as margens laterais */
}

.card-component {
  width: 300px; /* Ajuste a largura conforme necessário */
  margin: 0 auto;
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
  color: black;
  text-decoration: none;
}

.page-link:hover {
  text-decoration: underline;
}

.page-item.active .page-link {
  font-weight: bold;
}

.btn-remover {
  background: linear-gradient(to right, #666666, #bbbbbb, #000000);
  color: #000;
  border: none;
  border-radius: 8px;
  padding: 10px 15px;
  font-weight: bold;
  transition: all 0.3s ease-in-out;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
}

.btn-remover:hover {
  background: linear-gradient(to right, #070707, #bbbbbb, #666666);
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

</style>
