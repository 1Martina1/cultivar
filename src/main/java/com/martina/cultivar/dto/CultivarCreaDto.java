package com.martina.cultivar.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CultivarCreaDto {
    @NotBlank
    private String nome;

    @NotBlank
    private String specie;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("14.0")
    private Double phMin;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("14.0")
    private Double phMax;

    @NotNull
    private FabbisognoIdrico fabbisognoIdrico;

    @NotBlank
    private String nomeTerreno;
}
