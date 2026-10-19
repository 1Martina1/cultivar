package com.martina.cultivar.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModificaParzialeCultivarDto {
    @Size(max = 100)
    private String nome;

    private String specie;

    @DecimalMin("0")
    @DecimalMax("14")
    private Double phMin;

    private Double phMax;

    private FabbisognoIdrico fabbisognoIdrico;

    private String tipoTerreno;
}