package com.martina.cultivar.client;

import com.martina.cultivar.client.dto.AnalisiTerrenoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AgroLabClient {

    private final RestClient restClient;   // creato in una @Configuration con baseUrl del provider

    public AnalisiTerrenoResponse recuperaAnalisi(String nomeTerreno) {
        return restClient.get()
                .uri("/analisi/terreni/{nomeTerreno}", nomeTerreno)
                .retrieve()                  // esegue la chiamata
                .body(AnalisiTerrenoResponse.class);      // trasforma il JSON in DTO
    }
}