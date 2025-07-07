<template>
  <header class="cabeca">
    <img src="../assets/logo.png" alt="Logo" class="logo">
    <nav>
      <ul>
        <li><router-link to="/">Inicial</router-link></li>
        <li><router-link to="/jogos">Jogos</router-link></li>
        <li v-if="autenticado"><router-link to="/favoritos">Meus Jogos</router-link></li>
        <li><router-link to="/sobre">Sobre</router-link></li>
      </ul>
    </nav>
    <button @click="entrar" v-if="!autenticado" class="login">Entrar</button>
    <button @click="registrar" v-if="!autenticado" class="registrar">Registrar</button>
    <button v-if="autenticado" class="user" @click="perfil">
<img
  :src="fotoPerfil"
  alt="Avatar"
  class="avatar-icon"
  @error="event => event.target.src = defaultAvatar"
/>
    </button>
    <button v-if="autenticado" @click="handleLogout" class="logout">Sair</button>
  </header>
</template>

<script>
import { useRouter } from 'vue-router';
import { onMounted, computed } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import defaultAvatar from '@/assets/default_avatar.jpg';

export default {
  name: 'HeaderTemplate',
  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const usuario = computed(() => authStore.usuario);
const fotoPerfil = computed(() => {
  const foto = usuario.value?.foto;
  return foto && foto.trim() !== '' ? foto : defaultAvatar;
});
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
  perfil,
  fotoPerfil,
  defaultAvatar
};

  }
};
</script>



<style scoped>
.cabeca {
  background-color: #020021;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  flex-wrap: wrap;
  margin: 0 -0.5rem;
}

.logo {
  width: 5rem;
  margin-left: 10rem;
}

.userPic {
  width: 2.5rem;
  height: 2.5rem;
  object-fit: cover;
  border-radius: 50%;
}

nav {
  flex-grow: 1;
  text-align: center;
}

nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;
}

nav ul li a {
  position: relative;
  text-decoration: none;
  color: #fff;
  font-weight: 500;
  transition: color 0.3s ease, transform 0.3s ease;
  padding-bottom: 0.2rem;
}

nav ul li a::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0%;
  height: 2px;
  background-color: #fff;
  transition: width 0.3s ease, left 0.3s ease;
  transform: translateX(-50%);
}

nav ul li a:hover {
  color: #558afc;
  transform: translateY(-2px);
}

nav ul li a:hover::after {
  width: 100%;
}
.userIcon {
  font-size: 1.5rem;
  color: white;
}


/* Botões */
button {
  border: none;
  cursor: pointer;
  border-radius: 0.625rem;
  padding: 0.625rem 1.25rem;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

button.login {
  background-color: #fff;
  color: #000;
  margin-right: 0.625rem;
}

button.user {
  background-color: transparent;
  width: 3.125rem;
  height: 3.125rem;
  padding: 0;
  margin-right: 0.625rem;
}

button.registrar {
  background-color: transparent;
  color: #fff;
  border: 1px solid #fff;
  margin-right: 10rem;
}

button.logout {
  background-color: transparent;
  color: #fff;
  border: 1px solid #fff;
  margin-right: 10rem;
}

button:hover {
  transform: translateY(-5px) scale(1.03);
  opacity: 0.8;
  box-shadow: 2px 5px 20px #6abdf599;
}
.avatar-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

/* Responsividade */
@media (max-width: 768px) {
  .cabeca {
    flex-direction: column;
    align-items: center;
  }

  .logo {
    margin-left: 0;
    margin-bottom: 0.5rem;
  }

  nav ul {
    flex-direction: column;
    gap: 0.5rem;
  }

  button {
    margin-top: 0.5rem;
    width: 100%;
    max-width: 15rem;
    text-align: center;
  }

  button.registrar,
  button.logout {
    margin-right: 0;
  }
}
</style>
