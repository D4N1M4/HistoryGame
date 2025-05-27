<template>
  <div class="card mb-3 custom-card" @click="handleClick">
    <img :src="fullImageUrl" class="card-img-top" alt="Imagem do Jogo" />
    <div class="card-body">
      <h5 class="card-title">{{ nome }}</h5>
      <p class="card-text">{{ truncatedDescricao }}</p>
      <p class="card-text"><strong>Modo de Jogo:</strong> {{ modoJogo }}</p>
      <p class="card-text"><strong>Data de Lançamento:</strong> {{ dataLancamento }}</p>

      <slot name="footer"></slot>
    </div>
  </div>
</template>

<!-- cardComponent.vue -->
<script setup>
import { defineProps, computed, defineEmits } from 'vue';
import { useRouter } from 'vue-router';
import imagemPadrao from '@/assets/jogosSemImagem.jpg';

const router = useRouter();
const emit = defineEmits(['card-click']);

const props = defineProps({
  id: { type: Number, required: true },
  nome: { type: String, required: true },
  modoJogo: { type: String, required: true },
  resumo: { type: String, required: true },
  dataLancamento: { type: String, required: true },
  capa: { type: String, required: true },
});

const maxLength = 100;

const truncatedDescricao = computed(() => {
  return props.resumo && props.resumo.length > maxLength
    ? props.resumo.substring(0, maxLength) + '...'
    : props.resumo;
});

const fullImageUrl = computed(() => {
  if (!props.capa || props.capa.trim() === '') {
    return imagemPadrao;
  }
  return props.capa.startsWith('http')
    ? props.capa
    : `https:${props.capa.replace('t_thumb', 't_cover_big')}`;
});

const handleClick = () => {
  router.push({ name: 'DetalhesPage', params: { id: props.id } });
};
</script>


<style scoped>
.custom-card {
  width: 320px;
  background: #fff;
  cursor: pointer;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.custom-card:hover {
  transform: translateY(-5px) scale(1.03);
  box-shadow: 0 10px 20px rgba(0,0,0,0.15);
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
  font-weight: 700;
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 10px;
  line-height: 1.3;
}

.card-text {
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
  font-size: 0.8rem;
  color: #999;
  text-align: right;
}
</style>
