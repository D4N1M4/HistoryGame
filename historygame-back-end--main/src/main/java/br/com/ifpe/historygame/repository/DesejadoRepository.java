package br.com.ifpe.historygame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.historygame.entity.Desejado;

public interface DesejadoRepository extends JpaRepository<Desejado, Long> {
    List<Desejado> findByUsuarioId(String usuarioId);
    Optional<Desejado> findByUsuarioIdAndJogoId(String usuarioId, Long jogoId);
    int countByJogoId(Long jogoId);
}