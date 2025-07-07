<template>
  <div class="modal">
    <div class="modal-content">
      <span class="close" @click="$emit('close')">&times;</span>
      <h3>Avalie este jogo</h3>

      <!-- Estrelas -->
      <div class="stars">
        <span
          v-for="star in 5"
          :key="star"
          @click="selectStar(star)"
          :style="{
            color: star <= estrelas ? '#f90' : '#ccc',
            cursor: 'pointer',
            fontSize: '24px'
          }"
        >
          ★
        </span>
      </div>

      <!-- Campo de comentário -->
      <textarea
        v-model="texto"
        placeholder="Escreva seu comentário"
        required
        rows="4"
      ></textarea>

      <!-- Botão de envio -->
      <button @click="enviarComentario">Enviar</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import ComentarioService from '@/services/ComentarioService';

const authStore = useAuthStore(); // instancia authStore

const props = defineProps({
  gameId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['close', 'comentarioEnviado']);

const estrelas = ref(0);
const texto = ref('');

// Define estrela selecionada
function selectStar(star) {
  estrelas.value = star;
}

// Envia comentário
async function enviarComentario() {
  // Verifica se está autenticado
  if (!authStore.autenticado) {
    alert('Você precisa estar autenticado para comentar.');
    return;
  }

  if (!texto.value.trim()) {
    alert('Por favor, escreva um comentário.');
    return;
  }

  if (estrelas.value === 0) {
    alert('Por favor, selecione pelo menos uma estrela.');
    return;
  }

  try {
    await ComentarioService.criarComentario({
      jogoId: props.gameId,
      texto: texto.value,
      estrelas: estrelas.value
    });
    emit('comentarioEnviado');
    emit('close');
  } catch (e) {
    alert(e.message || 'Erro ao enviar comentário.');
    console.error(e);
  }

}
</script>

<style scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(2px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-content {
  background: #fefefe;
  border-radius: 16px;
  padding: 30px 24px;
  width: 90%;
  max-width: 420px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  position: relative;
  text-align: center;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    transform: scale(0.95);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.close {
  position: absolute;
  top: 12px;
  right: 16px;
  font-size: 24px;
  font-weight: bold;
  color: #555;
  cursor: pointer;
  transition: color 0.2s ease;
}

.close:hover {
  color: #000;
}

h3 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #333;
}

.stars {
  margin-bottom: 20px;
}

.stars span {
  font-size: 28px;
  transition: transform 0.2s ease;
}

.stars span:hover {
  transform: scale(1.2);
}

textarea {
  width: 90%;
  padding: 12px;
  border: 1px solid #041dfb;
  border-radius: 10px;
  font-size: 15px;
  resize: none;
  outline: none;
  transition: border-color 0.3s ease;
}

textarea:focus {
  border-color: #4f73ff;
  box-shadow: 0 0 0 3px rgba(79, 115, 255, 0.2);
}

button {
  width: 100%;
  padding: 12px;
  margin-top: 20px;
  background: linear-gradient(90deg, #748cf7,#1948f4, #03109d);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

button:hover {
  background-position: right center;
  box-shadow: 0 6px 16px rgba(50, 74, 213, 0.4);
  transform: translateY(-2px);
}
</style>
