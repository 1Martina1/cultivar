package com.martina.cultivar.vivaio.controller;

import com.martina.cultivar.vivaio.dto.Pianta;
import com.martina.cultivar.vivaio.service.PiantaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/vivaio")
@RequiredArgsConstructor
public class VivaioController {

    private final PiantaService piantaService;

    @GetMapping("/piante/{id}")
    @Operation(summary = "Recupera le piante in base all'id", description = "Recupera le piante in base all'id")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Pianta non trovata con id: ")
    public Mono<Pianta> recuperaPiantaPerId (@PathVariable Long id) {

        return piantaService.trovaPiantaPerId(id);

    }
}
