package com.martina.cultivar.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatiTerreno {
    private String nomeTerreno;
    private Long numeroCultivar;
    private Long numeroSpecie;
    private FabbisognoIdrico fabbisognoPiuFrequente;
    private String cultivarPhPiuAmpio;
}