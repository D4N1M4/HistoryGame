<template>
  <div class="profile-container">
    <div class="profile-header">
      <img :src="foto || defaultAvatar" alt="Foto de Perfil" class="profile-picture" />

      <div class="avatar-gallery">
        <h3>Escolha seu avatar:</h3>
        <div class="avatar-options">
          <img
            v-for="avatar in avatares"
            :key="avatar"
            :src="avatar"
            @click="selecionarAvatar(avatar)"
            :class="{ selected: avatar === foto }"
            class="avatar-item"
          />
        </div>
      </div>
    </div>
<div class="bio-section">
  <h3>Bio:</h3>
  <textarea
    v-model="bio"
    class="edit-input bio-input"
    @input="checkChanges"
    placeholder="Escreva algo sobre você..."
  ></textarea>
</div>

    <div class="profile-details">
      <h2>Detalhes do Usuário</h2>
      <p>
        <strong>Nome:</strong>
        <input type="text" v-model="nome" @input="checkChanges" class="edit-input" />
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
import defaultAvatar from '@/assets/default_avatar.jpg';

export default {
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();

    const nome = ref("");
    const bio = ref("");
    const email = ref("");
    const foto = ref("");
    const saved = ref(false);
    const isChanged = ref(false);

const avatares = [
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem1.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem2.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem3.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem4.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem5.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem6.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem7.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem8.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem9.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem10.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem11.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem12.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem13.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem14.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem15.png",
  "https://raw.githubusercontent.com/D4N1M4/HistoryGame-Imagens/main/imagem16.png"
];


    const loadUserProfile = async () => {
      try {
        await authStore.verificarAuth();
        const dados = authStore.usuario;

        if (!dados) {
          router.push("/login");
          return;
        }

        nome.value = dados.nome || "Usuário";
        email.value = dados.email || "E-mail não informado";
        foto.value = dados.foto || defaultAvatar;
        bio.value = dados.bio || '';
      } catch (error) {
        console.error("Erro ao carregar perfil:", error);
        router.push("/login");
      }
    };

    const checkChanges = () => {
      isChanged.value = true;
    };

    const selecionarAvatar = (url) => {
      foto.value = url;
      checkChanges();
    };

    const saveProfile = async () => {
      try {
        await authStore.atualizarPerfil({
          nome: nome.value,
          foto: foto.value || defaultAvatar,
          bio: bio.value || "",
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

    onMounted(loadUserProfile);

    return {
      nome,
      email,
      bio,
      foto,
      saved,
      isChanged,
      avatares,
      defaultAvatar,
      checkChanges,
      selecionarAvatar,
      saveProfile,
    };
  },
};
</script>

<style scoped>
.profile-container {
  max-width: 480px;
  margin: 2rem auto;
  padding: 1.5rem 2rem;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(8, 68, 219, 0.226);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 1.8rem;
}

.profile-picture {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #536bc1;
  box-shadow: 0 4px 8px rgba(3, 51, 241, 0.3);
  margin-bottom: 1rem;
  transition: transform 0.3s ease;
}
.profile-picture:hover {
  transform: scale(1.05);
}

.avatar-gallery {
  width: 100%;
  margin-top: 1rem;
  text-align: center;
}

.avatar-gallery h3 {
  font-weight: 600;
  margin-bottom: 0.8rem;
  color: #3e3bed;
}

.avatar-options {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.avatar-item {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  border: 2.5px solid transparent;
  cursor: pointer;
  object-fit: cover;
  transition: border-color 0.25s ease, transform 0.25s ease;
}

.avatar-item:hover {
  border-color: #8194c7;
  transform: scale(1.1);
}

.avatar-item.selected {
  border-color: #385f8e;
  box-shadow: 0 0 8px #6677bb;
}

.bio-section {
  margin: 1.8rem 0;
  width: 100%;
}

.bio-section h3 {
  font-weight: 600;
  margin-bottom: 0.6rem;
  color: #394362;
}

.bio-input {
  width: 100%;
  min-height: 90px;
  resize: vertical;
  padding: 0.7rem 0.9rem;
  font-size: 1rem;
  border: 2px solid #dddddd;
  border-radius: 10px;
  transition: border-color 0.25s ease;
  font-family: inherit;
  color: #333;
}

.bio-input:focus {
  border-color: #4c66af;
  outline: none;
  box-shadow: 0 0 5px rgba(17, 22, 50, 0.5);
}

.profile-details {
  margin-bottom: 1.5rem;
}

.profile-details h2 {
  margin-bottom: 1rem;
  font-weight: 700;
  color: #2e3e7d;
  border-bottom: 2px solid #4c65af;
  padding-bottom: 4px;
}

.profile-details p {
  margin-bottom: 1rem;
  font-size: 1rem;
}

.edit-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  font-size: 1rem;
  border: 2px solid #ddd;
  border-radius: 8px;
  transition: border-color 0.25s ease;
  font-family: inherit;
  color: #333;
}

.edit-input:focus {
  border-color: #4c5caf;
  outline: none;
  box-shadow: 0 0 5px rgba(76, 99, 175, 0.5);
}

.save-button {
  display: block;
  width: 100%;
  padding: 0.75rem 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(90deg, #748cf7,#1948f4, #03109d);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  user-select: none;
  box-shadow: 0 5px 12px rgba(76, 97, 175, 0.4);
}

.save-button:disabled {
  background-color: #a5b2d6;
  cursor: not-allowed;
  box-shadow: none;
}

.save-button:not(:disabled):hover {
  background-color: #384d8e;
}

.confirmation-message {
  margin-top: 0.9rem;
  font-weight: 600;
  color: #384b8e;
  text-align: center;
  user-select: none;
  animation: fadeInOut 2s ease forwards;
}

@keyframes fadeInOut {
  0%, 100% {opacity: 0;}
  10%, 90% {opacity: 1;}
}

/* Responsividade */
@media (max-width: 500px) {
  .profile-container {
    margin: 1rem 1rem 2rem;
    padding: 1rem 1rem;
  }

  .avatar-item {
    width: 56px;
    height: 56px;
  }

  .profile-picture {
    width: 110px;
    height: 110px;
  }
}
</style>
