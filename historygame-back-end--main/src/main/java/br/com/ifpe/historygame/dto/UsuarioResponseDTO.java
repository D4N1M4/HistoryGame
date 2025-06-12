package br.com.ifpe.historygame.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

import br.com.ifpe.historygame.entity.Usuario;

@Getter
@Setter
public class UsuarioResponseDTO {

    private String nome;
    private String email;
    private Set<String> roles;

    public UsuarioResponseDTO(Usuario usuario) {
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.roles = usuario.getRoles().stream()
            .map(role -> role.getNome())
            .collect(Collectors.toSet());
    }
}
