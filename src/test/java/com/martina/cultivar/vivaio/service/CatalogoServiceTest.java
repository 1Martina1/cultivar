package com.martina.cultivar.vivaio.service;

import com.martina.cultivar.vivaio.dto.Attrezzo;
import com.martina.cultivar.vivaio.dto.Pianta;
import com.martina.cultivar.vivaio.dto.Prodotto;
import com.martina.cultivar.vivaio.dto.RiepilogoCategoria;
import com.martina.cultivar.vivaio.repository.CatalogoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Attiva Mockito per questa classe di test (niente Spring, niente database: velocissimo)
@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    // Il repository FINTO: restituirà solo quello che gli diciamo noi
    @Mock
    private CatalogoRepository catalogoRepository;

    // Il service VERO, a cui Mockito "inietta" il repository finto
    @InjectMocks
    private CatalogoService catalogoService;

    @Test
    void riepilogoPerCategoria_piantePiuNumerose_vengonoPrima() {
        // ===== GIVEN: preparo i prodotti finti della storia =====
        Pianta lavanda     = new Pianta(1L, "Lavanda", "AROMATICA", 6.50, 12);   // id, nome, tipo, prezzo, quantita
        Pianta salvia      = new Pianta(2L, "Salvia", "AROMATICA", 4.00, 0);
        Attrezzo rastrello = new Attrezzo(3L, "Rastrello", 12.00);              // id, nome, prezzo

        // La lista che il repository finto restituirà
        List<Prodotto> prodotti = List.of(lavanda, salvia, rastrello);

        // Il copione del mock: "quando qualcuno chiama findAll(), restituisci questa lista"
        when(catalogoRepository.findAll()).thenReturn(prodotti);

        // ===== WHEN: chiamo DAVVERO il metodo da testare =====
        List<RiepilogoCategoria> risultato = catalogoService.riepilogoPerCategoria();

        // ===== THEN: controllo il risultato (prima l'atteso, poi l'ottenuto) =====

        // Due categorie nella storia (PIANTA e ATTREZZO) -> due riepiloghi
        assertEquals(2, risultato.size());

        // Il PRIMO deve essere PIANTA, perché ha più prodotti (2 contro 1)
        RiepilogoCategoria piante = risultato.get(0);
        assertEquals("PIANTA", piante.getCategoria());
        assertEquals(2L, piante.getNumeroProdotti());              // 2L: il campo è Long
        assertEquals(5.25, piante.getPrezzoMedio());               // (6.50 + 4.00) / 2
        assertEquals(4.0, piante.getPrezzoMinimo());               // il prezzo della Salvia
        assertEquals("Salvia", piante.getProdottoPiuEconomico());  // CHI costa meno

        // Il SECONDO deve essere ATTREZZO
        RiepilogoCategoria attrezzi = risultato.get(1);
        assertEquals("ATTREZZO", attrezzi.getCategoria());
        assertEquals(1L, attrezzi.getNumeroProdotti());            // un solo attrezzo
        assertEquals(12.0, attrezzi.getPrezzoMedio());             // con un solo prodotto, la media è il suo prezzo
        assertEquals(12.0, attrezzi.getPrezzoMinimo());            // e anche il minimo
        assertEquals("Rastrello", attrezzi.getProdottoPiuEconomico());

        // Il service ha davvero chiesto i prodotti al repository?
        verify(catalogoRepository).findAll();
    }
}
