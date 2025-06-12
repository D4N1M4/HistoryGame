package br.com.ifpe.historygame.dto;
import java.time.LocalDate;
import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JogoDTO {
    private Long id;
    private String nome;
    private String resumo;
    private String capa;
    private String modoJogo;
    private LocalDate dataLancamento;
    private List<GeneroDTO> generos;

    private Integer favoritos;
    private Integer jogados;
    private Integer desejados;
    
    public JogoDTO(Long id, String nome, String resumo, String capa, String modoJogo, LocalDate dataLancamento,
            List<GeneroDTO> generos, Integer favoritos, Integer jogados, Integer desejados) {
        this.id = id;
        this.nome = nome;
        this.resumo = resumo;
        this.capa = capa;
        this.modoJogo = modoJogo;
        this.dataLancamento = dataLancamento;
        this.generos = generos;
        this.favoritos = favoritos;
        this.jogados = jogados;
        this.desejados = desejados;
    }

    public JogoDTO() {
    }

}


