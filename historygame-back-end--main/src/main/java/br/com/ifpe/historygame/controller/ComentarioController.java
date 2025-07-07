package br.com.ifpe.historygame.controller;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.historygame.dto.ComentarioDTO;
import br.com.ifpe.historygame.dto.ComentarioRequestDTO;
import br.com.ifpe.historygame.service.ComentarioService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

@PostMapping("/jogos/{jogoId}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<Void> adicionarComentario(
        @PathVariable Long jogoId,
        @RequestBody ComentarioRequestDTO dto,
        Authentication authentication) {

    String usuarioEmail = null;

    if (authentication.getPrincipal() instanceof UserDetails userDetails) {
        usuarioEmail = userDetails.getUsername(); // login por sessão (email/senha)
    } else if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
        usuarioEmail = oauth2User.getAttribute("email"); // login pelo Google
    }

    if (usuarioEmail == null) {
        return ResponseEntity.status(401).build(); // falha de autenticação
    }

    comentarioService.adicionarComentario(usuarioEmail, jogoId, dto.getTexto(), dto.getEstrelas());
    return ResponseEntity.ok().build();
}



    @GetMapping("/jogos/{jogoId}")
    public ResponseEntity<List<ComentarioDTO>> listarComentarios(@PathVariable Long jogoId) {
        return ResponseEntity.ok(comentarioService.listarComentarios(jogoId));
    }

@GetMapping("/ultimos-gerais")
public ResponseEntity<List<ComentarioDTO>> listarUltimosComentariosGerais() {
    return ResponseEntity.ok(comentarioService.listarUltimos3ComentariosGerais());
}


    @DeleteMapping("/{comentarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long comentarioId) {
        comentarioService.deletarComentario(comentarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/media-estrelas/{jogoId}")
    public ResponseEntity<Double> mediaEstrelas(@PathVariable Long jogoId) {
        return ResponseEntity.ok(comentarioService.calcularMediaEstrelas(jogoId));
    }

    @GetMapping("/jogos/{jogoId}/paginado")
public ResponseEntity<Page<ComentarioDTO>> listarComentariosPaginado(
        @PathVariable Long jogoId,
        @PageableDefault(size = 5, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {
    
    return ResponseEntity.ok(comentarioService.listarComentariosPaginado(jogoId, pageable));
}

}
