package br.com.ifpe.historygame.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class JogoDTO {
    private Long id;
    private String nome;
    private String resumo;
    private String capa;
    private String modoJogo;
    private LocalDate dataLancamento;
    private List<GeneroDTO> generos; // Supondo que você tem um GeneroDTO

    // Campos de contagem existentes
    private Integer favoritados;
    private Integer jogados;
    private Integer desejados;

    // --- NOVO CAMPO ADICIONADO ---
    private Long numeroAcessos;
}