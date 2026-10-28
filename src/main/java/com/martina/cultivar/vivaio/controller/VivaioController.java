package com.martina.cultivar.vivaio.controller;

import com.martina.cultivar.dto.CultivarDto;
import com.martina.cultivar.dto.InserimentoCultivarDto;
import com.martina.cultivar.vivaio.dto.*;
import com.martina.cultivar.vivaio.service.CatalogoService;
import com.martina.cultivar.vivaio.service.PiantaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/vivaio")
@RequiredArgsConstructor
public class VivaioController {

    private final PiantaService piantaService;
    private final CatalogoService catalogoService;

    @GetMapping("/piante/{id}")
    @Operation(summary = "Recupera le piante in base all'id", description = "Recupera le piante in base all'id")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Pianta non trovata con id: ")
    public Mono<Pianta> recuperaPiantaPerId (@PathVariable Long id) {

        return piantaService.trovaPiantaPerId(id);

    }

    @GetMapping("/catalogo/{id}/scheda")
    @Operation(summary = "Dato l’id di un prodotto qualsiasi, restituisce la sua scheda descrittiva,", description = "Questa API, dato l’id di un prodotto qualsiasi, restituisce la sua scheda descrittiva, un testo su più righe. La scheda è diversa per ogni tipo di prodotto, e cambia anche se il prodotto è esaurito.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Prodotto non trovato con id: ")
    public SchedaProdotto recuperaSchedaPerId (@PathVariable Long id) {

        return catalogoService.schedaProdotto(id);

    }

    @GetMapping("/catalogo/{id}/prezzo-scontato")
    @Operation(summary = "Sconti in base al prodotto", description = "Il vivaio fa degli sconti, ma le regole sono diverse per ogni tipo di prodotto. Questa API, dato l’id di un prodotto, restituisce il prezzo pieno, la percentuale di sconto che gli spetta e il prezzo finale.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Prodotto non trovato con id: ")
    public PrezzoScontato scontoPerProdotto (@PathVariable Long id) {

        return catalogoService.prezzoScontato(id);

    }

    @GetMapping("/catalogo/piante/riepilogo")
    @Operation(summary = "Panoramica del magazzino delle piante", description = "Panoramica del magazzino delle piante, raggruppata per tipo (AROMATICA, FIORE, ALBERO…). Per ogni tipo vuole sapere quante piante diverse ci sono, quanti pezzi in totale, il prezzo medio, la pianta più cara e quali sono esaurite. È il tipo di API “da gestionale” che trovi spesso nei progetti veri.")
    @ApiResponse(responseCode = "200", description = "Ok")
    public List<RiepilogoTipoPianta> magazzino () {

        return catalogoService.riepilogoPiantePerTipo();

    }

    @GetMapping("/catalogo/riepilogo-categorie")
    @Operation(summary = "Riepilogo del catalogo per categoria", description = "Il responsabile vuole un colpo d’occhio su tutto il catalogo, diviso per categoria: piante, attrezzi e semi. Per ogni categoria vuole sapere quanti prodotti ci sono, il prezzo medio, il prezzo più basso e qual è il prodotto più economico. Le categorie con più prodotti devono comparire per prime.")
    @ApiResponse(responseCode = "200", description = "Ok")
    public List<RiepilogoCategoria> catalogoPerCategorie () {

        return catalogoService.riepilogoPerCategoria();

    }

    @GetMapping("/catalogo/da-riordinare")
    @Operation(summary = "Prodotti sotto scorta da riordinare", description = "Il vivaio deve fare l’ordine ai fornitori. Questa API restituisce la lista dei prodotti sotto scorta, cioè che stanno finendo, con quanti pezzi ordinare per ognuno. Gli attrezzi non c’entrano: non hanno una quantità.")
    @ApiResponse(responseCode = "200", description = "Ok")
    public List<DaRiordinare> prodottiDaRiordinare () {

        return catalogoService.prodottiDaRiordinare();

    }

    @PostMapping("/catalogo/{id}/acquisto")
    @Operation(summary = "L’API controlla che l’acquisto sia possibile, scala i pezzi dal magazzino e restituisce lo scontrino", description = "Un cliente compra un certo numero di pezzi di una pianta. L’API controlla che l’acquisto sia possibile, scala i pezzi dal magazzino e restituisce lo scontrino: cosa ha comprato, quanto ha speso e quanti pezzi restano.")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Prodotto non trovato")
    @ApiResponse(responseCode = "400", description = "Quantità non valida (zero o negativa), oppure il prodotto non è una pianta")
    @ApiResponse(responseCode = "409", description = "Disponibili solo X pezzi di NOME")
    public Scontrino acquistoEScontrino(@PathVariable Long id, @RequestBody RichiestaAcquisto richiesta){

        return catalogoService.acquista(id, richiesta);

    }
}
