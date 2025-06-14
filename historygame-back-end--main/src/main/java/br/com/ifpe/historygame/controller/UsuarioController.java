package br.com.ifpe.historygame.controller;

import br.com.ifpe.historygame.dto.AuthRequest;
import br.com.ifpe.historygame.dto.RedefinirSenhaRequest;
import br.com.ifpe.historygame.dto.UsuarioDTO;
import br.com.ifpe.historygame.entity.Usuario;
import br.com.ifpe.historygame.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest request) {
        usuarioService.registerUser(request); // sempre cria como USER
        return ResponseEntity.ok("Usuário registrado com sucesso.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody AuthRequest request) {
        usuarioService.registerAdmin(request); // apenas ADMIN pode criar outro ADMIN
        return ResponseEntity.ok("Administrador registrado com sucesso.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha());

        try {
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession(true); // força criação da sessão

            // Retorna apenas dados básicos do usuário autenticado
            Usuario usuario = (Usuario) auth.getPrincipal();
            return ResponseEntity.ok(Map.of(
                "email", usuario.getEmail(),
                "nome", usuario.getNome(),
                "roles", usuario.getRoles()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }

@GetMapping("/me")
public ResponseEntity<UsuarioDTO> getUsuarioLogado(Authentication authentication) {
    // Se este método for alcançado, o usuário JÁ ESTÁ autenticado pelo Spring Security.
    // O trabalho de criar o usuário a partir do Google já foi feito pelo CustomOAuth2UserService.
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String email = authentication.getName();
    Usuario usuario = usuarioService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado no banco de dados."));

    return ResponseEntity.ok(new UsuarioDTO(usuario));
}

    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(@RequestBody RedefinirSenhaRequest request) {
        usuarioService.redefinirSenha(request.getEmail(), request.getNovaSenha());
        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }
}
