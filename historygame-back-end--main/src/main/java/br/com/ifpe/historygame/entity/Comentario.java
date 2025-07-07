package br.com.ifpe.historygame.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Jogo jogo;

    private String texto;

    private int estrelas;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    public Comentario() {} // construtor padrão obrigatório

    public Comentario(Usuario usuario, Jogo jogo, String texto, int estrelas) {
        this.usuario = usuario;
        this.jogo = jogo;
        this.texto = texto;
        this.estrelas = estrelas;
        this.dataCriacao = LocalDateTime.now();
    }
}



