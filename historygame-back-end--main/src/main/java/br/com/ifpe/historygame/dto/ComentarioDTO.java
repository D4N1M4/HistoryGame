package br.com.ifpe.historygame.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String texto;
    private String dataCriacao;
    private int estrelas; // ⭐ novo campo
    
}

