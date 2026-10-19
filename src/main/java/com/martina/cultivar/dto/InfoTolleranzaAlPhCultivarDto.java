package com.martina.cultivar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoTolleranzaAlPhCultivarDto {
    private String specie;
    private Long numeroCultivar;
    private String cultivarPiuTollerante;
    private Double ampiezzaMassima;
    private String cultivarMenoTollerante;
    private Double ampiezzaMinima;
}