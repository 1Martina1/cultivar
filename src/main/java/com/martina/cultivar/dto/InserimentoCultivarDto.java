package com.martina.cultivar.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InserimentoCultivarDto {
    @NotBlank(message = "Campo obbligatorio")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "Campo obbligatorio")
    private String specie;

    @NotNull(message = "Campo obbligatorio")
    @DecimalMin("0")
    @DecimalMax("14")
    private Double phMin;

    @NotNull(message = "Campo obbligatorio")
    private Double phMax;

    @NotNull(message = "Campo obbligatorio")
    private FabbisognoIdrico fabbisognoIdrico;

    @NotBlank(message = "Campo obbligatorio")
    private String tipoTerreno;
}