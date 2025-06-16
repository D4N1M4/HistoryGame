<template>
  <div class="login-container">
    <h1>Login</h1>

    <form @submit.prevent="handleLogin">
      <label for="username">Usuário</label>
      <input type="text" id="username" v-model="email" required />

      <label for="password">Senha</label>
      <input type="password" id="password" v-model="password" required />

      <button type="submit">Entrar</button>

      <button @click="loginWithGoogle" type="button" class="gsi-material-button">
        <div class="gsi-material-button-state"></div>
        <div class="gsi-material-button-content-wrapper">
          <div class="gsi-material-button-icon">
            <!-- Ícone do Google -->
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48">
              <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/>
              <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
              <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/>
              <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/>
            </svg>
          </div>
          <span class="gsi-material-button-contents">Entrar com Google</span>
        </div>
      </button>

      <router-link class="forgot-password" to="/recuperarsenha">
        Esqueceu a senha?
      </router-link>
    </form>
  </div>
</template>


<script>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

export default {
  setup() {
    const email = ref("");
    const password = ref("");
    const router = useRouter();
    const authStore = useAuthStore();

    const handleLogin = async () => {
      try {
        const response = await fetch("http://localhost:8080/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded",
          },
          body: new URLSearchParams({
            username: email.value,
            password: password.value,
          }),
          credentials: "include",
        });

    if (!response.ok && !(response.data && response.status === 200) ) { 
        const errorData = response.body ? await response.json().catch(() => response.text()) : 'Erro desconhecido';
        throw new Error(typeof errorData === 'string' ? errorData : errorData.message || "Usuário ou senha inválidos");
    }
        await authStore.verificarAuth(); 

        email.value = "";
        password.value = "";

        router.push("/favoritos");
      } catch (error) {
        alert(error.message || "Erro ao fazer login");
        console.error(error);
      }
    };

    const loginWithGoogle = () => {
      window.location.href = "http://localhost:8080/oauth2/authorization/google";
    };

    return {
      email,
      password,
      handleLogin,
      loginWithGoogle,
    };
  },
};
</script>


<style scoped>
/* Fundo e container central */
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
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
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
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
button {
  width: 100%;
  padding: 0.75rem 1rem;
  background: linear-gradient(90deg, #748cf7, #1948f4, #03109d);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(66, 133, 244, 0.4);
}

button:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(66, 133, 244, 0.3);
}

/* Link Esqueceu a senha */
.forgot-password { 
  margin-top: 8px;
  display: block;
  color: #fefefe;
  text-decoration: none;
  font-size: 0.85rem;
  text-align: right;
}

.forgot-password:hover { 
  text-decoration: underline;
}

/* Estilização para o botão de login com Google */
.gsi-material-button {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border: 1px solid #747775;
  border-radius: 8px;
  padding: 0.50rem 1rem;
  cursor: pointer;
  font-family: 'Roboto', arial, sans-serif;
  font-size: 14px;
  transition: background-color 0.2s, box-shadow 0.2s;
}

.gsi-material-button:hover {
  box-shadow: 0 1px 2px rgba(0, 85, 255, 0.3), 0 1px 3px 1px rgba(3, 69, 250, 0.15);
}

.gsi-material-button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.gsi-material-button .gsi-material-button-icon {
  height: 20px;
  width: 20px;
  margin-right: 12px;
}

.gsi-material-button .gsi-material-button-contents {
  font-weight: 500;
}

/* Responsividade */
@media (max-width: 480px) {
  h1 {
    font-size: 1.5rem;
  }

  form {
    padding: 1.5rem;
  }

  input {
    width: 100%;
  }
}
</style>
