import { startAfter,limit,addDoc, orderBy,serverTimestamp,getDocs, collection, query, where, setDoc, getDoc, doc, arrayUnion, updateDoc, arrayRemove, increment } from 'firebase/firestore';
import { db } from './firebaseConfig';

let lastVisible = null;
let PAGE_SIZE_REVIEW = 3;

class DAOService {
async getAll(ordenacao) {
    try {
      const gamesCollectionRef = collection(db, 'games2');
      const q = query(gamesCollectionRef, orderBy(ordenacao,'desc'));
      const gamesSnapshot = await getDocs(q);

      const documents = gamesSnapshot.docs.map(doc => {
        const data = doc.data();
        return {
          id: doc.id,
          name: data.name,
          summary: data.summary,
          coverUrl: data.cover,
          slug: data.slug,
          cliques: data.cliques
        };
      });
      return documents;
    } catch (error) {
      console.error('Error getting documents: ', error);
      throw new Error('Error getting documents');
    }
  }

  async getByName(name) {
    try {
      const gamesRef = collection(db, 'games2');
  
      // Transforma a busca para minúsculo
      const nameLower = name.toLowerCase();
  
      // Filtra pelo campo 'name_lowercase'
      const q = query(
        gamesRef,
        where('name_lowercase', '>=', nameLower),
        where('name_lowercase', '<=', nameLower + '\uf8ff')
      );
  
      const gamesSnapshot = await getDocs(q);
  
      const documents = gamesSnapshot.docs.map(doc => {
        const data = doc.data();
        return {
          id: doc.id,
          name: data.name,
          summary: data.summary,
          coverUrl: data.cover,
          slug: data.slug
        };
      });
      return documents;
    } catch (error) {
      console.error('Error getting documents: ', error);
      throw new Error('Error getting documents');
    }
  } 

  async getById(id) {
    await this.atualizarContador(id); 
    try {
      const gameDoc = await getDoc(doc(db, 'games2', id));
      if (gameDoc.exists()) {
        return { id: gameDoc.id, ...gameDoc.data() };
      } else {
        throw new Error('Game not found');
      }
    } catch (error) {
      console.error('Error getting game:', error);
      throw new Error('Error getting game');
    } 
    
  }
     atualizarContador = async (gameId ) => {
    const itemRef = doc(db, 'games2', gameId); // Substitua 'items' pela coleção desejada
    const field = 'cliques';
    const incrementValue = 1;
    try {
      // Verifica se o documento existe no Firestore
      const itemDoc = await getDoc(itemRef);
  
      if (itemDoc.exists()) {
        // Inicializa o campo com 0 caso ele ainda não exista no documento
        if (!(field in itemDoc.data())) {
          await updateDoc(itemRef, { [field]: 0 });
        }
  
        // Incrementa ou decrementa o valor do campo
        await updateDoc(itemRef, {
          [field]: increment(incrementValue),
        });
        console.log(`Valor do campo '${field}' atualizado com sucesso! Incremento: ${incrementValue}.`);
      } else {
        // Cria o documento caso ele não exista, com o campo inicializado
        await setDoc(itemRef, { [field]: incrementValue });
        console.log(`Documento criado com sucesso! Campo '${field}' inicializado com o valor: ${incrementValue}.`);
      }
    } catch (error) {
      console.error(`Erro ao atualizar o contador no Firestore: ${error}`);
    }
  };
  

//Função que serpara os jogos por categoria
async getGamesByGenre() {
  try {
    const gamesCollectionRef = collection(db, 'games2');
    const gamesSnapshot = await getDocs(gamesCollectionRef);

    const genreMap = {};

    gamesSnapshot.forEach(doc => {
      const data = doc.data();
      const game = {
        id: doc.id,
        name: data.name,
        summary: data.summary,
        coverUrl: data.cover,
        slug: data.slug
      };

      // Se o jogo tiver o campo genres
      if (Array.isArray(data.genres)) {
        data.genres.forEach(genre => {
          // Se ainda não existe a categoria, cria o array
          if (!genreMap[genre]) {
            genreMap[genre] = [];
          }
          // Adiciona o jogo no array da categoria
          genreMap[genre].push(game);
        });
      } else {
        // Se o jogo não tiver genres definido, adiciona em "Outros"
        if (!genreMap["Outros"]) {
          genreMap["Outros"] = [];
        }
        genreMap["Outros"].push(game);
      }
    });

    return genreMap;

  } catch (error) {
    console.error("Erro ao buscar jogos por gênero:", error);
    throw new Error("Erro ao buscar jogos por gênero");
  }
}

