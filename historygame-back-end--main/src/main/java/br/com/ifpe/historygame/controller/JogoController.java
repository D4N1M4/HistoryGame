package br.com.ifpe.historygame.controller;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.historygame.dto.JogoDTO;
import br.com.ifpe.historygame.service.JogoService;

@RestController
@RequestMapping("/api/jogos")
@CrossOrigin(origins = "http://localhost:5174")
public class JogoController {

    @Autowired
    private JogoService service;

    @GetMapping
    public List<JogoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JogoDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
    @GetMapping("/mais-acessados")
    public ResponseEntity<List<JogoDTO>> getMaisAcessados(@RequestParam(defaultValue = "10") int limit) {
        List<JogoDTO> jogos = service.getMaisAcessados(limit);
        return ResponseEntity.ok(jogos);
    }
    @GetMapping("/buscar")
    public ResponseEntity<List<JogoDTO>> buscarPorNome(@RequestParam String busca) {
        return ResponseEntity.ok(service.buscarPorNome(busca));
    }

    @PostMapping
    public ResponseEntity<JogoDTO> criar(@RequestBody JogoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JogoDTO> atualizar(@PathVariable Long id, @RequestBody JogoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/genero/{nomeGenero}")
    public ResponseEntity<List<JogoDTO>> listarPorGenero(@PathVariable String nomeGenero) {
        return ResponseEntity.ok(service.listarPorGenero(nomeGenero));
    }

    @PatchMapping("/{id}/acesso")
    public ResponseEntity<JogoDTO> incrementarAcessos(@PathVariable Long id) {
        Optional<JogoDTO> jogoAtualizado = service.incrementarAcessos(id);
        if (jogoAtualizado.isPresent()) {
            return ResponseEntity.ok(jogoAtualizado.get());
        }
        return ResponseEntity.notFound().build();
    }
}

