<template>
  <div class="card mb-3 custom-card" @click="handleClick">
    <img :src="fullImageUrl" class="card-img-top" alt="Imagem do Jogo" />
    <div class="card-body">
      <h5 class="card-title">{{ nome }}</h5>
      <p class="card-text">{{ truncatedDescricao }}</p>
      <p class="card-text"><strong>Modo de Jogo:</strong> {{ modoJogo }}</p>
      <p class="card-text"><strong>Data de Lançamento:</strong> {{ dataLancamento }}</p>
      <p class="card-text accesses-count"><strong>Acessos:</strong> {{ numeroAcessos }}</p>

      <!-- Botões visíveis apenas se for ADMIN -->
      <div v-if="authStore.isAdmin" class="admin-buttons mt-3">
        <button @click.stop="emitEdit" class="btn btn-warning me-2">Editar</button>
        <button @click.stop="emitDelete" class="btn btn-danger">Excluir</button>
      </div>

      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
import { defineProps, computed, defineEmits } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import imagemPadrao from '@/assets/jogosSemImagem.jpg';
import api from 'axios';

const router = useRouter();
const authStore = useAuthStore();
const emit = defineEmits(['card-click',  'access-incremented', 'edit', 'delete']);

const props = defineProps({
  id: Number,
  nome: String,
  modoJogo: String,
  resumo: String,
  dataLancamento: String,
  capa: String,
  numeroAcessos: { type: Number, default: 0 }
});

const maxLength = 100;
const truncatedDescricao = computed(() =>
  props.resumo?.length > maxLength
    ? props.resumo.slice(0, maxLength) + '...'
    : props.resumo
);

const fullImageUrl = computed(() =>
  !props.capa || props.capa.trim() === ''
    ? imagemPadrao
    : props.capa.startsWith('http')
      ? props.capa
      : `https:${props.capa.replace('t_thumb', 't_cover_big')}`
);

const handleClick = async () => {
  try {
    await api.patch(`http://localhost:8080/api/jogos/${props.id}/acesso`);
    console.log(`Acesso incrementado para o jogo ID: ${props.id}`);
    emit('access-incremented', props.id);
  } catch (error) {
    console.error(`Erro ao incrementar acessos para o jogo ID: ${props.id}`, error);
  } finally {
    router.push({ name: 'DetalhesPage', params: { id: props.id } });
  }
};

const emitEdit = () => emit('edit', props.id);
const emitDelete = () => emit('delete', props.id);
</script>

<style scoped>
:root {
  --card-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
  --image-background: linear-gradient(145deg, #f8f9fa, #e9ecef);
  --text-color: #5a6474;
  --footer-color: #9a9fa8;
  --transition-speed: 0.3s;
}
.custom-card {
  width: 320px;
  background: #ffffff;
  cursor: pointer;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform var(--transition-speed) ease, box-shadow var(--transition-speed) ease;
}

.custom-card:hover {
  transform: translateY(-5px) scale(1.03);
  box-shadow: 0 10px 20px #a1d9ffaa;
}

.card-img-top {
  width: 100%;
  height: 220px;
  object-fit: contain; /* para exibir a imagem completa */
  background-color: #f5f5f5; /* cor neutra atrás da imagem */
  border-bottom: 1px solid #e0e0e0;
  padding: 10px; /* espaçamento para a imagem não ficar grudada */
}

.card-body {
  flex-grow: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card-title {
  font-family: 'Inter', sans-serif;
  font-weight: 700;
  font-size: 1.25rem;
  color: #1a1d24;
  margin-bottom: 0.75rem;
  line-height: 1.4;
}

.card-text {
  font-family: 'Inter', sans-serif;
  color: #666;
  font-size: 0.9rem;
  flex-grow: 1;
  margin-bottom: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3; /* limita a 3 linhas */
  line-clamp: 3; /* padrão para compatibilidade */
  -webkit-box-orient: vertical;
}

.card-footer {
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  color: #999;
  text-align: right;
}
.accesses-count {
  font-weight: bold;
  color: #000;
  margin-top: 10px;
}

.admin-buttons {
  display: flex;
  justify-content: center; /* Centraliza horizontalmente */
  gap: 40px; /* Espaço entre os botões */
  margin-top: 10px;
}

.admin-buttons button {
  border: none;
  border-radius: 50px;
  color: #fff;
  padding: 10px 40px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 55, 255, 0.575);
}

/* ✏️ Botão Editar */
.admin-buttons button:nth-child(1) {
  background: #959493;
}

.admin-buttons button:nth-child(1):hover {
  background: linear-gradient(90deg, #f4cf62, #c69500);
  transform: translateY(-2px);
}

/* 🗑️ Botão Excluir */
.admin-buttons button:nth-child(2) {
  background: #959493;
}

.admin-buttons button:nth-child(2):hover {
  background: linear-gradient(90deg, #c82333, #bd2130);
  transform: translateY(-2px);
}

</style>
