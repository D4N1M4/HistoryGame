package br.com.ifpe.historygame.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.com.ifpe.historygame.entity.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByJogoIdOrderByDataCriacaoDesc(Long jogoId);

    @Query("SELECT AVG(c.estrelas) FROM Comentario c WHERE c.jogo.id = :jogoId")
    Double calcularMediaEstrelasPorJogo(@Param("jogoId") Long jogoId);

    List<Comentario> findTop3ByOrderByDataCriacaoDesc();

    Page<Comentario> findByJogoIdOrderByDataCriacaoDesc(Long jogoId, Pageable pageable);

}


