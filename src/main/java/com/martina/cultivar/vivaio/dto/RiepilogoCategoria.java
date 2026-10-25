package com.martina.cultivar.vivaio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiepilogoCategoria {
    private String categoria;
    private long numeroProdotti;
    private Double prezzoMedio;
    private Double prezzoMinimo;
    private String prodottoPiuEconomico;
}
