<template>
  <div class="genre-card">
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-lg font-semibold">Categorias</h3>
      <button @click="toggleCategories" class="focus:outline-none text-white">
        <span v-if="showCategories">▲</span>
        <span v-else>▼</span>
      </button>
    </div>

    <div v-show="showCategories" class="categories-list">
      <button
        v-for="genero in generos"
        :key="genero.id"
        @click="selectGenre(genero)"
        class="category-button"
      >
        {{ genero.nome }}
      </button>
      <button @click="clearSelection" class="category-button clear">Limpar filtro</button>
    </div>
  </div>
</template>

<script>
import GeneroService from '@/services/GeneroService'; // serviço para buscar gêneros

export default {
  name: "GenreFilter",
  data() {
    return {
      generos: [],
      selectedGenre: null,
      showCategories: true,
    };
  },
  mounted() {
    this.loadGenres();
  },
  methods: {
    async loadGenres() {
      const service = new GeneroService();
      this.generos = await service.getAllGeneros();
    },
    toggleCategories() {
      this.showCategories = !this.showCategories;
    },
    selectGenre(genero) {
      this.selectedGenre = genero;
      this.$emit('genre-selected', genero);
    },
    clearSelection() {
      this.selectedGenre = null;
      this.$emit('genre-selected', null);
    }
  }
};
</script>

  
<style scoped>
  .genre-card {
    background-color: #020021;
    color: #fff;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 8px rgba(255, 255, 255, 0.1);
    min-width: 240px;
    margin-top: 20px;
  }
  
  .category-button {
    background-color: #6dceff47;
    color: #fff;
    padding: 10px 16px;
    border: 1px solid #0138fd;
    border-radius: 8px;
    cursor: pointer;
    text-align: left;
    transition: background-color 0.2s ease, transform 0.1s ease;
  }
  
  .category-button:hover {
    background: linear-gradient(90deg, #748cf7,#1948f4, #03109d);
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(0, 123, 255, 0.35);
  }
  
  .game-button {
    background-color: #2a2a2a;
    color: #fff;
    padding: 10px 16px;
    border: 1px solid #555;
    border-radius: 8px;
    cursor: pointer;
    text-align: left;
    transition: background-color 0.2s ease, transform 0.1s ease;
  }
  
  .game-button:hover {
    background-color: #444;
    transform: translateY(-1px);
  }

  .categories-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.games-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 10px;
}

.games-list li {
  list-style: none;
}
</style>