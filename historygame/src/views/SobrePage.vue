<template>

   <!-- Seção "Quem somos" -->
     <section class="SobrePage-container" > <h1>Quem somos</h1> 
        <p> Este site foi criado com carinho para pessoas que, assim como nós, compartilham uma paixão inabalável por videogames! 
            Nosso propósito é reunir jogadores de todas as idades, estilos e níveis de experiência em um espaço único, 
            onde a paixão pelos games transcende gerações. Aqui, acreditamos que cada jogador tem uma história para contar e que cada jogo
            é uma aventura esperando para ser compartilhada.
            Queremos conectar comunidades de gamers, promovendo a interação, a troca de experiências e o fortalecimento dos laços entre aqueles 
            que amam explorar mundos virtuais. Este é o lugar ideal para você registrar suas opiniões, compartilhar momentos inesquecíveis e 
            discutir as emoções que os games despertam. Além disso, nossa plataforma serve como um guia para ajudar outros jogadores em sua busca 
            por novos desafios, recomendando títulos que possam se transformar em suas próximas grandes aventuras.
            Nosso objetivo é proporcionar uma experiência completa e envolvente, onde você se sinta parte de um universo vibrante, dinâmico e acolhedor,
            repleto de histórias fascinantes, conexões significativas e, claro, muita diversão. Seja um iniciante curioso, um colecionador nostálgico 
            ou um veterano ávido por explorar novas fronteiras, aqui há espaço para todos!
            Convidamos você a mergulhar de cabeça neste mundo de possibilidades e a aproveitar ao máximo cada momento em nosso site. 
            Compartilhe, descubra, interaja e, acima de tudo, divirta-se! Afinal, o universo dos games é infinito, e estamos apenas começando essa jornada juntos. 
            Bem-vindo a um espaço feito para gamers por gamers!

 </p>
             </section>

</template>

<script>
    export default {
        name: 'SobrePage'
    }
</script>

<style scoped>

.SobrePage-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #e0eafc, #cfdef3);
  padding: 40px 20px;
}

.SobrePage-container h1 {
  color: #020021;
  font-size: 3rem;
  margin-bottom: 20px;
  text-align: center;
}

.SobrePage-container p {
  background: linear-gradient(90deg, #3e60f8, #001c82, #020021);
  color: #fefefe;
  padding: 30px;
  border-radius: 16px;
  max-width: 900px;
  line-height: 1.8;
  font-size: 1.1rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  text-align: justify;
}

footer {
  background-color: #020021;
  color: white;
  text-align: center;
  padding: 15px;
  font-size: 0.9rem;
}

/* Responsividade */
@media (max-width: 768px) {
  .SobrePage-container h1 {
    font-size: 2.2rem;
  }

  .SobrePage-container p {
    font-size: 1rem;
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .SobrePage-container h1 {
    font-size: 1.8rem;
  }

  .SobrePage-container p {
    font-size: 0.95rem;
    padding: 16px;
  }
}

</style>
