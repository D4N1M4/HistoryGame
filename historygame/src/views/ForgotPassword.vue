<template>
  <div class="forgot-password-container">
    <h1>Redefinir Senha</h1>
    <form @submit.prevent="handleForgotPassword">
      <label for="email">Email</label>
      <input type="email" id="email" v-model="email" required />

      <label for="novaSenha">Nova Senha</label>
      <input type="password" id="novaSenha" v-model="novaSenha" required />

      <button type="submit">Redefinir Senha</button>
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

  
  <style>
  /* Estilos baseados na tela de login */
  
  .forgot-password-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
    background-color: #f4f4f4;
  }
  
  h1 {
    color: #333;
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  form {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 300px;
  }
  
  label {
    color: #555;
    font-size: 14px;
    margin-bottom: 5px;
  }
  
  input {
    width: 100%;
    padding: 10px;
    margin-bottom: 15px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  
  .submit-button {
    width: 100%;
    padding: 10px;
    background-color: #4285F4; /* Azul Google */
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .submit-button:hover {
    background-color: #357ae8; /* Azul mais escuro no hover */
  }
  </style>
  