package com.martina.cultivar.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfiloCultivarPerTerrenoDto {
    private String nomeTerreno;
    private Long numeroCultivar;
    private List<String> nomiCultivar;
    private Map<FabbisognoIdrico, Long> conteggioPerFabbisogno;
    private FabbisognoIdrico fabbisognoPiuFrequente;
    private String cultivarPiuTollerantePh;
}