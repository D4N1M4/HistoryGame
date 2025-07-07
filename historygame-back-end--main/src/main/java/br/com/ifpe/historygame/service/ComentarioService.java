package br.com.ifpe.historygame.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ifpe.historygame.dto.ComentarioDTO;
import br.com.ifpe.historygame.entity.Comentario;
import br.com.ifpe.historygame.entity.Jogo;
import br.com.ifpe.historygame.entity.Usuario;
import br.com.ifpe.historygame.repository.ComentarioRepository;
import br.com.ifpe.historygame.repository.JogoRepository;
import br.com.ifpe.historygame.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;
    
public void adicionarComentario(String usuarioId, Long jogoId, String texto, int estrelas) {
    if (estrelas < 1 || estrelas > 5) {
        throw new IllegalArgumentException("A avaliação deve estar entre 1 e 5 estrelas.");
    }

    Usuario usuario = usuarioRepository.findByEmail(usuarioId)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    Jogo jogo = jogoRepository.findById(jogoId)
        .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

    Comentario comentario = new Comentario(usuario, jogo, texto, estrelas);
    comentarioRepository.save(comentario);
}

    public List<ComentarioDTO> listarComentarios(Long jogoId) {
        return comentarioRepository.findByJogoIdOrderByDataCriacaoDesc(jogoId).stream()
            .map(c -> new ComentarioDTO(
                c.getId(),
                c.getUsuario().getNome(),   
                c.getUsuario().getEmail(),
                c.getTexto(),
                c.getDataCriacao().toString(),
                c.getEstrelas()
            ))
            .toList();
    }

public List<ComentarioDTO> listarUltimos3ComentariosGerais() {
    return comentarioRepository.findTop3ByOrderByDataCriacaoDesc().stream()
        .map(c -> new ComentarioDTO(
            c.getId(),
            c.getUsuario().getNome(),
            c.getUsuario().getEmail(),
            c.getTexto(),
            c.getDataCriacao().toString(),
            c.getEstrelas()
        ))
        .toList();
}


    public void deletarComentario(Long id) {
        comentarioRepository.deleteById(id);
    }

    public double calcularMediaEstrelas(Long jogoId) {
        Double media = comentarioRepository.calcularMediaEstrelasPorJogo(jogoId);
        return media != null ? media : 0.0;
    }
public Page<ComentarioDTO> listarComentariosPaginado(Long jogoId, Pageable pageable) {
    return comentarioRepository.findByJogoIdOrderByDataCriacaoDesc(jogoId, pageable)
        .map(c -> new ComentarioDTO(
            c.getId(),
                            c.getUsuario().getNome(),   
            c.getUsuario().getEmail(),
            c.getTexto(),
            c.getDataCriacao().toString(),
            c.getEstrelas()
        ));
}

}
