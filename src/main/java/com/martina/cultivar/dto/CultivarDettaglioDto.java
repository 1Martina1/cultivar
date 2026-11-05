package com.martina.cultivar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CultivarDettaglioDto {
    private Long id;
    private String nome;
    private String specie;
    private String nomeTerreno;
    private String intervalloPh;
    private String fabbisognoIdrico;
}
