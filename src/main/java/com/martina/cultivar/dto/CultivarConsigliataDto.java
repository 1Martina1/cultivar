package com.martina.cultivar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CultivarConsigliataDto {

    private String nome;
    private String specie;
    private Double phMin;
    private Double phMax;
    private Double margine;

}