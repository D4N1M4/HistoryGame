package br.com.ifpe.historygame.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedefinirSenhaRequest {
    private String email;
    private String novaSenha;
}