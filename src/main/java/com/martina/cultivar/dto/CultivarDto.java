package com.martina.cultivar.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CultivarDto {
    private String nome;
    private String specie;
    private Double phMin;
    private Double phMax;
    private FabbisognoIdrico fabbisognoIdrico;
    private String tipoTerreno;
}