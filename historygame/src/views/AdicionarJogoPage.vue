<template>
  <div class="add-game-page container">
    <h1>Adicionar Novo Jogo</h1>

    <form @submit.prevent="submitForm">
      <div class="mb-3">
        <label for="nome" class="form-label">Nome</label>
        <input v-model="form.nome" type="text" id="nome" class="form-control" required />
      </div>

      <div class="mb-3">
        <label for="resumo" class="form-label">Resumo</label>
        <textarea v-model="form.resumo" id="resumo" class="form-control" rows="3"></textarea>
      </div>

      <div class="mb-3">
        <label for="modoJogo" class="form-label">Modo de Jogo</label>
        <input v-model="form.modoJogo" type="text" id="modoJogo" class="form-control" />
      </div>

      <div class="mb-3">
        <label for="dataLancamento" class="form-label">Data de Lançamento</label>
        <input v-model="form.dataLancamento" type="date" id="dataLancamento" class="form-control" />
      </div>

      <div class="mb-3">
        <label for="capa" class="form-label">URL da Capa</label>
        <input v-model="form.capa" type="url" id="capa" class="form-control" />
      </div>

      <div class="mb-3">
        <label for="generos" class="form-label">Gêneros (separados por vírgula)</label>
        <input v-model="generosInput" type="text" id="generos" class="form-control" placeholder="Ex: Terror, Puzzle, Single" />
      </div>

      <button type="submit" class="btn btn-primary">Salvar</button>
    </form>

    <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
    <div v-if="success" class="alert alert-success mt-3">Jogo adicionado com sucesso!</div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import JogoService from '@/services/JogoService';
import { useRouter } from 'vue-router';

const router = useRouter();
const jogoService = new JogoService();

const form = ref({
  nome: '',
  resumo: '',
  modoJogo: '',
  dataLancamento: '', // já no formato yyyy-MM-dd pelo input date
  capa: ''
});

const generosInput = ref('');

const error = ref('');
const success = ref(false);

const submitForm = async () => {
  error.value = '';
  success.value = false;

  const generosArray = generosInput.value
    .split(',')
    .map(g => g.trim())
    .filter(g => g.length > 0)
    .map(g => ({ nome: g }));

  // Validação simples:
  if (!form.value.nome) {
    error.value = 'Nome é obrigatório';
    return;
  }
  if (!form.value.dataLancamento) {
    error.value = 'Data de lançamento é obrigatória';
    return;
  }
  if (!form.value.capa) {
    error.value = 'URL da capa é obrigatória';
    return;
  }

  const payload = {
    ...form.value,
    generos: generosArray
  };

  console.log('Payload enviado:', payload);

  try {
    await jogoService.createGame(payload);
    success.value = true;
  } catch (err) {
    error.value = 'Erro ao salvar o jogo: ' + (err.response?.data?.message || err.message);
    console.error(err);
  }
};

</script>

<style scoped>
.add-game-page {
  max-width: 600px;
  margin: 40px auto;
}
</style>

