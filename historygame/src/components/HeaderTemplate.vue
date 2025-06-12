<template>
  <header class="cabeca">
    <img src="../assets/logo.png" alt="Logo" class="logo">
    <nav>
      <ul>
        <li><router-link to="/">Inicial</router-link></li>
        <li><router-link to="/jogos">Jogos</router-link></li>
        <li v-if="autenticado"><router-link to="/favoritos">Lista Pessoal</router-link></li>
        <li><router-link to="/sobre">Sobre</router-link></li>
      </ul>
    </nav>
    <button @click="entrar" v-if="!autenticado" class="login">Entrar</button>
    <button @click="registrar" v-if="!autenticado" class="registrar">Registrar</button>
    <button v-if="autenticado" class="user" @click="perfil">
      <img src="../assets/do-utilizador.png" alt="UserPic" class="userPic">
    </button>
    <button v-if="autenticado" @click="handleLogout" class="logout">Sair</button>
  </header>
</template>

<script>
import { useRouter } from 'vue-router';
import { onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/authStore';

export default {
  name: 'HeaderTemplate',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();

    const autenticado = computed(() => authStore.autenticado); // reatividade correta

    const entrar = () => router.push("/login");
    const registrar = () => router.push("/registro");
    const perfil = () => router.push("/perfil");

    const handleLogout = async () => {
      try {
        await authStore.logout();
        router.push("/");
      } catch (error) {
        console.error(error);
        alert("Erro ao fazer logout.");
      }
    };

    onMounted(() => {
      authStore.verificarAuth();
    });

    return {
      autenticado,
      handleLogout,
      entrar,
      registrar,
      perfil
    };
  }
};
</script>



<style scoped>
.cabeca {
    background-color: black;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    flex-wrap: wrap;
    margin-top: -10px;
    margin-left: -5px;
    margin-right: -5px;
}

.logo {
    width: 80px;
    margin-left: 150px;
}

.userPic {
    width: 40px;
}

nav {
    flex-grow: 1;
    text-align: center;
}

nav ul {
    list-style-type: none;
    padding: 0;
    margin: 0;
    display: flex;
    justify-content: center;
}

nav ul li {
    margin: 0 15px;
}

nav ul li a {
    text-decoration: none;
    color: white;
}


button.login {
    background-color: white;
    color: black;
    border: none;
    padding: 10px 20px;
    margin-right: 10px;
    cursor: pointer;
    border-radius: 10px;
}
button.user {
    width: 50px;
    height: 50px;
    margin-right: 10px;
    padding: 0px;
    cursor: pointer;
    border-radius: 50px;
}

button.registrar {
    background-color: black;
    color: white;
    border: 1px solid white;
    padding: 10px 20px;
    margin-right: 150px;
    cursor: pointer;
    border-radius: 10px;
}

button.logout {
    background-color: black;
    color: white;
    border: 1px solid white;
    padding: 10px 20px;
    margin-right: 150px;
    cursor: pointer;
    border-radius: 10px;
}


button.login:hover, button.registrar:hover, button.user:hover, button.logout:hover {
    opacity: 0.7;
}

/* Responsividade */
@media (max-width: 768px) {
    nav ul {
        flex-direction: column;
    }

    button {
        margin-top: 10px;
    }

    .logo {
        margin-left: 0;
    }

    nav {
        text-align: center;
        margin-top: 10px;
    }

    .cabeca {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>