package com.martina.cultivar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "CULTIVAR")
@NoArgsConstructor
@Entity
public class Cultivar {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,      // l'id lo prendo da una SEQUENCE...
            generator = "seqCultivar")               // ...quella definita qui sotto con questo nome
    @SequenceGenerator(name = "seqCultivar",                 // nome "interno", usato solo per collegarla al @GeneratedValue
            sequenceName = "SEQ_CULTIVAR",        // nome VERO della sequence nel database
            allocationSize = 1)                   // quanti numeri chiedo alla sequence ogni volta
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String specie;

    @Column(columnDefinition = "NUMBER(3,1)")
    private Double phMin;

    @Column(columnDefinition = "NUMBER(3,1)")
    private Double phMax;

    @Enumerated(EnumType.STRING)
    private FabbisognoIdrico fabbisognoIdrico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TIPO_TERRENO_ID", nullable = false)
    private TipoTerreno tipoTerreno;

}