package com.martina.cultivar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // Spring chiama questo metodo una volta all'avvio
    // e tiene il RestClient che restituisce, pronto da iniettare
    @Bean
    public RestClient agroLabRestClient(@Value("${agrolab.base-url}") String baseUrl) {
        return RestClient.builder()   // inizio a costruire il "telefono"
                .baseUrl(baseUrl)     // gli do il numero base: https://api.agrolab.example
                .build();             // fine: il RestClient è pronto
    }
}