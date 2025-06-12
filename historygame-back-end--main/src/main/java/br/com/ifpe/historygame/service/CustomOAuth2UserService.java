package br.com.ifpe.historygame.service;

import br.com.ifpe.historygame.entity.Role;
import br.com.ifpe.historygame.entity.Usuario;
import br.com.ifpe.historygame.repository.RoleRepository;
import br.com.ifpe.historygame.repository.UsuarioRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    public CustomOAuth2UserService(UsuarioRepository usuarioRepository, RoleRepository roleRepository) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("O provedor OAuth2 não retornou um e-mail.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setEmail(email);
            novoUsuario.setNome(nome != null ? nome : "Usuário Google");

            Role userRole = roleRepository.findByNome("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER não encontrada"));

            novoUsuario.setRoles(Set.of(userRole));
            return usuarioRepository.save(novoUsuario);
        });

        return new DefaultOAuth2User(
            usuario.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNome()))
                .collect(Collectors.toSet()),
            oAuth2User.getAttributes(),
            "email"
        );
    }
}
