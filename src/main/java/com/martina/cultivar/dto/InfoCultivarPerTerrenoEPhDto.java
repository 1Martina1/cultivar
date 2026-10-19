package com.martina.cultivar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoCultivarPerTerrenoEPhDto {
    private String nomeCultivar;
    private String nomeTerreno;
    private Double ph;
    private Boolean terrenoCompatibile;
    private Boolean phCompatibile;
    private Boolean compatibile;
    private String messaggio;
    private List<String> alternative;
}