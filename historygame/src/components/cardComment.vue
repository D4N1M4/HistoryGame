<template>
  <div class="comment-card">
    <!-- Estrelas -->
    <div class="stars">
      <span
        v-for="s in 5"
        :key="s"
        :style="{ color: s <= estrelas ? '#f90' : '#ccc', fontSize: '20px' }"
      >
        ★
      </span>
    </div>

    <!-- Texto do comentário -->
    <p>{{ texto }}</p>

    <!-- Dados do usuário e data -->
    <small>
      Por: {{ nome || 'Anônimo' }} ({{ email || 'sem email' }}) em
      {{ formatarData(dataCriacao) }}
    </small>

    <!-- Botão de excluir visível apenas para admin -->
    <button v-if="isAdmin" class="delete-button" @click="$emit('excluir-comentario')">
      Excluir
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  texto: String,
  estrelas: Number,
  dataCriacao: String,
  nome: String,
  email: String,
  isAdmin: Boolean
});

// Função para formatar a data de forma segura
function formatarData(data) {
  if (!data) return 'Data desconhecida';
  const d = new Date(data);
  return d.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
}
</script>

<style scoped>
.comment-card {
  background-color: #f8f8f8;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: relative; /* necessário para posicionar o botão dentro */
  padding-top: 40px; /* espaço para o botão no topo */
}

.stars {
  margin-bottom: 8px;
}

.delete-button {
  position: absolute;
  top: 12px;
  right: 12px;
  
  margin: 0;
  padding: 6px 12px;
  background-image: linear-gradient(90deg, #f84557, #c82333);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.5);
  transition: background-position 0.4s ease, box-shadow 0.3s ease;
  background-size: 200% 100%;
  background-position: left center;
  user-select: none;
}

.delete-button:hover {
  background-position: right center;
  box-shadow: 0 6px 16px rgba(220, 53, 69, 0.7);
}

.delete-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.6);
}
</style>
