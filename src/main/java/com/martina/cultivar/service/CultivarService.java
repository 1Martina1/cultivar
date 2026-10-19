package com.martina.cultivar.service;

import com.martina.cultivar.client.AgroLabClient;
import com.martina.cultivar.client.dto.AnalisiTerrenoResponse;
import com.martina.cultivar.dto.*;
import com.martina.cultivar.entity.Cultivar;
import com.martina.cultivar.entity.FabbisognoIdrico;
import com.martina.cultivar.entity.TipoTerreno;
import com.martina.cultivar.exception.CultivarNotFoundException;
import com.martina.cultivar.exception.PhNonValidoException;
import com.martina.cultivar.exception.TipoTerrenoNotFoundException;
import com.martina.cultivar.mapper.CultivarMapper;
import com.martina.cultivar.repository.CultivarRepository;
import com.martina.cultivar.repository.TipoTerrenoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class CultivarService {

    private final TipoTerrenoRepository tipoTerrenoRepository;
    private final CultivarRepository cultivarRepository;
    private final CultivarMapper cultivarMapper;
    private final AgroLabClient agroLabClient;

    @Transactional(readOnly = true)
    public List<CultivarDto> trovaCultivarAdatte(String nomeTerreno, Double ph) {

        tipoTerrenoRepository.findByNomeIgnoreCase(nomeTerreno)
                .orElseThrow(() -> new TipoTerrenoNotFoundException("Tipo di terreno non trovato: " + nomeTerreno));

        return cultivarRepository.findAdatte(nomeTerreno, ph)
                .stream()
                .map(cultivarMapper::cultivarToCultivarDto)
                .toList();
    }

    @Transactional
    public CultivarDto inserimentoCultivar(InserimentoCultivarDto dto) {

        // 1. cerco il terreno (serve l'entity, non la stringa)
        TipoTerreno terreno = tipoTerrenoRepository.findByNomeIgnoreCase(dto.getTipoTerreno())
                .orElseThrow(() -> new TipoTerrenoNotFoundException("Tipo di terreno non trovato: " + dto.getTipoTerreno()));

        // 2. creo la Cultivar e la riempio con i dati del DTO
        Cultivar cultivar = cultivarMapper.inserimentoCultivarDtotoCultivarDto(dto, terreno);

        // 3. salvo
        Cultivar salvata = cultivarRepository.save(cultivar);

        // 4. restituisco il DTO di risposta
        return cultivarMapper.cultivarToCultivarDto(salvata);

    }

    @Transactional(readOnly = true)
    public CultivarDto dettaglioCultivar(Long id) {

        Cultivar cultivar = cultivarRepository.findById(id)
                .orElseThrow(() -> new CultivarNotFoundException("Cultivar non trovata con id: " + id));

        return  cultivarMapper.cultivarToCultivarDto(cultivar);
    }

    @Transactional
    public void eliminaCultivar(Long id) {

        boolean cultivarExist = cultivarRepository.existsById(id);

        if(!cultivarExist){
            throw new CultivarNotFoundException("Cultivar non trovata con id: " + id);
        }

        cultivarRepository.deleteById(id);

    }

    @Transactional(readOnly = true)
    public StatisticheTerrenoDto statisticheTerreno(String nomeTerreno) {

        tipoTerrenoRepository.findByNomeIgnoreCase(nomeTerreno)
                .orElseThrow(() -> new TipoTerrenoNotFoundException("Tipo di terreno non trovato: " + nomeTerreno));

        List<Cultivar> cultivars = cultivarRepository.findByTipoTerrenoNomeIgnoreCase(nomeTerreno);

        Long numeroCultivar = cultivars.stream().count();

        //map(Cultivar::getSpecie) → prende solo la specie: [Vite, Olivo, Vite]
        //.distinct() → toglie i doppioni, ogni valore resta una volta sola: [Vite, Olivo]
        //.sorted() → ordina, le stringhe in ordine alfabetico: [Olivo, Vite]
        //.toList() → chiude lo stream e restituisce la lista.
        List<String> specie = cultivars.stream().map(Cultivar::getSpecie).distinct().sorted().toList();

        Double phMinimo = cultivars.stream().map(Cultivar::getPhMin).min(Comparator.naturalOrder()).orElse(null);
        Double phMassimo = cultivars.stream().map(Cultivar::getPhMax).max(Comparator.naturalOrder()).orElse(null);

        //Obiettivo: raggruppare le cultivar in base al loro fabbisogno idrico. Per ogni fabbisogno vuoi la lista dei nomi delle cultivar.
        Map<FabbisognoIdrico, List<String>> cultivarPerFabbisognoIdrico = cultivars.stream()
                .collect(Collectors.groupingBy(
                        Cultivar::getFabbisognoIdrico, //divide in gruppi per fabbisogno
                        Collectors.mapping(Cultivar::getNome, Collectors.toList()))); //in ogni gruppo prende solo il nome (è il map dentro i gruppi) e mette i nomi in una lista.

        return new StatisticheTerrenoDto(numeroCultivar, specie, phMinimo, phMassimo, cultivarPerFabbisognoIdrico);

    }

    @Transactional(readOnly = true)
    public RiepilogoSpecieDto ottieniCultivarDiSpecie(String nomeSpecie) {

        List<Cultivar> cultivars = cultivarRepository.findBySpecieIgnoreCase(nomeSpecie);

        if(cultivars.isEmpty()){
            throw new CultivarNotFoundException("Cultivar non trovata in base al nome della specie: " + nomeSpecie);
        }

        Long numeroCultivar = cultivars.stream().count();

        List<String> nomiCultivar = cultivars.stream().map(Cultivar::getNome).sorted().toList();

        List<String> terreni = cultivars.stream().map(c -> c.getTipoTerreno().getNome()).distinct().sorted().toList();

        Double phComuneMin = cultivars.stream().map(Cultivar::getPhMin).max(Comparator.naturalOrder()).orElse(null);
        Double phComuneMax = cultivars.stream().map(Cultivar::getPhMax).min(Comparator.naturalOrder()).orElse(null);

        Map<FabbisognoIdrico, Long> conteggioPerFabbisogno = cultivars.stream()
                .collect(Collectors.groupingBy(
                        Cultivar::getFabbisognoIdrico, Collectors.counting()));

        return new RiepilogoSpecieDto(cultivars.getFirst().getSpecie(), numeroCultivar, nomiCultivar, terreni, phComuneMin, phComuneMax, conteggioPerFabbisogno);
    }

    @Transactional
    public CultivarDto modificaCultivar(Long id, InserimentoCultivarDto inserimentoCultivarDto) {

        if(inserimentoCultivarDto.getPhMin() > inserimentoCultivarDto.getPhMax()){
            throw new PhNonValidoException("Il phMin " + inserimentoCultivarDto.getPhMin() + " non può essere maggiore del phMax " + inserimentoCultivarDto.getPhMax());
        }

        Cultivar cultivar = cultivarRepository.findById(id)                             // ← cultivar nasce qui (dati vecchi)
                .orElseThrow(() -> new CultivarNotFoundException("Cultivar non trovata con id: " + id));

        TipoTerreno tipoTerreno = tipoTerrenoRepository.findByNomeIgnoreCase(inserimentoCultivarDto.getTipoTerreno())
                .orElseThrow(() -> new TipoTerrenoNotFoundException("Tipo di terreno non trovato: " + inserimentoCultivarDto.getTipoTerreno()));

        //la variabile cultivar è come le chiavi: indica un oggetto in memoria;
        //quando chiami aggiornaCultivar(…, cultivar), il mapper riceve le chiavi dello stesso oggetto, non una copia;
        //il mapper cambia nome, specie, pH… dentro quell’oggetto;
        //quando poi fai save(cultivar), cultivar indica lo stesso oggetto, che ormai ha già i dati nuovi.
        //il mapper non “fa uscire” niente, lavora sull’oggetto che gli dai. Poi nel service vai avanti tranquilla, perché sai che quell’oggetto è già cambiato.
        // 1. modifica la cultivar che hai trovato (non restituisce niente: cambia "cultivar")
        cultivarMapper.aggiornaCultivar(inserimentoCultivarDto, tipoTerreno, cultivar);                 // ← cultivar qui viene modificata (dati nuovi)

        // 2. salva la cultivar modificata
        Cultivar cultivarSalvata = cultivarRepository.save(cultivar);                   // ← qui salvi cultivar (con i dati nuovi)

        // 3. trasformala in DTO e restituiscila
        return cultivarMapper.cultivarToCultivarDto(cultivarSalvata);
    }

    @Transactional
    public CultivarDto modificaParzialeCultivar(Long id, ModificaParzialeCultivarDto modificaParzialeCultivarDto) {

        Cultivar cultivar = cultivarRepository.findById(id)
                .orElseThrow(() -> new CultivarNotFoundException("Cultivar non trovata con id: " + id));

        TipoTerreno tipoTerreno = null;

        if(modificaParzialeCultivarDto.getTipoTerreno() != null) {
            tipoTerreno = tipoTerrenoRepository.findByNomeIgnoreCase(modificaParzialeCultivarDto.getTipoTerreno())
                    .orElseThrow(() -> new TipoTerrenoNotFoundException("Tipo di terreno non trovato: " + modificaParzialeCultivarDto.getTipoTerreno()));
        }

        cultivarMapper.dtoETerrenoToCultivar(modificaParzialeCultivarDto, tipoTerreno, cultivar);

        if(cultivar.getPhMin() != null && cultivar.getPhMax() != null && cultivar.getPhMin() > cultivar.getPhMax()){
            throw new PhNonValidoException("Il phMin " + cultivar.getPhMin() + " non può essere maggiore del phMax " + cultivar.getPhMax());
        }

        Cultivar cultivarSalvata = cultivarRepository.save(cultivar);

        return cultivarMapper.cultivarToCultivarDto(cultivarSalvata);
    }

    @Transactional(readOnly = true)
    public CaratteristicheTerrenoDto ottieniCaratteristiheTerreno() {

        CaratteristicheTerrenoDto caratteristicheTerrenoDto = new CaratteristicheTerrenoDto();
        List<DatiTerreno> cultivarDtoList = new ArrayList<>();

        // Prendo dal database TUTTI i tipi di terreno (anche quelli senza cultivar)
        List<TipoTerreno> tipiDiTerreno = tipoTerrenoRepository.findAll();

        // Ciclo sui terreni: a ogni giro "tipoTerreno1" è UN terreno diverso
        tipiDiTerreno.stream().forEach(tipoTerreno1 -> {

            DatiTerreno datiTerreno = new DatiTerreno();
            datiTerreno.setNomeTerreno(tipoTerreno1.getNome());

            // Prendo dal database le cultivar che crescono su QUESTO terreno
            // (se non ce ne sono, la lista è vuota: nessun errore)
            List<Cultivar> cultivars = cultivarRepository.findByTipoTerrenoNomeIgnoreCase(datiTerreno.getNomeTerreno());

            // Numero di cultivar = quanti elementi ci sono nella lista (lista vuota → 0)
            datiTerreno.setNumeroCultivar(cultivars.stream().count());

            // Numero di specie diverse:
            // map → prendo la specie di ogni cultivar [Vite, Olivo, Vite]
            // distinct → tolgo i doppioni
            // count → le conto
            datiTerreno.setNumeroSpecie(cultivars.stream().map(Cultivar::getSpecie).distinct().count());

            // FABBISOGNO PIÙ FREQUENTE, in due passi:
            // Passo 1: conto quante volte compare ogni fabbisogno
            // groupingBy → raggruppo per fabbisogno, counting → conto quanti per gruppo
            // risultato: {BASSO=1, MEDIO=2}
            Map<FabbisognoIdrico, Long> conteggioFabbisogni = cultivars.stream()
                    .collect(Collectors.groupingBy(Cultivar::getFabbisognoIdrico, Collectors.counting()));

            // Passo 2: prendo il fabbisogno con il conteggio più alto
            FabbisognoIdrico fabbisognoPiuFrequente = conteggioFabbisogni.entrySet().stream() // trasformo la Map in coppie: (BASSO,1), (MEDIO,2)
                    .max(Map.Entry.comparingByValue())   // prendo la coppia col NUMERO più alto: (MEDIO,2)
                    .map(Map.Entry::getKey)              // di quella coppia tengo solo il fabbisogno: MEDIO
                    .orElse(null);                       // se il terreno non ha cultivar → null

            // Metto il fabbisogno più frequente nel DTO
            datiTerreno.setFabbisognoPiuFrequente(fabbisognoPiuFrequente);

            // CULTIVAR CON IL pH PIÙ AMPIO
            String cultivarPhPiuAmpio = cultivars.stream()
                    .max(Comparator.comparingDouble(c -> c.getPhMax() - c.getPhMin())) // confronto le cultivar in base all'ampiezza (phMax - phMin) e prendo la più grande
                    .map(Cultivar::getNome)                                            // della cultivar vincente tengo solo il nome
                    .orElse(null);                                                     // se il terreno non ha cultivar → null

            // Metto il nome nel DTO
            datiTerreno.setCultivarPhPiuAmpio(cultivarPhPiuAmpio);

            // Il DTO di questo terreno è completo: lo aggiungo alla lista,
            // così non sparisce alla fine del giro
            cultivarDtoList.add(datiTerreno);
        });

        // Finito il ciclo, la lista contiene un DTO per ogni terreno. Ora la ordino:
        // comparing(getNumeroCultivar) → in base al numero di cultivar
        // reversed()                   → dal più grande al più piccolo
        // thenComparing(getNomeTerreno) → se due terreni hanno lo stesso numero, in ordine alfabetico
        cultivarDtoList.sort(comparing(DatiTerreno::getNumeroCultivar).reversed()
                .thenComparing(DatiTerreno::getNomeTerreno));

        // Metto la lista ordinata dentro il DTO contenitore
        caratteristicheTerrenoDto.setCultivarDtoList(cultivarDtoList);

        // Restituisco il DTO contenitore
        return caratteristicheTerrenoDto;
    }

    @Transactional(readOnly = true)
    public  Map<String, Long> recuperaCultivarPerSpecie() {

        //Restituisce, per ogni specie presente nel sistema, quante cultivar ci sono.
        List<Cultivar> cultivars = cultivarRepository.findAll();

        //.groupingBy legge la specie solo per scegliere il cesto, e nel cesto mette la cultivar intera
        return cultivars.stream().collect(Collectors.groupingBy(Cultivar::getSpecie, Collectors.counting()));

    }

    @Transactional(readOnly = true)
    public CultivarPerFabbisognoDto recuperaCultivarPerFabbisogno(String terreno) {

        // 1. Controllo che il terreno ESISTA (tabella dei terreni → Optional → orElseThrow)
        //    Se non esiste → eccezione → 404. Non salvo il risultato: mi serve solo il controllo.
        tipoTerrenoRepository.findByNomeIgnoreCase(terreno)
                .orElseThrow(() -> new TipoTerrenoNotFoundException("Tipo di terreno non trovato: " + terreno));

        // 2. Prendo le cultivar di quel terreno (tabella delle cultivar → List, anche vuota)
        List<Cultivar> cultivars = cultivarRepository.findByTipoTerrenoNomeIgnoreCase(terreno);

        // 3. Conto quante cultivar per ogni fabbisogno → {MEDIO=2, BASSO=1}
        //    (se la lista è vuota, la Map è vuota → {})
        Map<FabbisognoIdrico, Long> conteggioPerFabbisogno = cultivars.stream()
                .collect(Collectors.groupingBy(Cultivar::getFabbisognoIdrico, Collectors.counting()));

        // 4. Metto la Map nel DTO e lo restituisco
        CultivarPerFabbisognoDto cultivarPerFabbisognoDto = new CultivarPerFabbisognoDto();
        cultivarPerFabbisognoDto.setConteggioPerFabbisogno(conteggioPerFabbisogno);
        return cultivarPerFabbisognoDto;

    }

    @Transactional(readOnly = true)
    public PhTerrenoDto recuperaDatiPH(String nomeTerreno) {

        // 1. Il terreno esiste? (tabella dei terreni → Optional → orElseThrow → 404)
        tipoTerrenoRepository.findByNomeIgnoreCase(nomeTerreno)
                .orElseThrow(() -> new TipoTerrenoNotFoundException("Tipo di terreno non trovato: " + nomeTerreno));

        // 2. Le cultivar di quel terreno (List, anche vuota: niente orElseThrow)
        List<Cultivar> cultivars = cultivarRepository.findByTipoTerrenoNomeIgnoreCase(nomeTerreno);

        // 3. "QUANTO?" → map + naturalOrder → il valore (lista vuota → null)
        Double phMaxPiuAlto = cultivars.stream().map(Cultivar::getPhMax).max(Comparator.naturalOrder()).orElse(null);
        Double phMinPiuBasso = cultivars.stream().map(Cultivar::getPhMin).min(Comparator.naturalOrder()).orElse(null);

        // 4. "CHI?" → niente map prima, comparing(campo) → la cultivar → map(getNome) → il nome
        String cultivarPhMaxPiuAlto = cultivars.stream()
                .max(Comparator.comparing(Cultivar::getPhMax))   // Optional<Cultivar>
                .map(Cultivar::getNome)                           // Optional<String>
                .orElse(null);                                    // String (lista vuota → null)

        String cultivarPhMinPiuBasso = cultivars.stream()
                .min(Comparator.comparing(Cultivar::getPhMin))
                .map(Cultivar::getNome)
                .orElse(null);

        // 5. Riempio il DTO e lo restituisco (niente mapper: sono solo valori)
        PhTerrenoDto phTerrenoDto = new PhTerrenoDto();
        phTerrenoDto.setNomeTerreno(nomeTerreno);
        phTerrenoDto.setPhMaxPiuAlto(phMaxPiuAlto);
        phTerrenoDto.setPhMinPiuBasso(phMinPiuBasso);
        phTerrenoDto.setCultivarPhMaxPiuAlto(cultivarPhMaxPiuAlto);
        phTerrenoDto.setCultivarPhMinPiuBasso(cultivarPhMinPiuBasso);
        return phTerrenoDto;
    }

    @Transactional(readOnly = true)
    public SchedaRiassuntivaCultivarDto recuperaSchedaCultivar(String specie) {

        // Le cultivar di quella specie (List, anche vuota)
        List<Cultivar> cultivars = cultivarRepository.findBySpecieIgnoreCase(specie);

        // Non c'è una tabella delle specie: lista vuota = specie non trovata → 404
        if (cultivars.isEmpty()) {
            throw new CultivarNotFoundException("Specie non trovata: " + specie);
        }

        List<String> nomiCultivar = cultivars.stream().map(Cultivar::getNome).sorted().collect(Collectors.toList());

        Long numeroCultivar = cultivars.stream().count();
        Double phMaxPiuAlto = cultivars.stream().map(Cultivar::getPhMax).max(Comparator.naturalOrder()).orElse(null);
        String cultivarPhMinPiuBasso = cultivars.stream()
                .min(Comparator.comparingDouble(Cultivar::getPhMin))
                .map(Cultivar::getNome)
                .orElse(null);

        Map<String, Long> conteggioPerTerreno = cultivars.stream().collect(Collectors.groupingBy(c -> c.getTipoTerreno().getNome(), Collectors.counting()));

        SchedaRiassuntivaCultivarDto schedaRiassuntivaCultivarDto = new SchedaRiassuntivaCultivarDto();
        schedaRiassuntivaCultivarDto.setNomiCultivar(nomiCultivar);
        schedaRiassuntivaCultivarDto.setNumeroCultivar(numeroCultivar);
        schedaRiassuntivaCultivarDto.setPhMaxPiuAlto(phMaxPiuAlto);
        schedaRiassuntivaCultivarDto.setCultivarPhMinPiuBasso(cultivarPhMinPiuBasso);
        schedaRiassuntivaCultivarDto.setSpecie(cultivars.getFirst().getSpecie());
        schedaRiassuntivaCultivarDto.setConteggioPerTerreno(conteggioPerTerreno);
        return schedaRiassuntivaCultivarDto;
    }

    @Transactional(readOnly = true)
    public RecuperaCultivarPerTerrenoDto recuperaCultivarAdatteAlTerreno(String terreno) {

            TipoTerreno tipoTerreno = tipoTerrenoRepository.findByNomeIgnoreCase(terreno)
                .orElseThrow(() -> new TipoTerrenoNotFoundException("Tipo di terreno non trovato: " + terreno));

            AnalisiTerrenoResponse analisiTerrenoResponse = agroLabClient.recuperaAnalisi(terreno);

            List<CultivarDto> cultivarAdatte = cultivarRepository.findAdatte(terreno, analisiTerrenoResponse.getPhMisurato())
                        .stream()
                        .map(cultivarMapper::cultivarToCultivarDto)
                        .toList();

            RecuperaCultivarPerTerrenoDto recuperaCultivarPerTerrenoDto = new RecuperaCultivarPerTerrenoDto();
            recuperaCultivarPerTerrenoDto.setCultivarAdatte(cultivarAdatte);
            recuperaCultivarPerTerrenoDto.setNomeTerreno(tipoTerreno.getNome());
            recuperaCultivarPerTerrenoDto.setDataAnalisi(analisiTerrenoResponse.getDataAnalisi());
            recuperaCultivarPerTerrenoDto.setPhMisurato(analisiTerrenoResponse.getPhMisurato());

            return recuperaCultivarPerTerrenoDto;
    }

    // Metodo di sola lettura: legge dal DB ma non modifica niente
    @Transactional(readOnly = true)
    // Riceve l'id della cultivar da verificare, il nome del terreno e il pH richiesti; restituisce il DTO con il verdetto
    public InfoCultivarPerTerrenoEPhDto infoCultivarPerTerrenoEPh(Long id, String terreno, Double ph) {

        // ── BLOCCO 1: CONTROLLI ──

        // Controllo "gratis", fatto per primo: se il pH è fuori dalla scala 0–14...
        if (ph < 0 || ph > 14) {
            // ...lancio l'eccezione → l'handler risponde 400 e il metodo si ferma qui
            throw new PhNonValidoException("Il ph è minore di 0 o maggiore di 14");
        }

        // Cerco nel DB il terreno con quel nome (senza badare a maiuscole/minuscole): mi torna una scatola (Optional)
        TipoTerreno tipoTerreno = tipoTerrenoRepository.findByNomeIgnoreCase(terreno)
                // Se la scatola è piena la apro e ho il terreno; se è vuota lancio l'eccezione → 404
                .orElseThrow(() -> new TipoTerrenoNotFoundException("Terreno non trovato: " + terreno));

        // Cerco nel DB la cultivar con quell'id: altra scatola
        Cultivar cultivar = cultivarRepository.findById(id)
                // Piena → ho la cultivar; vuota → eccezione → 404
                .orElseThrow(() -> new CultivarNotFoundException("Id non trovato: " + id));

        // Creo il DTO vuoto che riempirò e restituirò
        InfoCultivarPerTerrenoEPhDto info = new InfoCultivarPerTerrenoEPhDto();

        // ── BLOCCO 2: I TRE BOOLEANI ──

        // Il terreno della cultivar è uguale a quello richiesto? Il risultato (true/false) va dritto nel setter
        info.setTerrenoCompatibile(cultivar.getTipoTerreno().equals(tipoTerreno));

        // Il pH richiesto sta dentro l'intervallo della cultivar (tra min e max, estremi compresi)?
        info.setPhCompatibile(ph >= cultivar.getPhMin() && ph <= cultivar.getPhMax());

        // Verdetto finale: compatibile solo se terreno E pH sono compatibili
        info.setCompatibile(info.getTerrenoCompatibile() && info.getPhCompatibile());

        // ── BLOCCO 3: IL MESSAGGIO (catena: scatta solo il primo caso vero) ──

        // 1° priorità: se il terreno NON è compatibile...
        if (!info.getTerrenoCompatibile()) {
            // ...dico su quale terreno cresce davvero la cultivar
            info.setMessaggio("Terreno non adatto: la cultivar cresce su " + cultivar.getTipoTerreno().getNome());
            // 2° priorità: altrimenti, se il pH è sotto il minimo...
        } else if (ph < cultivar.getPhMin()) {
            // ...dico qual è il minimo
            info.setMessaggio("pH troppo basso: il minimo è " + cultivar.getPhMin());
            // 3° priorità: altrimenti, se il pH è sopra il massimo...
        } else if (ph > cultivar.getPhMax()) {
            // ...dico qual è il massimo
            info.setMessaggio("pH troppo alto: il massimo è " + cultivar.getPhMax());
            // Nessuno dei casi sopra: è tutto a posto
        } else {
            info.setMessaggio("Compatibile");
        }

        // ── DATI DI RIEPILOGO ──

        // Il nome della cultivar che ho verificato
        info.setNomeCultivar(cultivar.getNome());
        // Il terreno RICHIESTO (quello vero della cultivar compare già nel messaggio, se diverso)
        info.setNomeTerreno(terreno);
        // Il pH RICHIESTO
        info.setPh(ph);

        // ── BLOCCO 4: LE ALTERNATIVE ──

        // Parto dalle cultivar compatibili con terreno e pH richiesti, e ci apro sopra uno stream
        List<String> alternative = cultivarRepository.findAdatte(terreno, ph).stream()
                // Tengo solo le altre della STESSA SPECIE della cultivar che sto verificando
                .filter(altra -> altra.getSpecie().equals(cultivar.getSpecie()))
                // Tolgo la cultivar stessa (confronto gli id: sono Long, quindi .equals e non !=)
                .filter(altra -> !altra.getId().equals(cultivar.getId()))
                // Da Cultivar passo a String: d'ora in poi lo stream contiene solo i nomi
                .map(Cultivar::getNome)
                // Ordino i nomi in ordine alfabetico
                .sorted()
                // Chiudo lo stream e ottengo la List<String>
                .toList();

        // Metto la lista delle alternative nel DTO (se non ce ne sono, è una lista vuota, mai null)
        info.setAlternative(alternative);

        // Restituisco il DTO al controller, che lo trasforma in JSON
        return info;
    }

    @Transactional(readOnly = true)
    public InfoTolleranzaAlPhCultivarDto tolleranzaAlPhCultivar(String specie) {

        List<Cultivar> cultivars = cultivarRepository.findBySpecieIgnoreCase(specie);

        if(cultivars.isEmpty()) {
            throw new CultivarNotFoundException("Nessuna cultivar trovata per la specie: " + specie);
        }

        String cultivarPiuTollerante = cultivars.stream()
                .max(Comparator.comparingDouble(c -> c.getPhMax() - c.getPhMin()))
                .map(Cultivar::getNome)
                .orElse(null);

        String cultivarMenoTollerante = cultivars.stream()
                .min(Comparator.comparingDouble(c -> c.getPhMax() - c.getPhMin()))
                .map(Cultivar::getNome)
                .orElse(null);

        Double ampiezzaMassima = cultivars.stream()
                .map(c -> c.getPhMax() - c.getPhMin())
                .max(Comparator.naturalOrder())
                .orElse(null);

        Double ampiezzaMinima = cultivars.stream()
                .map(c -> c.getPhMax() - c.getPhMin())
                .min(Comparator.naturalOrder())
                .orElse(null);

        InfoTolleranzaAlPhCultivarDto info = new InfoTolleranzaAlPhCultivarDto();
        info.setSpecie(specie);
        info.setNumeroCultivar((long) cultivars.size());
        info.setCultivarPiuTollerante(cultivarPiuTollerante);
        info.setAmpiezzaMassima(ampiezzaMassima);
        info.setCultivarMenoTollerante(cultivarMenoTollerante);
        info.setAmpiezzaMinima(ampiezzaMinima);

        return info;
    }

    @Transactional(readOnly = true)
    public ProfiloCultivarPerTerrenoDto profiloCultivarPerTerreno(String nomeTerreno) {

        TipoTerreno tipoTerreno = tipoTerrenoRepository.findByNomeIgnoreCase(nomeTerreno)
               .orElseThrow(() -> new TipoTerrenoNotFoundException("Terreno non trovato: " + nomeTerreno));

        ProfiloCultivarPerTerrenoDto profilo = new ProfiloCultivarPerTerrenoDto();

        List<Cultivar> cultivars = cultivarRepository.findByTipoTerrenoNomeIgnoreCase(nomeTerreno);

        if(cultivars.isEmpty()){
            throw new CultivarNotFoundException("Nessuna cultivar trovata per il terreno: " + tipoTerreno.getNome());

        }

        Map<FabbisognoIdrico, Long> conteggioPerFabbisogno = cultivars.stream()
                .collect(Collectors.groupingBy(Cultivar::getFabbisognoIdrico, Collectors.counting()));

        FabbisognoIdrico fabbisognoPiuFrequente = conteggioPerFabbisogno
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        String cultivarPiuTollerantePh = cultivars.stream()
                .max(Comparator.comparingDouble(c -> c.getPhMax() - c.getPhMin()))
                .map(Cultivar::getNome)
                .orElse(null);

        profilo.setNomeTerreno(tipoTerreno.getNome());
        profilo.setNumeroCultivar((long) cultivars.size());
        profilo.setNomiCultivar(cultivars.stream().map(Cultivar::getNome).sorted().toList());
        profilo.setConteggioPerFabbisogno(conteggioPerFabbisogno);
        profilo.setFabbisognoPiuFrequente(fabbisognoPiuFrequente);
        profilo.setCultivarPiuTollerantePh(cultivarPiuTollerantePh);

        return profilo;

    }

    @Transactional(readOnly = true)
    public ClassificaCultivarDto cultivarDiUnTerrenoInClassifica(String nomeTerreno) {

        TipoTerreno tipoTerreno = tipoTerrenoRepository.findByNomeIgnoreCase(nomeTerreno)
                .orElseThrow(() -> new TipoTerrenoNotFoundException("Terreno non trovato: " + nomeTerreno));

        ClassificaCultivarDto  classificaCultivarDto = new ClassificaCultivarDto();

        List<Cultivar> cultivars = cultivarRepository.findByTipoTerrenoNomeIgnoreCase(nomeTerreno);

        List<CultivarDto> classifica =  cultivars.stream()
                                        .sorted(Comparator.comparingDouble((Cultivar c) -> c.getPhMax() - c.getPhMin())
                                        .reversed()                                         // rovesciato: dalla più ampia alla più stretta
                                        .thenComparing(Cultivar::getNome))                  // spareggio: a pari ampiezza, il nome alfabetico
                                        .map(cultivarMapper::cultivarToCultivarDto)
                                        .toList();

        classificaCultivarDto.setNomeTerreno(tipoTerreno.getNome());
        classificaCultivarDto.setNumeroCultivar((long) cultivars.size());
        classificaCultivarDto.setClassifica(classifica);
        classificaCultivarDto.setSpecie(classifica.stream().map(CultivarDto::getSpecie).distinct().sorted().toList());

        return classificaCultivarDto;
    }

}