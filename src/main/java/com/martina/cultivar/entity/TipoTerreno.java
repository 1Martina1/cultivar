package com.martina.cultivar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "TIPO_TERRENO")
@NoArgsConstructor
public class TipoTerreno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column
    private String descrizione;

    @OneToMany(mappedBy = "tipoTerreno")   // "un terreno ha molte cultivar": la relazione inversa di @ManyToOne in Cultivar
    private List<Cultivar> cultivars;       // mappedBy = il nome del campo, in Cultivar, che punta al terreno
}