package com.martina.cultivar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecuperaCultivarPerTerrenoDto {
    private String nomeTerreno;
    private Double phMisurato;
    private LocalDate dataAnalisi;
    private List<CultivarDto> cultivarAdatte;
}