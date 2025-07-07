package br.com.ifpe.historygame.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.ifpe.historygame.dto.JogoDTO;
import br.com.ifpe.historygame.entity.Desejado;
import br.com.ifpe.historygame.entity.Favorito;
import br.com.ifpe.historygame.entity.Genero;
import br.com.ifpe.historygame.entity.Jogado;
import br.com.ifpe.historygame.entity.Jogo;
import br.com.ifpe.historygame.mapper.JogoMapper;
import br.com.ifpe.historygame.repository.DesejadoRepository;
import br.com.ifpe.historygame.repository.FavoritoRepository;
import br.com.ifpe.historygame.repository.GeneroRepository;
import br.com.ifpe.historygame.repository.JogadoRepository;
import br.com.ifpe.historygame.repository.JogoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class JogoService {

    private final GeneroRepository generoRepository;
    private final JogoMapper mapper;
    private final JogoRepository jogoRepository;

    private final DesejadoRepository desejadoRepository;
    private final JogadoRepository jogadoRepository;
    private final FavoritoRepository favoritoRepository;

    // Listar todos os jogos
    public List<JogoDTO> listar() {
        return jogoRepository.findAll()
                         .stream()
                         .map(mapper::toDTO)
                         .toList();
    }

    // Buscar um jogo por ID
public JogoDTO buscarPorId(Long id) {
    Jogo jogo = jogoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

    JogoDTO dto = mapper.toDTO(jogo);

    // Adicionando as contagens ao DTO
    dto.setFavoritos(favoritoRepository.countByJogoId(id));
    dto.setJogados(jogadoRepository.countByJogoId(id));
    dto.setDesejados(desejadoRepository.countByJogoId(id));

    return dto;
}
    @Transactional
    public Optional<JogoDTO> incrementarAcessos(Long id) {
        Optional<Jogo> jogoOptional = jogoRepository.findById(id);

        if (jogoOptional.isPresent()) {
            Jogo jogo = jogoOptional.get();
            // System.out.println("DEBUG: Jogo ID: " + jogo.getId() + " - Acessos ANTES: " + jogo.getNumeroAcessos());
            
            jogo.setNumeroAcessos(jogo.getNumeroAcessos() + 1);

            // System.out.println("DEBUG: Jogo ID: " + jogo.getId() + " - Acessos DEPOIS do incremento em memória: " + jogo.getNumeroAcessos());
            
            Jogo jogoAtualizado = jogoRepository.save(jogo);

            // System.out.println("DEBUG: Jogo ID: " + jogoAtualizado.getId() + " - Acessos DEPOIS do SAVE: " + jogoAtualizado.getNumeroAcessos());
            
            return Optional.of(mapper.toDTO(jogoAtualizado));
        }
        return Optional.empty();
    }
    public List<JogoDTO> getMaisAcessados(int limit) {
        // Define a ordenação: por 'numeroAcessos' em ordem decrescente
        Sort sort = Sort.by("numeroAcessos").descending();
        // Define a paginação/limite: página 0, com 'limit' resultados, e a ordenação
        Pageable pageable = PageRequest.of(0, limit, sort);

        // Usa o método findAll(Pageable) do JpaRepository (que já existe na interface)
        // para buscar os jogos com o limite e a ordenação desejados.
        return jogoRepository.findAll(pageable)
                             .stream()
                             .map(mapper::toDTO)
                             .toList();
    }

    public List<JogoDTO> buscarPorNome(String termoBusca) {
        List<Jogo> jogos = jogoRepository.findByNomeContainingIgnoreCase(termoBusca);
        return jogos.stream().map(mapper::toDTO).toList();
    }

public JogoDTO salvar(JogoDTO dto) {
    // Converte o DTO para entidade
    Jogo jogo = mapper.toEntity(dto);

    // Trata os gêneros fornecidos pelo usuário
    if (dto.getGeneros() != null && !dto.getGeneros().isEmpty()) {
        List<Genero> generos = dto.getGeneros().stream()
            .map(generoDto -> {
                // Verifica se o gênero já existe no banco (ignora maiúsculas/minúsculas)
                return generoRepository.findByNomeIgnoreCase(generoDto.getNome())
                    .orElseGet(() -> {
                        // Se não existir, cria e salva um novo
                        Genero novoGenero = new Genero();
                        novoGenero.setNome(generoDto.getNome());
                        return generoRepository.save(novoGenero);
                    });
            })
            .collect(Collectors.toList());

        jogo.setGeneros(generos);
    }

    // Salva o jogo com os gêneros associados
    Jogo salvo = jogoRepository.save(jogo);

    // Converte a entidade salva de volta para DTO
    return mapper.toDTO(salvo);
}


    // Deletar jogo por ID
    public void deletar(Long id) {
        jogoRepository.deleteById(id);
    }

public JogoDTO atualizar(Long id, JogoDTO dto) {
    Jogo jogo = jogoRepository.findById(id)
                          .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

    if (dto.getNome() != null) {
        jogo.setNome(dto.getNome());
    }

    if (dto.getResumo() != null) {
        jogo.setResumo(dto.getResumo());
    }

    if (dto.getCapa() != null) {
        jogo.setCapa(dto.getCapa());
    }

    if (dto.getModoJogo() != null) {
        jogo.setModoJogo(dto.getModoJogo());
    }

    if (dto.getDataLancamento() != null) {
        jogo.setDataLancamento(dto.getDataLancamento());
    }

    if (dto.getGeneros() != null) {
        if (!dto.getGeneros().isEmpty()) {
            List<Genero> generos = dto.getGeneros().stream()
                .map(generoDto -> generoRepository.findByNomeIgnoreCase(generoDto.getNome())
                    .orElseGet(() -> {
                        Genero novoGenero = new Genero();
                        novoGenero.setNome(generoDto.getNome());
                        return generoRepository.save(novoGenero);
                    }))
                .collect(Collectors.toList());
            jogo.setGeneros(generos);
        } else {
            // Se enviar lista vazia, limpa os gêneros
            jogo.setGeneros(new ArrayList<>());
        }
    }
    // Se dto.getGeneros() for null, mantém os gêneros atuais

    Jogo atualizado = jogoRepository.save(jogo);
    return mapper.toDTO(atualizado);
}


    public List<JogoDTO> listarPorGenero(String nomeGenero) {
        List<Jogo> jogos = jogoRepository.findByGenerosNomeIgnoreCase(nomeGenero);
        return jogos.stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<JogoDTO> listarFavoritosPorUsuario(String usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId).stream()
            .map(Favorito::getJogo)
            .map(mapper::toDTO)
            .toList();
    }

    public List<JogoDTO> listarJogadosPorUsuario(String usuarioId) {
        return jogadoRepository.findByUsuarioId(usuarioId).stream()
            .map(Jogado::getJogo)
            .map(mapper::toDTO)
            .toList();
    }

    public List<JogoDTO> listarDesejadosPorUsuario(String usuarioId) {
        return desejadoRepository.findByUsuarioId(usuarioId).stream()
            .map(Desejado::getJogo)
            .map(mapper::toDTO)
            .toList();
    }
}