package com.martina.cultivar.vivaio.service;

import com.martina.cultivar.vivaio.dto.Pianta;
import com.martina.cultivar.vivaio.dto.SchedaProdotto;
import com.martina.cultivar.vivaio.exception.PiantaNotFoundException;
import com.martina.cultivar.vivaio.repository.PiantaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PiantaService {

    private final PiantaRepository piantaRepository;

    public Mono<Pianta> trovaPiantaPerId(Long id) {

        return piantaRepository.findById(id).switchIfEmpty(Mono.error(new PiantaNotFoundException("Pianta non trovata con id: " + id)));

    }


}
