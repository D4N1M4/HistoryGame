<template>
  <div class="editar-jogo-page container mt-4">
    <h2 class="mb-4">Editar Jogo</h2>

    <form @submit.prevent="salvarAlteracoes">
      <div class="mb-3">
        <label for="nome" class="form-label">Nome</label>
        <input v-model="jogo.nome" type="text" class="form-control" id="nome" required />
      </div>

      <div class="mb-3">
        <label for="resumo" class="form-label">Resumo</label>
        <textarea v-model="jogo.resumo" class="form-control" id="resumo" rows="3" required></textarea>
      </div>

      <div class="mb-3">
        <label for="modoJogo" class="form-label">Modo de Jogo</label>
        <input v-model="jogo.modoJogo" type="text" class="form-control" id="modoJogo" required />
      </div>

      <div class="mb-3">
        <label for="dataLancamento" class="form-label">Data de Lançamento</label>
        <input v-model="jogo.dataLancamento" type="date" class="form-control" id="dataLancamento" required />
      </div>

      <div class="mb-3">
        <label for="capa" class="form-label">URL da Capa</label>
        <input v-model="jogo.capa" type="text" class="form-control" id="capa" />
      </div>

      <button type="submit" class="btn btn-primary">Salvar Alterações</button>
      <button type="button" class="btn btn-secondary ms-2" @click="voltar">Cancelar</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import JogoService from '@/services/JogoService';

const route = useRoute();
const router = useRouter();
const jogoService = new JogoService();
const jogo = ref({
  nome: '',
  resumo: '',
  modoJogo: '',
  dataLancamento: '',
  capa: ''
});

const carregarJogo = async () => {
  try {
    const jogoCarregado = await jogoService.getGameById(route.params.id);
    jogo.value = jogoCarregado;
  } catch (error) {
    console.error('Erro ao carregar jogo:', error);
  }
};

const salvarAlteracoes = async () => {
  try {
    await jogoService.updateGame(route.params.id, jogo.value);
    alert('Jogo atualizado com sucesso!');
    router.push('/jogos'); // ou outra rota
  } catch (error) {
    console.error('Erro ao salvar alterações:', error);
    alert('Erro ao atualizar o jogo.');
  }
};

const voltar = () => {
  router.back();
};

onMounted(() => {
  carregarJogo();
});
</script>
