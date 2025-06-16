package br.com.ifpe.historygame.dto;

import java.time.LocalDate;

import br.com.ifpe.historygame.entity.Jogo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DesejadoJogoDTO {
    private Long id;
    private String nome;
    private String resumo;
    private String capa;
    private String modoJogo;
    private LocalDate dataLancamento;
    private Long numeroAcessos;

    public DesejadoJogoDTO(Jogo jogo, Long numeroAcessos) {
        this.id = jogo.getId();
        this.nome = jogo.getNome();
        this.resumo = jogo.getResumo();
        this.capa = jogo.getCapa();
        this.modoJogo = jogo.getModoJogo();
        this.dataLancamento = jogo.getDataLancamento();
        this.numeroAcessos = numeroAcessos;
    }
}
