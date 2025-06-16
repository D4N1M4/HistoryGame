<template>
  <div class="profile-container">
    <div class="profile-header">
      <img :src="user?.foto || defaultAvatar" alt="Foto de Perfil" class="profile-picture" />
      <input type="text" v-model="username" @input="checkChanges" class="edit-input username" />
      <textarea v-model="bio" @input="checkChanges" class="edit-input bio"></textarea>
    </div>

    <div class="profile-details">
      <h2>Detalhes do Usuário</h2>
      <p>
        <strong>Nome:</strong>
        <input type="text" v-model="username" @input="checkChanges" class="edit-input" />
      </p>
      <p>
        <strong>Email:</strong>
        <input type="email" v-model="email" disabled class="edit-input" />
      </p>
    </div>

    <button @click="saveProfile" :disabled="!isChanged" class="save-button">Salvar</button>
    <p v-if="saved" class="confirmation-message">Perfil salvo com sucesso!</p>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useAuthStore } from "@/stores/authStore";
import { useRouter } from "vue-router";

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    const nome = ref("");
    const email = ref("");
    const user = ref({});
    const saved = ref(false);
    const isChanged = ref(false);

    const defaultAvatar = require('@/assets/default_avatar.jpg');

    const loadUserProfile = async () => {
      try {
        await authStore.verificarAuth(); // Verifica se está autenticado
        const dados = authStore.usuario;

        if (!dados) {
          router.push("/login"); // Redireciona se não estiver logado
          return;
        }

        nome.value = dados.nome || "Usuário";
        email.value = dados.email || "E-mail não informado";
        user.value = dados;

      } catch (error) {
        console.error("Erro ao carregar perfil:", error);
        router.push("/login");
      }
    };

    const checkChanges = () => {
      isChanged.value = true;
    };

    const saveProfile = async () => {
      try {
        await authStore.atualizarPerfil({
          nome: username.value,
        });

        saved.value = true;
        isChanged.value = false;

        setTimeout(() => {
          saved.value = false;
        }, 2000);
      } catch (error) {
        console.error("Erro ao salvar perfil:", error);
      }
    };

    onMounted(() => {
      loadUserProfile();
    });

    return {
      nome,
      email,
      user,
      saved,
      isChanged,
      checkChanges,
      saveProfile,
      defaultAvatar,
    };
  },
};
</script>



<style scoped>
    .profile-container {
    font-family: Arial, sans-serif;
    color: #000;
    background-color: #fff;
    padding: 20px;
    max-width: 600px;
    margin: 0 auto;
    border: 1px solid #000;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    }

    .profile-header {
    text-align: center;
    }

    .profile-picture {
    width: 150px;
    height: 150px;
    border-radius: 50%;
    border: 2px solid #000;
    }

    .edit-input {
    width: calc(100% - 40px);
    padding: 10px;
    margin: 10px 20px;
    border: 1px solid #000;
    border-radius: 5px;
    background-color: #fff;
    font-size: 16px;
    }

    .username {
    font-size: 24px;
    text-align: center;
    }

    .bio {
    font-size: 14px;
    color: #555;
    text-align: center;
    }

    .profile-details {
    margin-top: 20px;
    }

    .profile-details h2 {
    font-size: 20px;
    margin-bottom: 10px;
    border-bottom: 1px solid #000;
    }

    .profile-details p {
    font-size: 16px;
    margin: 5px 0;
    }

    .save-button {
    display: block;
    margin: 20px auto;
    padding: 10px 20px;
    font-size: 16px;
    color: #fff;
    background-color: #000;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    }

    .save-button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
    }

    .confirmation-message {
    text-align: center;
    font-size: 14px;
    color: green;
    }
</style>
