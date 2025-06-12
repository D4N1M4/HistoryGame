package br.com.ifpe.historygame.dto;

import br.com.ifpe.historygame.entity.Genero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneroDTO {

    private Long id;
    private String nome;
    
    public GeneroDTO() {}

    public GeneroDTO(Genero genero) {
        this.id = genero.getId();
        this.nome = genero.getNome();
    }
}

