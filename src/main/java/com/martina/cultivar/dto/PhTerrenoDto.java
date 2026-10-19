package com.martina.cultivar.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhTerrenoDto {
    private String nomeTerreno;
    private Double phMinPiuBasso;
    private String cultivarPhMinPiuBasso;
    private Double phMaxPiuAlto;
    private String cultivarPhMaxPiuAlto;
}