package com.martina.cultivar.vivaio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scontrino {
    private String nomePianta;
    private Integer quantitaAcquistata;
    private double prezzoUnitario;
    private double totale;
    private Integer quantitaRimasta;
}
