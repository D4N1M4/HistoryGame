<template>
  <div>
    <!-- Perfil -->
    <div class="profile-container d-flex justify-content-center align-items-center mb-4">
<div class="profile-pic-box">
  <img :src="user?.foto || defaultAvatar" alt="Foto de Perfil" class="profile-pic" />
</div>
<div class="profile-info-box">
  <div class="profile-info">
    <h2 class="edit-input">{{ username }}</h2>
    <p class="profile-description">{{ user?.bio || 'Bio não informada.' }}</p>
  </div>
</div>
    </div>

    <!-- Título -->
    <div>
      <h1 class="text-center">Meus Jogos</h1>

<div class="search-container d-flex align-items-center mb-4">
  <div class="search-wrapper">
    <i class="fas fa-search search-icon"></i>
    <input 
      type="text" 
      class="form-control search-input" 
      placeholder="Pesquisar..." 
      v-model="searchTerm" 
    />
  </div>
</div>

      <!-- Filtros -->
      <div class="btn-group d-flex justify-content-center mb-4" role="group">
        <button type="button" @click="getUserGames('favoritos')" class="btn btn-dark">Favoritos</button>
        <button type="button" @click="getUserGames('jogados')" class="btn btn-dark">Jogados</button>
        <button type="button" @click="getUserGames('desejados')" class="btn btn-dark">Desejados</button>
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

async function handleAccessIncremented(jogoId) {
  const jogo = allGames.value.find(j => j.id === jogoId);
  if (jogo) {
    jogo.numeroAcessos++;
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
  background-color: #f0f2f5;
  color: #333;
}

.container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.profile-pic-box {
  border: 2px solid #020021;
  border-radius: 50%;
  padding: 0.5rem;
  margin-right: 2rem;
  background-color: #b3c8fc;
  box-shadow: 0 4px 12px rgba(2, 39, 252, 0.486);
}

.profile-pic {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
}

.profile-info-box {
  border-radius: 12px;
  padding: 2rem;
  background: linear-gradient(145deg, #ffffff, #cbdffa);
  box-shadow: 5px 5px 15px rgba(43, 142, 255, 0.559);
}

.profile-info {
  display: flex;
  flex-direction: column;
}

.profile-name {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #222;
}

.profile-description {
  font-size: 1.1rem;
  color: #666;
  
}

.search-container {
  display: flex;
  justify-content: center; /* Centraliza horizontalmente */
  margin-bottom: 1.5rem;
  width: 100%;

}

.search-wrapper {
  position: relative;
  width: 100%;
  max-width: 90%;
}

.search-icon {
  position: absolute;
  top: 50%;
  left: 16px;
  transform: translateY(-50%);
  color: #4c65c0;
  font-size: 1rem;
  z-index: 2;
}

.search-input {
  padding-left: 40px !important;
  padding-right: 16px;
  padding: 0.5rem 1rem;
  border-radius: 50px;
  border: 2px solid #0037ff;
  box-shadow: 5px 5px 15px rgba(4, 92, 255, 0.575);
  width: 100%;
}

/* Centraliza os botões de filtro */
.btn-group {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;
}


.btn-group .btn {
  background: #020021;
  color: white;
  border: 1px solid white;
  padding: 0.6rem 7.2rem;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.btn-group .btn:hover {
    background: linear-gradient(90deg, #748cf7,#1948f4, #03109d);
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(0, 123, 255, 0.35);
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  justify-content: center;
  margin: 30px 50px; /* Aumenta as margens laterais */
}

.card-component {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}
.card-component:hover {
  transform: translateY(-6px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}
.pagination {
  display: flex;
  justify-content: center;
  list-style: none;
  padding: 0;
  margin-top: 2rem;
}

.page-item {
  margin: 0 0.4rem;
}

.page-link {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  background-color: #fff;
  color: #333;
  border: 1px solid #ccc;
  text-decoration: none;
  transition: all 0.3s ease;
}

.page-link:hover {
  background: linear-gradient(90deg, #748cf7,#1948f4, #03109d);
  color: #fff;
  border-color: #007bff;
}

.page-item.active .page-link {
  font-weight: bold;
  background-color: #0056b3;
  color: #fff;
}

.btn-remover {
  background: #5d5c5cf0;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 0.75rem 1rem;
  font-weight: bold;
  transition: all 0.3s ease-in-out;
  width: 100%;
  text-align: center;
}

.btn-remover:hover {
  background: linear-gradient(90deg, #ff4e4e, #ff1f1f, #d60000);
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(255, 0, 0, 0.5);
}

.profile-description {
  font-style: italic;
  color: #666;
  margin-top: 6px;
  max-width: 320px;
  word-wrap: break-word;
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
