package br.com.ifpe.historygame.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

import br.com.ifpe.historygame.entity.Usuario;

@Getter
@Setter
@Data
@NoArgsConstructor
public class UsuarioDTO {
    private String id;
    private String nome;
    private String email;
    private Set<String> roles;
    private String bio;
    private String foto;
public UsuarioDTO(Usuario usuario) {
    this.id = usuario.getId();
    this.nome = usuario.getNome();
    this.email = usuario.getEmail();
    this.roles = usuario.getRoles() == null ? Set.of() :
        usuario.getRoles().stream()
            .map(role -> role.getNome()) // pegar o nome da role da entidade
            .collect(Collectors.toSet());
    this.bio = usuario.getBio();
    this.foto = usuario.getFoto();
}

}
