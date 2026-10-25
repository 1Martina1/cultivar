package com.martina.cultivar.vivaio.repository;

import com.martina.cultivar.vivaio.dto.Attrezzo;
import com.martina.cultivar.vivaio.dto.Pianta;
import com.martina.cultivar.vivaio.dto.Prodotto;
import com.martina.cultivar.vivaio.dto.Seme;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Repository NON reattivo: restituisce Optional e List, come nei progetti del team
@Repository
public class CatalogoRepository {

    // Il magazzino: chiave = id, valore = un Prodotto qualsiasi (Pianta, Attrezzo o Seme)
    private final Map<Long, Prodotto> catalogo = new HashMap<>();

    public CatalogoRepository() {
        // Piante: id, nome, tipo, prezzo, quantita
        catalogo.put(1L, new Pianta(1L, "Lavanda", "AROMATICA", 6.50, 12));
        catalogo.put(2L, new Pianta(2L, "Geranio", "FIORE", 4.50, 0));      // esaurita
        catalogo.put(3L, new Pianta(3L, "Ortensia", "FIORE", 15.00, 3));    // ultimi pezzi
        catalogo.put(4L, new Pianta(4L, "Ulivo", "ALBERO", 45.00, 8));
        // Attrezzi: id, nome, prezzo
        catalogo.put(5L, new Attrezzo(5L, "Rastrello", 12.00));
        catalogo.put(6L, new Attrezzo(6L, "Annaffiatoio", 9.90));
        // Semi: id, specie, prezzo, bustine
        catalogo.put(7L, new Seme(7L, "Basilico", 1.80, 30));
        catalogo.put(8L, new Seme(8L, "Pomodoro", 2.20, 0));                // bustine finite
    }

    // Optional: contiene il prodotto, oppure è vuoto se l'id non esiste
    // (Optional.ofNullable trasforma il null di map.get in un Optional vuoto)
    public Optional<Prodotto> findById(Long id) {
        return Optional.ofNullable(catalogo.get(id));
    }

    // Tutti i prodotti, in una lista nuova (così chi la riceve non tocca il magazzino)
    public List<Prodotto> findAll() {
        return new ArrayList<>(catalogo.values());
    }

}
