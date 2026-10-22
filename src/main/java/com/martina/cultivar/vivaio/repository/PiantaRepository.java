package com.martina.cultivar.vivaio.repository;

import com.martina.cultivar.vivaio.dto.Pianta;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

// @Repository: Spring crea UN oggetto di questa classe all'avvio
// e lo "inietta" dove serve (es. nel PiantaService)
@Repository
public class PiantaRepository {

    // Il "magazzino" delle piante: chiave = id, valore = la pianta.
    // private: da fuori nessuno può toccarlo direttamente.
    // final: il magazzino è sempre lo stesso (il contenuto può cambiare, il magazzino no)
    private final Map<Long, Pianta> piante = new HashMap<>();

    // Costruttore: viene eseguito una volta, quando Spring crea il repository.
    // Qui riempiamo il magazzino con le piante di prova.
    public PiantaRepository() {
        // new Pianta(id, nome, tipo, prezzo, quantita)
        piante.put(1L, new Pianta(1L, "Lavanda", "AROMATICA", 6.50, 12));
        piante.put(2L, new Pianta(2L, "Rosmarino", "AROMATICA", 5.00, 20));
        piante.put(3L, new Pianta(3L, "Geranio", "FIORE", 4.50, 0));     // esaurita
        piante.put(4L, new Pianta(4L, "Ortensia", "FIORE", 15.00, 5));
        piante.put(5L, new Pianta(5L, "Ulivo", "ALBERO", 45.00, 3));
        piante.put(6L, new Pianta(6L, "Limone", "ALBERO", 35.00, 0));    // esaurita
    }

    // Cerca una pianta per id.
    // piante.get(id) restituisce la pianta, oppure null se l'id non esiste.
    // Mono.justOrEmpty: se c'è la pianta -> Mono con la pianta dentro;
    //                   se è null        -> Mono VUOTO (nessun errore, solo "niente")
    public Mono<Pianta> findById(Long id) {
        return Mono.justOrEmpty(piante.get(id));
    }

    // Restituisce tutte le piante.
    // piante.values() = la collezione di tutte le piante (senza le chiavi).
    // Flux.fromIterable: trasforma la collezione in un Flux,
    // che "emette" le piante una alla volta.
    public Flux<Pianta> findAll() {
        return Flux.fromIterable(piante.values());
    }
}