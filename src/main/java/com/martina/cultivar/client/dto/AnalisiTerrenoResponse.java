package com.martina.cultivar.client.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import com.martina.cultivar.entity.TipoTerreno;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalisiTerrenoResponse {
    private String terreno;
    private Double phMisurato;
    private Double umidita;
    private LocalDate dataAnalisi;
}