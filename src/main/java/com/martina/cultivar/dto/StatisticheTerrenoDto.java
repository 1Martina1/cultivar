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
public class StatisticheTerrenoDto {

    private Long numeroCultivar;
    private List<String> specie;
    private Double phMin;
    private Double phMax;
    private Map<FabbisognoIdrico, List<String>> perFabbisogno;
}