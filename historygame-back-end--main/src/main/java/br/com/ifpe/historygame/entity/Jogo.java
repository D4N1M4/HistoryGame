package br.com.ifpe.historygame.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="jogo")
@Getter
@Setter
public class Jogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    private Long id;

    @Column(name="nome", nullable=false)
    private String nome;

    @Column(name="resumo", nullable=false)
    private String resumo;

    @Column(name="capa", nullable=false)
    private String capa;

    @Column(name="modoJogo", nullable = false) // Certifique-se que esta linha está correta
    private String modoJogo;

    @Column(name="dataLancamento", nullable=false)
    private LocalDate dataLancamento;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "jogo_genero",
        joinColumns = @JoinColumn(name = "jogo_id"),
        inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private List<Genero> generos;


    @Column(name = "numero_acessos", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long numeroAcessos = 0L; // Inicializa com 0 para novos jogos
}
