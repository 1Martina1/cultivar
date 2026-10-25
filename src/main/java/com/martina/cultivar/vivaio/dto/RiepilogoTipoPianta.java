package com.martina.cultivar.vivaio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiepilogoTipoPianta {
    private String tipo;
    private long numeroPiante;
    private Integer quantitaTotale;
    private Double prezzoMedio;
    private String piantaPiuCara;
    private List<String> esaurite;
}
