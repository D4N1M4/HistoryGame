package br.com.ifpe.historygame.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.historygame.dto.JogadoJogoDTO;
import br.com.ifpe.historygame.service.JogadoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/jogados")
@RequiredArgsConstructor
public class JogadoController {

    private final JogadoService jogadoService;

    @PostMapping("/{jogoId}")
    public ResponseEntity<Void> adicionarJogado(@PathVariable String usuarioId, @PathVariable Long jogoId) {
        jogadoService.adicionarJogado(usuarioId, jogoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{jogoId}")
    public ResponseEntity<Void> removerJogado(@PathVariable String usuarioId, @PathVariable Long jogoId) {
        jogadoService.removerJogado(usuarioId, jogoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<JogadoJogoDTO>> listarJogados(@PathVariable String usuarioId) {
        List<JogadoJogoDTO> jogados = jogadoService.listarJogados(usuarioId);
        return ResponseEntity.ok(jogados);
    }
}

