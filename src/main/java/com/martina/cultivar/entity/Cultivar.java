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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String specie;

    @Column
    private Double phMin;

    @Column
    private Double phMax;

    @Enumerated(EnumType.STRING)
    private FabbisognoIdrico fabbisognoIdrico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TIPO_TERRENO_ID", nullable = false)
    private TipoTerreno tipoTerreno;

}