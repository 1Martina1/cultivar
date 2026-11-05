package com.martina.cultivar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginaCultivarDto {
    private List<CultivarDettaglioDto> conteuto;
    private int numeroPagina;
    private int dimensionePagina;
    private Long totaleElementi;
    private int totalePagine;
}
