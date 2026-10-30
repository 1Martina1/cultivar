package com.martina.cultivar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiepilogoTerrenoDto {

    private String nomeTerreno;
    private Long numeroCultivar;
    private Double phMaxMedio;
    private List<String> nomiCultivar;

}