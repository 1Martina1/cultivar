package com.martina.cultivar.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiepilogoSpecieDto {
    private String specie;
    private Long numeroCultivar;
    private List<String> nomiCultivar;
    private List<String> terreni;
    private Double phComuneMin;
    private Double phComuneMax;
    private Map<FabbisognoIdrico, Long> conteggioPerFabbisogno;

}