package br.com.ifpe.historygame.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.historygame.dto.DesejadoJogoDTO;
import br.com.ifpe.historygame.service.DesejadoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/desejados")
@RequiredArgsConstructor
public class DesejadoController {

    private final DesejadoService desejadoService;

    @PostMapping("/{jogoId}")
    public ResponseEntity<Void> adicionarDesejado(@PathVariable String usuarioId, @PathVariable Long jogoId) {
        desejadoService.adicionarDesejado(usuarioId, jogoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{jogoId}")
    public ResponseEntity<Void> removerDesejado(@PathVariable String usuarioId, @PathVariable Long jogoId) {
        desejadoService.removerDesejado(usuarioId, jogoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<DesejadoJogoDTO>> listarDesejados(@PathVariable String usuarioId) {
        List<DesejadoJogoDTO> desejados = desejadoService.listarDesejados(usuarioId);
        return ResponseEntity.ok(desejados);
    }
}