package br.com.ifpe.historygame.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ifpe.historygame.dto.JogadoJogoDTO;
import br.com.ifpe.historygame.entity.Jogado;
import br.com.ifpe.historygame.entity.Jogo;
import br.com.ifpe.historygame.entity.Usuario;
import br.com.ifpe.historygame.repository.JogadoRepository;
import br.com.ifpe.historygame.repository.JogoRepository;
import br.com.ifpe.historygame.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JogadoService {

    private final JogadoRepository jogadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;

    public void adicionarJogado(String usuarioId, Long jogoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Jogo jogo = jogoRepository.findById(jogoId)
            .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        if (jogadoRepository.findByUsuarioIdAndJogoId(usuarioId, jogoId).isEmpty()) {
            jogadoRepository.save(new Jogado(usuario, jogo));
        }
    }

    public void removerJogado(String usuarioId, Long jogoId) {
        jogadoRepository.findByUsuarioIdAndJogoId(usuarioId, jogoId)
            .ifPresent(jogadoRepository::delete);
    }

    public List<JogadoJogoDTO> listarJogados(String usuarioId) {
        return jogadoRepository.findByUsuarioId(usuarioId).stream()
            .map(jog -> new JogadoJogoDTO(jog.getJogo()))
            .toList();
    }
}
