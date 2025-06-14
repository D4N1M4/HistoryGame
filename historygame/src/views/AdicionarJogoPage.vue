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

      <div class="mb-3 campo-data">
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

      <div class="button-container">
        <button type="submit" class="btn btn-primary">Salvar</button>
      </div>
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
  max-width: 700px;
  margin: 3rem auto;
  background-color: #000000;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  transition: all 0.3s ease-in-out;
}

.add-game-page h1 {
  text-align: center;
  margin-bottom: 2rem;
  font-size: 2rem;
  color: #d3d2d2;
  font-weight: 600;
}

.form-label {
  font-weight: 500;
  color: #d3d2d2;
  margin-bottom: 0.5rem;
  display: block;
}

/* Aplica altura padrão apenas para inputs de texto e URL */
input[type="text"].form-control,
input[type="url"].form-control {
  height: 2.5rem;
}

/* Estilo base para todos os campos */
.form-control {
  width: 265%;
  height: 2.5rem;
  padding: 0.625rem 1rem;
  font-size: 1rem;
  border-radius: 10px;
  border: 1px solid #000000;
  background-color: #d3d2d2;
  color: #333;
  box-sizing: border-box;
}
.campo-data {
  width: 100%;         /* Faz o campo ocupar toda a largura do card */
  margin: 0 0 1rem 4.3rem;      /* Centraliza horizontalmente */
}

.campo-data .form-control {
  width: 202%; /* garante que o input também ocupe tudo */
  margin: 0px;    /* remove qualquer margem lateral */

}

/* Estilo para textarea com altura específica */
textarea.form-control {
  resize: vertical;
  height: 6rem;
}
.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
  outline: none;
}

.button-container {
  display: center;
  justify-content: center;
  margin-top: 1.5rem;
}

button.btn-primary {
  background: linear-gradient(90deg, #6fa4ff,#26a1ff, #0056b3);
  border: none;
  border-radius: 10px;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  color: white;
  min-width: 200px; /* Largura mínima para o botão */
  width: 180%; /* A largura se ajusta ao conteúdo */
  transition: all 0.3s ease;
  cursor: pointer;
  margin: 0 1rem 0 4.3rem;
}


button.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 123, 255, 0.35);
}

.alert {
  border-radius: 10px;
  padding: 1rem;
  font-size: 0.95rem;
  margin-top: 1.5rem;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.alert-danger {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

/* Responsividade */
@media (max-width: 768px) {
  .add-game-page {
    margin: 1rem;
    padding: 1.5rem;
  }

  .add-game-page h1 {
    font-size: 1.5rem;
  }

  button.btn-primary {
    font-size: 0.95rem;
  }
}
</style>