  async isPlayDesFav(user, game,field){
    const userDocRef = doc(db, "userGames", user);
    try {
      const userDoc = await getDoc(userDocRef);
      if (userDoc.exists()){
        const userData = userDoc.data()[field] || [];
if (userData.includes(game)) {
  console.log("Está em favorito");
  return true;
} else {
  console.log(`Não está em ${field}`);
  return false;
}
      }else{
        console.log("Documento não encontrado");
        return !false;
      }
    }catch (error){
      console.error("Erro ao verificar GameId em banco", error);
      return !false;
    }
  }
  async saveReview(review) {
    try {
      const docRef = await addDoc(collection(db, "reviews"), {
        userID: review.userID,
        userName: review.userName,
        userPhotoURL: review.userPhotoUrl,
        gameID: review.gameID,
        stars: review.stars,
        title: review.title,
        comment: review.comment,
        slug: review.slug,
        timestamp: serverTimestamp()
      });
      console.log("Avaliação enviada com ID: ", docRef.id);
    }catch (error){
      console.error("Erro ao enviar review", error);
    }
  }

  async getStars(gameId){
    try{ 
      const reviewRef = collection(db,'reviews');
      const q = query(reviewRef, where('gameID', '==', gameId));
      const querySnapshot = await getDocs(q);

      let totalRating = 0;
      let reviewCount = 0;

      querySnapshot.forEach((doc) => {
        totalRating += doc.data().stars;
        reviewCount++;
      });

      const averageRating = reviewCount > 0 ? totalRating / reviewCount :0;
      return averageRating;


    } catch (error) {
      console.error('Erro ao calcular a médias das avaliações', error);
      return 0;
    }

  }

  async loadFirstPageReviews(gameId) {
    try {
      const commentsCollectionRef = collection(db, "reviews");
      const q = query(
        commentsCollectionRef,
        where("gameID", "==", gameId),
        orderBy("timestamp",'desc'),
        limit(PAGE_SIZE_REVIEW)
      );

      const querySnapshot = await getDocs(q);
      if (!querySnapshot.empty) {
        lastVisible = querySnapshot.docs[querySnapshot.docs.length - 1]; // Define o último item
      }

      return querySnapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      }));
    } catch (error) {
      console.error("Erro ao carregar reviews:", error);
      throw new Error("Erro ao carregar reviews");
    }
  }

  async loadNextPageReviews(gameId) {
    if (!lastVisible) {
      console.warn("Nenhum comentário restante para carregar.");
      return [];
    }

    try {
      const commentsCollectionRef = collection(db, "reviews");
      const q = query(
        commentsCollectionRef,
        where("gameID", "==", gameId),
        orderBy("timestamp",'desc'),
        startAfter(lastVisible), // Pega a partir do último carregado
        limit(PAGE_SIZE_REVIEW)
      );

      const querySnapshot = await getDocs(q);
      if (!querySnapshot.empty) {
        lastVisible = querySnapshot.docs[querySnapshot.docs.length - 1]; // Atualiza o último item visível
      } else {
        lastVisible = null; // Se não há mais resultados, impede novas buscas
      }

      return querySnapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      }));
    } catch (error) {
      console.error("Erro ao carregar mais reviews:", error);
      return [];
    }
  }

  async loadLastestReviews() {
    try {
      const commentsCollectionRef = collection(db, "reviews");
      const q = query(
        commentsCollectionRef,
        orderBy("timestamp",'desc'),
        limit(4)
      );

      const querySnapshot = await getDocs(q);
      if (!querySnapshot.empty) {
        lastVisible = querySnapshot.docs[querySnapshot.docs.length - 1]; // Define o último item
      }

      return querySnapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      }));
    } catch (error) {
      console.error("Erro ao carregar reviews:", error);
      throw new Error("Erro ao carregar reviews");
    }
  }
  async getMaisAcess() {
    try {
      const gamesCollectionRef = collection(db, 'games2');
      const q = query(gamesCollectionRef, orderBy('cliques','desc'),limit(3));
      const gamesSnapshot = await getDocs(q);

      const documents = gamesSnapshot.docs.map(doc => {
        const data = doc.data();
        return {
          id: doc.id,
          name: data.name,
          summary: data.summary,
          coverUrl: data.cover,
          slug: data.slug,
          cliques: data.cliques
        };
      });
      return documents;
    } catch (error) {
      console.error('Error getting documents: ', error);
      throw new Error('Error getting documents');
    }
  }


}

export default DAOService;
