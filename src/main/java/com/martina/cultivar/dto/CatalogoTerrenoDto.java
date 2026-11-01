package com.martina.cultivar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogoTerrenoDto {
    private String nomeTerreno;
    private String descrizione;
    private Long numeroCultivar;
    private List<CultivarCatalogoDto> catalogoDtos;
}
