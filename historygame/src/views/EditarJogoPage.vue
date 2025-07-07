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

      <div class="mb-3 campo-data">
        <label for="dataLancamento" class="form-label">Data de Lançamento</label>
        <input v-model="jogo.dataLancamento" type="date" class="form-control" id="dataLancamento" required />
      </div>

      <div class="mb-3">
        <label for="capa" class="form-label">URL da Capa</label>
        <input v-model="jogo.capa" type="text" class="form-control" id="capa" />
      </div>

      <div class="button-container">
        <button type="submit" class="btn btn-primary">Salvar Alterações</button>
        <button type="button" class="btn btn-secondary ms-2" @click="voltar">Cancelar</button>
      </div>
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

<style scoped>
.editar-jogo-page {
  max-width: 700px;
  margin: 3rem auto;
  background-color: #020021;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  transition: all 0.3s ease-in-out;
}

.editar-jogo-page h2 {
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

/* Inputs de texto e URL */
input[type="text"].form-control,
input[type="url"].form-control {
  height: 2.5rem;
}

/* Campo de data */
input[type="date"].form-control {
  height: 2.5rem;
}

/* Estilo base dos campos */
.form-control {
  width: 100%;
  padding: 0.625rem 1rem;
  font-size: 1rem;
  border-radius: 10px;
  border: 1px solid #000000;
  background-color: #d3d2d2;
  color: #333;
  box-sizing: border-box;
}

/* Campo específico para data */
.campo-data {
  width: 100%;
  display: flex;
  flex-direction: column;
  margin: 0 0 1rem 0;

}

.campo-data .form-control {
  width: 100%;
}

/* Textarea */
textarea.form-control {
  resize: vertical;
  height: 6rem;
}

/* Efeito no foco dos campos */
.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
  outline: none;
}

.button-container {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;
}

/* Botões */
button.btn-primary {
  background: linear-gradient(90deg, #748cf7,#1948f4, #03109d);
  border: none;
  border-radius: 10px;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  color: white;
  width: auto;
  min-width: 180px;
  transition: all 0.3s ease;
  cursor: pointer;
  margin-top: 10px;
}

button.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(5, 55, 255, 0.35);
}

button.btn-secondary {
  background-color: #6c757d;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 500;
  color: white;
  width: auto;
  margin-top: 10px;
  min-width: 180px;

  transition: all 0.3s ease;
  cursor: pointer;
}

button.btn-secondary:hover {
  background: linear-gradient(90deg, #d60000, #ff1f1f, #ff4e4e);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 0, 0, 0.466);
}

/* Alertas */
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
  .editar-jogo-page {
    margin: 1rem;
    padding: 1.5rem;
  }

  .editar-jogo-page h2 {
    font-size: 1.5rem;
  }

  button.btn-primary,
  button.btn-secondary {
    font-size: 0.95rem;
  }
}
</style>
