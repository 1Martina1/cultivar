package com.martina.cultivar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedaRiassuntivaCultivarDto {
    private String specie;
    private Long numeroCultivar;
    private List<String> nomiCultivar;
    private Double phMaxPiuAlto;
    private String cultivarPhMinPiuBasso;
    private Map<String, Long> conteggioPerTerreno;
}