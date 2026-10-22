package com.martina.cultivar.vivaio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pianta {
    private Long id;
    private String nome;
    private String tipo;
    private Double prezzo;
    private int quantita;
}
