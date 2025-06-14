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
    :numeroAcessos="jogo.numeroAcessos"
  @card-click="detalharJogos(jogo.id)"
  @access-incremented="handleAccessIncremented"
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
import { ref, computed, onMounted, watch } from 'vue';
import UserGameService from '@/services/UserGameService';
import { useAuthStore } from '@/stores/authStore';

// --- Estado do Componente ---
const allGames = ref([]);
const searchTerm = ref('');
const currentPage = ref(1);
const itemsPerPage = 6;
const currentFilter = ref('favoritos'); // Filtro inicial
const isLoading = ref(true); // Para um futuro feedback de loading

// --- Lógica de Autenticação ---
const authStore = useAuthStore();
// ✅ CORRIGIDO: O usuário vem da store, garantindo reatividade e consistência.
const user = computed(() => authStore.usuario);
const username = computed(() => user.value?.nome || 'Usuário'); // Usar 'nome' do seu DTO de usuário se disponível

// --- Lógica de Paginação e Filtro (sem alterações) ---
const filteredGames = computed(() => {
  if (!allGames.value) return [];
  return allGames.value.filter(game =>
    game.nome?.toLowerCase().includes(searchTerm.value?.toLowerCase() || '')
  );
});

const paginatedGames = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return filteredGames.value.slice(start, start + itemsPerPage);
});

const totalPages = computed(() => {
  return Math.ceil(filteredGames.value.length / itemsPerPage);
});

const pagesToShow = computed(() => {
  const range = [];
  const total = totalPages.value;
  const current = currentPage.value;
  if (total <= 1) return [];
  for (let i = Math.max(1, current - 1); i <= Math.min(total, current + 1); i++) {
    range.push(i);
  }
  return range;
});

function previousPage() { if (currentPage.value > 1) currentPage.value--; }
function nextPage() { if (currentPage.value < totalPages.value) currentPage.value++; }
function changePage(page) { currentPage.value = page; }


// --- Lógica de API ---

// Função principal para buscar jogos do usuário
async function getUserGames(filtro) {
  // Se um novo filtro for aplicado, atualiza o estado
  if (filtro) {
    currentFilter.value = filtro;
  }
  
  const userId = authStore.usuario?.id;
  if (!userId) {
    console.warn('Tentativa de buscar jogos sem ID de usuário.');
    allGames.value = []; // Limpa a lista se o usuário deslogar
    return;
  }

  isLoading.value = true;
  try {
    let jogos = [];
    switch (currentFilter.value) {
      case 'favoritos':
        jogos = await UserGameService.listarFavoritos(userId);
        break;
      case 'jogados':
        jogos = await UserGameService.listarJogados(userId);
        break;
      case 'desejados':
        jogos = await UserGameService.listarDesejados(userId);
        break;
    }
    allGames.value = jogos;
    currentPage.value = 1; // Reseta a paginação a cada novo filtro
  } catch (error) {
    console.error(`Erro ao buscar lista "${currentFilter.value}":`, error);
    allGames.value = []; // Limpa a lista em caso de erro
  } finally {
    isLoading.value = false;
  }
}

// Função para remover um jogo de qualquer lista
async function removerJogo(jogoId, tipo) {
  const userId = authStore.usuario?.id;
  if (!userId || !tipo) return;

  try {
    const removerMap = {
      favoritos: UserGameService.removerFavorito,
      jogados: UserGameService.removerJogado,
      desejados: UserGameService.removerDesejado,
    };
    
    const removerFuncao = removerMap[tipo];
    if (removerFuncao) {
      await removerFuncao(userId, jogoId);
      // Otimização: em vez de refazer a chamada de API, remove o item localmente.
      allGames.value = allGames.value.filter(jogo => jogo.id !== jogoId);
    }
  } catch (error) {
    console.error(`Erro ao remover jogo da lista ${tipo}:`, error);
  }
}

// ✅ CORRIGIDO: Lógica de inicialização robusta
onMounted(async () => {
  // Garante que o estado de autenticação da store seja verificado primeiro
  await authStore.verificarAuth();
  
  // Se o usuário estiver autenticado, busca os jogos favoritos (padrão)
  if (authStore.autenticado) {
    getUserGames('favoritos');
  } else {
    isLoading.value = false; // Se não há usuário, para o loading
  }
});

// ✅ BÔNUS: Observa mudanças no login/logout para atualizar a lista dinamicamente
watch(() => authStore.usuario, (novoUsuario) => {
    if(novoUsuario) {
        getUserGames('favoritos'); // Se logou, carrega os favoritos
    } else {
        allGames.value = []; // Se deslogou, limpa a lista
    }
})

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
