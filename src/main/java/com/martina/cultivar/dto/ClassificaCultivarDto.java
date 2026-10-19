package com.martina.cultivar.dto;

import com.martina.cultivar.entity.FabbisognoIdrico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassificaCultivarDto {
    private String nomeTerreno;
    private Long numeroCultivar;
    private List<CultivarDto> classifica;
    private List<String> specie;
}