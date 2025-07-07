package br.com.ifpe.historygame.service;

import br.com.ifpe.historygame.dto.AuthRequest;
import br.com.ifpe.historygame.entity.Role;
import br.com.ifpe.historygame.entity.Usuario;
import br.com.ifpe.historygame.repository.RoleRepository;
import br.com.ifpe.historygame.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registro de usuário comum
    public void registerUser(AuthRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já registrado.");
        }

        Usuario u = new Usuario();
        u.setEmail(request.getEmail());
        u.setSenha(passwordEncoder.encode(request.getSenha()));
        u.setNome(request.getNome());

        Role userRole = roleRepository.findByNome("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Role USER não encontrada"));

        u.setRoles(Set.of(userRole));
        usuarioRepository.save(u);
    }

    // Registro de administrador
    public void registerAdmin(AuthRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já registrado.");
        }

        Usuario u = new Usuario();
        u.setEmail(request.getEmail());
        u.setSenha(passwordEncoder.encode(request.getSenha()));
        u.setNome("Administrador");

        Role adminRole = roleRepository.findByNome("ROLE_ADMIN")
            .orElseThrow(() -> new RuntimeException("Role ADMIN não encontrada"));

        u.setRoles(Set.of(adminRole));
        usuarioRepository.save(u);
    }

    // Redefinir senha
    public void redefinirSenha(String email, String novaSenha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(senhaCriptografada);
        usuarioRepository.save(usuario);
    }

    // Buscar usuário por email (retorna Optional para facilitar controle)
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Salvar ou atualizar usuário
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario autenticar(String email, String senha) {
    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

    if (!passwordEncoder.matches(senha, usuario.getSenha())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }

    return usuario;
}
public Usuario atualizarPerfil(Usuario usuario) {
    return usuarioRepository.save(usuario);
}


}
