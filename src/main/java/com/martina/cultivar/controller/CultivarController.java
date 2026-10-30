package com.martina.cultivar.controller;

import com.martina.cultivar.dto.*;
import com.martina.cultivar.service.CultivarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cultivar")
@RequiredArgsConstructor
@Tag(name = "Progetto cultivar", description = "Usiamo Intellij e tante tecnologie simpatiche ma con troppi nomi stressanti da imparare")
public class CultivarController {

    private final CultivarService cultivarService;

    @GetMapping("/adatte")
    @Operation(summary = "Metodo per recuperare le cultivar adatte in base al tipo di terreno ricevuto", description = "ooooh nanni, o icchè lo so io")
    @ApiResponse(responseCode = "200", description = "Lista delle cultivar adatte")
    @ApiResponse(responseCode = "404", description = "Tipo di terreno non trovato")
    public List<CultivarDto> pianteAdatte(@Parameter(description = "E' il tipo di terreno", example = "stallatico aka merda") @RequestParam("terreno") String nomeTerreno, @Parameter(description = "E' il ph del terreno", example = "per le zone intime ph neutro-leggermente acido") @RequestParam Double ph){

        return cultivarService.trovaCultivarAdatte(nomeTerreno, ph);

    }

    @PostMapping("")
    @Operation(summary = "Metodo per inserire nuove cultivar", description = "ooooh nanni, o icchè lo so io")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Inserimento cultivar non effettuato")
    @ApiResponse(responseCode = "400", description = "Inserimento con dati errati")
    @ApiResponse(responseCode = "409", description = "Inserimento con tipoTerreno errato")
    @ResponseStatus(HttpStatus.CREATED)
    public CultivarDto inserimentoCultivar(@Valid @RequestBody InserimentoCultivarDto inserimentoCultivarDto){

        return cultivarService.inserimentoCultivar(inserimentoCultivarDto);

    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupero cultivar tramite id", description = "Metodo per recuperare una cultivar tramite id")
    @ApiResponse(responseCode = "404", description = "Cultivar non trovata")
    public CultivarDto dettaglioCultivar(@PathVariable Long id){

        return cultivarService.dettaglioCultivar(id);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimino cultivar tramite id", description = "Metodo per eliminare una cultivar tramite id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "NO_CONTENT")
    @ApiResponse(responseCode = "404", description = "Cultivar non trovata")
    public void eliminaCultivar(@PathVariable Long id){

        cultivarService.eliminaCultivar(id);

    }

    @GetMapping("/statistiche")
    @Operation(summary = "Statistiche delle cultivar per tipo di terreno", description = "Invii il tipo terreno e per ogni fabbisognoIdrico vuoi la lista dei nomi delle cultivar.")
    @ApiResponse(responseCode = "404", description = "Tipo di terreno non trovato")
    public StatisticheTerrenoDto statisticheTerreno(@Parameter(description = "E' il tipo di terreno", example = "argilloso") @RequestParam("terreno") String nomeTerreno){

        return cultivarService.statisticheTerreno(nomeTerreno);

    }

    @GetMapping("/riepilogo")
    @Operation(summary = "Restituisce il riepilogo delle cultivar di una specie", description = "Dato il nome di una specie (per esempio \"vite\"), restituisce un riassunto di tutte le cultivar di quella specie.")
    @ApiResponse(responseCode = "200", description = "Riepilogo trovato")
    @ApiResponse(responseCode = "404", description = "Tipo di specie non trovata")
    public RiepilogoSpecieDto cultivarDiUnaSpecie(@Parameter(description = "E' la specie", example = "vite") @RequestParam("specie") String nomeSpecie){

        return cultivarService.ottieniCultivarDiSpecie(nomeSpecie);

    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifica cultivar", description = "Aggiorna i dati di una cultivar che esiste già.")
    @ApiResponse(responseCode = "200", description = "Cultivar modificata")
    @ApiResponse(responseCode = "400", description = "Inserimento con dati errati")
    @ApiResponse(responseCode = "404", description = "Cultivar o tipo di terreno non trovato")
    public CultivarDto modificaCultivar(@Parameter(description = "Id della cultivar", example = "2") @PathVariable Long id, @Valid @RequestBody InserimentoCultivarDto inserimentoCultivarDto){

        return cultivarService.modificaCultivar(id, inserimentoCultivarDto);

    }

    @PatchMapping("/{id}")
    @Operation(summary = "Modifica cultivar", description = "Aggiorna i dati di una cultivar che esiste già.")
    @ApiResponse(responseCode = "200", description = "Cultivar modificata")
    @ApiResponse(responseCode = "400", description = "Inserimento con dati errati")
    @ApiResponse(responseCode = "404", description = "Cultivar o tipo di terreno non trovato")
    public CultivarDto modificaParzialeCultivar(@Parameter(description = "Id della cultivar", example = "2") @PathVariable Long id, @Valid @RequestBody ModificaParzialeCultivarDto modificaParzialeCultivarDto){

        return cultivarService.modificaParzialeCultivar(id, modificaParzialeCultivarDto);

    }

    @GetMapping("/terreni/confronto")
    @Operation(summary = "Restituisce per ogni terreno le cultivar che ci crescono", description = "Restituisce, per ogni tipo di terreno presente nel sistema, un riepilogo delle cultivar che ci crescono.")
    @ApiResponse(responseCode = "200", description = "Ok")
    public CaratteristicheTerrenoDto terrenoECultivar(){

        return cultivarService.ottieniCaratteristiheTerreno();

    }

    @GetMapping("/conteggio-specie")
    @Operation(summary = "Conteggio cultivar per specie", description = "Restituisce, per ogni specie presente nel sistema, quante cultivar ci sono.")
    @ApiResponse(responseCode = "200", description = "Ok")
    public Map<String, Long> recuperaCultivarPerSpecie () {

        return cultivarService.recuperaCultivarPerSpecie();

    }

    @GetMapping("/conteggio-fabbisogni")
    @Operation(summary = "Conteggio cultivar per fabbisogno", description = "Dato un tipo di terreno, restituisce quante cultivar ci sono per ogni fabbisogno idrico su quel terreno.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Tipo di terreno non trovato...")
    public CultivarPerFabbisognoDto recuperaCultivarPerFabbisogno (@RequestParam String terreno) {

        return cultivarService.recuperaCultivarPerFabbisogno(terreno);

    }

    @GetMapping("/estremi-ph")
    @Operation(summary = "Restituisce il pH minimo più basso e il pH massimo più alto", description = "Dato un tipo di terreno, restituisce il pH minimo più basso e il pH massimo più alto tra le sue cultivar, e a quale cultivar appartengono.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Tipo di terreno non trovato...")
    public PhTerrenoDto recuperaDatiPhPerTerrreno (@RequestParam String terreno) {

        return cultivarService.recuperaDatiPH(terreno);

    }

    @GetMapping("/scheda-specie")
    @Operation(summary = "Riassunto cultivar per specie", description = "Data una specie, restituisce una scheda riassuntiva delle sue cultivar.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Specie non trovata...")
    public SchedaRiassuntivaCultivarDto recuperaSchedaCultivar (@RequestParam String specie) {

        return cultivarService.recuperaSchedaCultivar(specie);

    }

    @GetMapping("/consiglio-laboratorio")
    @Operation(summary = "Recupera le cultivar adatte ad un determinato terreno", description = "Dato un terreno, chiede ad AgroLab il pH misurato e restituisce le cultivar adatte a quel pH.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Il terreno non esiste nel nostro database")
    public RecuperaCultivarPerTerrenoDto recuperaCultivarAdatteAlTerreno(@RequestParam String terreno) {

        return cultivarService.recuperaCultivarAdatteAlTerreno(terreno);

    }

    @GetMapping("/{id}/compatibilita")
    @Operation(summary = "Info cultivar per terreno e ph", description = "Data una cultivar, un terreno e un pH, dice se la cultivar può crescerci, spiega il perché, e propone delle alternative della stessa specie.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "400", description = "Il pH deve essere compreso tra 0 e 14")
    @ApiResponse(responseCode = "404", description = "Il terreno o la cultivar non esistono nel nostro database")
    public InfoCultivarPerTerrenoEPhDto infoCultivarPerTerrenoEPh(@Parameter(description = "Id della cultivar", example = "2") @PathVariable Long id, @RequestParam String terreno, @RequestParam Double ph) {

        return cultivarService.infoCultivarPerTerrenoEPh(id, terreno, ph);

    }

    @GetMapping("/tolleranza-ph")
    @Operation(summary = "Info cultivar per terreno e ph", description = "Restituisce, per una specie, la cultivar più tollerante al pH (quella con l’intervallo più ampio) e quella meno tollerante (l’intervallo più stretto).")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Nessuna cultivar trovata per la specie: ")
    public InfoTolleranzaAlPhCultivarDto tolleranzaAlPhCultivar(@RequestParam String specie) {

        return cultivarService.tolleranzaAlPhCultivar(specie);

    }

    @GetMapping("/profilo-terreno")
    @Operation(summary = "Info cultivar per terreno", description = "Restituisce un profilo delle cultivar che crescono su un tipo di terreno: quante sono, qual è il fabbisogno idrico più diffuso e quale cultivar tollera più pH diversi.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Nessuna cultivar trovata per il terreno: ")
    public ProfiloCultivarPerTerrenoDto profiloCultivarPerTerreno(@RequestParam String nomeTerreno) {

        return cultivarService.profiloCultivarPerTerreno(nomeTerreno);

    }

    @GetMapping("/classifica-terreno")
    @Operation(summary = "Restituisce le cultivar di un terreno in classifica", description = "Restituisce le cultivar di un terreno in classifica, dalla più tollerante al pH alla meno tollerante, più l’elenco delle specie presenti.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "404", description = "Nessuna cultivar trovata per il terreno: ")
    public ClassificaCultivarDto cultivarDiUnTerrenoInClassifica(@RequestParam String nomeTerreno) {

        return cultivarService.cultivarDiUnTerrenoInClassifica(nomeTerreno);

    }

    @GetMapping("/consigliate")
    @Operation(summary = "Dato il nome di un terreno e un pH, restituisce le cultivar di quel terreno che sopportano quel pH", description = "Dato il nome di un terreno e un pH, restituisce le cultivar di quel terreno che sopportano quel pH. Per ognuna c’è il margine, cioè quanto il pH è lontano dal bordo più vicino del suo intervallo, e la lista va dalla più sicura alla più al limite.")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "400", description = "Ph non valido")
    @ApiResponse(responseCode = "404", description = "Nessuna cultivar trovata per il terreno: ")
    public List<CultivarConsigliataDto> cultivarDelterrenoCheSopportanoIlPh(@RequestParam String nomeTerreno, @RequestParam Double ph) {

        return cultivarService.cultivarDelterrenoCheSopportanoIlPh(nomeTerreno, ph);

    }

    @GetMapping("/riepilogo-terreni")
    @Operation(summary = "Per ogni terreno, un riepilogo delle cultivar che ci crescono", description = "Per ogni terreno, un riepilogo delle cultivar che ci crescono: quante sono, il loro pH massimo medio e i loro nomi. I terreni con più cultivar vanno per primi.")
    @ApiResponse(responseCode = "200", description = "Ok")
    public List<RiepilogoTerrenoDto> riepilogoCultivarPerTerreno() {

        return cultivarService.riepilogoCultivarPerTerreno();

    }
}