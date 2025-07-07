<template>
  <div class="forgot-password-container">
    <h1>Redefinir Senha</h1>
    <form @submit.prevent="handleForgotPassword">
      <label for="email">Email</label>
      <input type="email" id="email" v-model="email" required />

      <label for="novaSenha">Nova Senha</label>
      <input type="password" id="novaSenha" v-model="novaSenha" required />

      <button type="submit" class="submit-button" >Redefinir Senha</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/assets/js/axios';

const email = ref('');
const novaSenha = ref('');
const router = useRouter();

const handleForgotPassword = async () => {
  try {
    await api.post('/usuarios/redefinir-senha', {
      email: email.value,
      novaSenha: novaSenha.value
    });

    window.alert('Senha alterada com sucesso!');
    router.push('/login');
  } catch (err) {
    console.error(err);

      const msg = err.response?.data?.message || 'E-mail não encontrado. Verifique se digitou corretamente.';
      window.alert(msg);

  }
};
</script>

  
<style scoped>
/* Fundo e container central */
.forgot-password-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #e0eafc, #cfdef3);
  padding: 20px;
}

/* Título */
h1 {
  color: #222;
  font-size: 2rem;
  margin-bottom: 1.5rem;
  text-align: center;
}

/* Formulário */
form {
  background-color: #020021;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* Labels */
label {
  color: #fefefe;
  font-size: 0.95rem;
  margin-bottom: 0.3rem;
}

/* Campos de input */
input {
  width: 90%;
  padding: 0.75rem 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
  transition: border-color 0.3s, box-shadow 0.3s;
}

input:focus {
  border-color: #0213fb;
  outline: none;
  box-shadow: 0 0 0 3px rgba(66, 133, 244, 0.2);
}

/* Botão de envio */
.submit-button {
  width: 100%;
  padding: 0.75rem 1rem;
  background: linear-gradient(90deg, #748cf7,#1948f4, #03109d);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(66, 133, 244, 0.4);
}

.submit-button:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(66, 133, 244, 0.3);
}

/* Responsividade */
@media (max-width: 480px) {
  h1 {
    font-size: 1.5rem;
  }

  form {
    padding: 1.5rem;
  }
}
</style>
