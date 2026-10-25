package com.martina.cultivar.vivaio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DaRiordinare {
    private String nome;
    private String categoria;
    private Integer quantitaAttuale;
    private Integer quantitaDaOrdinare;
}
