package com.martina.cultivar.vivaio.service;

import com.martina.cultivar.entity.Cultivar;
import com.martina.cultivar.vivaio.dto.*;
import com.martina.cultivar.vivaio.exception.AcquistoNonValidoException;
import com.martina.cultivar.vivaio.exception.ProdottoNotFoundException;
import com.martina.cultivar.vivaio.exception.ScortaInsufficienteException;
import com.martina.cultivar.vivaio.repository.CatalogoRepository;
import com.martina.cultivar.vivaio.repository.PiantaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class CatalogoService {
    private final CatalogoRepository catalogoRepository;
    private final PiantaRepository piantaRepository;

    public SchedaProdotto schedaProdotto(Long id) {

        // 1. CERCO IL PRODOTTO
        var prodotto = catalogoRepository.findById(id)                     // var: il tipo (Prodotto) lo deduce Java da destra
                .orElseThrow(() -> new ProdottoNotFoundException("Prodotto non trovato con id: " + id));

        //    Niente default: Prodotto è sealed, Java sa che esistono solo questi tipi.
        //    I case con "when" (più specifici) stanno PRIMA di quelli generici dello stesso tipo.
        String scheda = switch (prodotto) {

            // Pianta con quantità 0: "se è una Pianta E la quantità è 0, chiamala p"
            case Pianta p when p.quantita() == 0 -> """
                    Pianta: %s
                    Tipo: %s
                    Stato: ESAURITA
                    """.formatted(p.nome(), p.tipo());

            // Tutte le altre piante
            // text block: ogni riga del testo è una riga della scheda
            // %s = posto per un testo, %.2f = posto per un numero con 2 decimali, %d = posto per un numero intero
            // .formatted(...) riempie i segnaposto IN ORDINE
            case Pianta p -> """
                    Pianta: %s
                    Tipo: %s
                    Prezzo: %.2f €
                    Disponibili: %d
                    """.formatted(p.nome(), p.tipo(), p.prezzo(), p.quantita());

            case Attrezzo a -> """
                    Attrezzo: %s
                    Prezzo: %.2f €
                    """.formatted(a.nome(), a.prezzo());

            // Seme con bustine 0: RECORD PATTERN
            // apro il record Seme(id, specie, prezzo, bustine) e prendo solo quello che mi serve
            // _ = "questo campo non mi interessa" (id e prezzo)
            case Seme(_, var specie, _, var bustine) when bustine == 0 -> """
                    Semi di %s
                    Stato: NON DISPONIBILI
                    """.formatted(specie);

            // Tutti gli altri semi
            case Seme s -> """
                    Semi di %s
                    Prezzo: %.2f € a bustina
                    Bustine: %d
                    """.formatted(s.specie(), s.prezzo(), s.bustine());
        };                                                                 // punto e virgola: lo switch è un'espressione

        String categoria = switch (prodotto) {
            case Pianta p   -> "PIANTA";
            case Attrezzo a -> "ATTREZZO";
            case Seme s     -> "SEME";
        };

        //    ⚠️ stesso ordine della dichiarazione del record: (id, categoria, scheda)
        return new SchedaProdotto(id, categoria, scheda);
    }

    public PrezzoScontato prezzoScontato(Long id) {
        Prodotto prodotto = catalogoRepository.findById(id)
                .orElseThrow(() -> new ProdottoNotFoundException("Prodotto non trovato con id: " + id));

        String nome = switch (prodotto){
            case Pianta p -> p.nome();
            case Attrezzo a -> a.nome();
            case Seme s     -> "Semi di " + s.specie();
        };

        Double prezzoPieno = prodotto.prezzo();

        Integer percentuale = switch (prodotto) {
            case Pianta p when p.quantita() == 0 -> 0;

            case Pianta p ->
                    switch (p.tipo()) {
                        case "AROMATICA" -> 10;
                        case "FIORE" -> {
                            // case su più righe: graffe + yield per dire "questo è il valore"
                            if (p.quantita() > 10) {
                                yield 25;
                            }
                            yield 20;
                        }
                        case "ALBERO", "ARBUSTO" -> 0;   // due valori nello stesso case
                        default -> 5;                    // qualsiasi altro tipo
                    };

            case Attrezzo a when a.prezzo() > 10 -> 15;
            case Attrezzo a -> 0;

            case Seme s when s.bustine() > 20 -> 30;
            case Seme s -> 0;
        };

        Double prezzoScontato = prezzoPieno * (100 - percentuale) / 100;

        return new PrezzoScontato(id, nome, prezzoPieno, percentuale, prezzoScontato);
    }

    public List<RiepilogoTipoPianta> riepilogoPiantePerTipo() {
        // Tutti i prodotti del catalogo (piante, attrezzi e semi mescolati)
        List<Prodotto> prodotti = catalogoRepository.findAll();

        // Da tutti i prodotti, solo le piante
        List<Pianta> piante = prodotti.stream()                 // passo i prodotti uno alla volta
                .filter(prodotto -> prodotto instanceof Pianta) // tengo solo quelli che sono piante
                .map(prodotto -> (Pianta) prodotto)             // e li "tratto da piante" (cast)
                .toList();                                      // li raccolgo in una lista di Pianta

        // passo 2: raggruppare le piante per tipo
        // Chiave = il tipo, valore = la lista delle piante di quel tipo
        Map<String, List<Pianta>> piantePerTipo = piante.stream()
                .collect(Collectors.groupingBy(Pianta::tipo));   // raggruppa usando il tipo di ogni pianta

        return piantePerTipo.entrySet().stream()             // scorro i cesti uno alla volta
                .map(cesto -> {
                    String tipo = cesto.getKey();             // il tipo di questo cesto
                    List<Pianta> pianteDelTipo = cesto.getValue(); // le piante di questo cesto

                    Long numeroPiante = pianteDelTipo.stream().count();

                    Integer quantitaTotale = pianteDelTipo.stream()
                            .mapToInt(Pianta::quantita)   // da ogni pianta prendo la quantità (un numero int)
                            .sum();                       // e le sommo
                    Double prezzoMedio = pianteDelTipo.stream()
                            .mapToDouble(Pianta::prezzo)  // da ogni pianta prendo il prezzo (un numero con decimali)
                            .average()                    // la media: restituisce un Optional, perché una lista vuota non ha media
                            .orElse(0.0);                 // se la lista fosse vuota -> 0.0
                    String piantaPiuCara = pianteDelTipo.stream()
                            .max(Comparator.comparing(Pianta::prezzo))  // la pianta col prezzo più alto (Optional<Pianta>)
                            .map(Pianta::nome)                           // dalla pianta prendo il nome
                            .orElse("");                                 // se la lista fosse vuota -> stringa vuota
                    List<String> esaurite = pianteDelTipo.stream()
                            .filter(pianta -> pianta.quantita() == 0)    // tengo solo le esaurite
                            .map(Pianta::nome)                           // da ognuna prendo il nome
                            .toList();                                   // li raccolgo in una lista (vuota se nessuna)

                    return new RiepilogoTipoPianta(tipo, numeroPiante, quantitaTotale,
                            prezzoMedio, piantaPiuCara, esaurite);
                })
                .sorted(Comparator.comparing(RiepilogoTipoPianta::getTipo))  // in ordine alfabetico di tipo
                .toList();
    }

    public List<RiepilogoCategoria> riepilogoPerCategoria() {

        // Tutti i prodotti
        List<Prodotto> prodotti = catalogoRepository.findAll();

        // PASSAGGIO 3: COSTRUISCO la tabella (chiave = categoria, valore = i suoi prodotti)
        Map<String, List<Prodotto>> prodottiPerCategoria = prodotti.stream()
                .collect(Collectors.groupingBy(prodotto -> switch (prodotto) {   // lo switch sceglie il cesto
                    case Pianta p   -> "PIANTA";
                    case Attrezzo a -> "ATTREZZO";
                    case Seme s     -> "SEME";
                }));                                    // ← qui finisce l'istruzione: la tabella è pronta

        // PASSAGGIO 4: USO la tabella, riga per riga
        return prodottiPerCategoria.entrySet().stream()               // passo le righe una alla volta
                .map(cesto -> {
                    String categoria = cesto.getKey();                // ← QUI si valorizza categoria
                    List<Prodotto> prodottiDelGruppo = cesto.getValue();

                    Long numeroProdotti = prodottiDelGruppo.stream().count();   // conta i prodotti di QUESTO cesto

                    Double prezzoMedio = prodottiDelGruppo.stream()
                            .mapToDouble(Prodotto::prezzo)   // 1. da ogni prodotto prendo il prezzo (numero con decimali)
                            .average()                       // 2. media: somma tutto e divide per quanti sono
                            .orElse(0.0);                    // 3. apro la scatola (se il cesto fosse vuoto -> 0.0)

                    Double prezzoMinimo = prodottiDelGruppo.stream()
                            .mapToDouble(Prodotto::prezzo)   // 1. da ogni prodotto prendo il prezzo (numero con decimali)
                            .min()
                            .orElse(0.0);                    // 3. apro la scatola (se il cesto fosse vuoto -> 0.0)

                    String prodottoPiuEconomico = prodottiDelGruppo.stream()
                            .min(Comparator.comparing(Prodotto::prezzo))
                            .map(prodotto -> switch (prodotto) {
                                case Pianta p -> p.nome();
                                case Attrezzo a -> a.nome();
                                case Seme s     -> "Semi di " + s.specie();
                            })
                            .orElse("");

                    return new RiepilogoCategoria(categoria, numeroProdotti, prezzoMedio, prezzoMinimo, prodottoPiuEconomico);
                })
                .sorted(Comparator.comparing(RiepilogoCategoria::getNumeroProdotti)   // 1. guarda il numero di prodotti
                        .reversed()                                                   // 2. dal più alto al più basso
                        .thenComparing(RiepilogoCategoria::getCategoria))             // 3. a parità: categoria in ordine alfabetico
                .toList();
    }

    public List<DaRiordinare> prodottiDaRiordinare() {

        List<Prodotto> prodotti = catalogoRepository.findAll();

        List<Prodotto> sottoScorta = prodotti.stream()             // passo i prodotti uno alla volta
                .filter(prodotto -> switch (prodotto) {            // per ogni prodotto: lo tengo?
                    case Pianta p   -> p.quantita() < 5;           // pianta: sì se ne ha meno di 5
                    case Seme s     -> s.bustine() < 10;           // seme: sì se ha meno di 10 bustine
                    case Attrezzo a -> false;                      // attrezzo: mai
                })
                .toList();

        return sottoScorta.stream()              // passo i prodotti sotto scorta
                .map(prodotto -> switch (prodotto) {                        // ognuno diventa un DaRiordinare
                    case Pianta p   -> new DaRiordinare(p.nome(), "PIANTA", p.quantita(), 20 - p.quantita());
                    case Seme s     -> new DaRiordinare("Semi di " + s.specie(), "SEME", s.bustine(), 50 - s.bustine());
                    case Attrezzo a -> throw new IllegalStateException("Un attrezzo non può essere sotto scorta");
                })
                .sorted(Comparator.comparing(DaRiordinare::getQuantitaDaOrdinare)
                        .reversed()
                        .thenComparing(DaRiordinare::getNome))
                .toList();
    }

    public Scontrino acquista(Long id, RichiestaAcquisto richiesta) {

        if(richiesta.getQuantita() <= 0){
            throw new AcquistoNonValidoException("La quantità deve essere maggiore di zero");
        }

        Prodotto prodotto = catalogoRepository.findById(id)
                .orElseThrow(() -> new ProdottoNotFoundException("Prodotto non trovato con id: " + id));

        if (!(prodotto instanceof Pianta pianta)) {
            throw new AcquistoNonValidoException("Si possono acquistare solo piante");
        }

        String nomePianta = pianta.nome();

        if(richiesta.getQuantita() > pianta.quantita()){
            throw new ScortaInsufficienteException("Disponibili solo " + pianta.quantita() + " pezzi di " + nomePianta);
        }

        Integer quantitaAcquistata = richiesta.getQuantita();
        double prezzoUnitario = prodotto.prezzo();
        double totale = prezzoUnitario * quantitaAcquistata;
        Integer quantitaRimasta = pianta.quantita() - quantitaAcquistata;

        // Pianta è un record, quindi immutabile: non possiamo cambiarle la quantità.
        // Creiamo una pianta NUOVA, uguale alla vecchia tranne la quantità.
        // Ordine del costruttore: id, nome, tipo, prezzo, quantita
        Pianta piantaAggiornata = new Pianta(
                pianta.id(),        // stesso id → stesso "cassetto" nella Map
                pianta.nome(),      // stesso nome
                pianta.tipo(),      // stesso tipo
                pianta.prezzo(),    // stesso prezzo
                quantitaRimasta     // unica cosa che cambia: i pezzi rimasti dopo l'acquisto
        );

        // La rimettiamo nel magazzino: put sulla stessa chiave sostituisce la vecchia
        catalogoRepository.salva(piantaAggiornata);

        Scontrino scontrino = new Scontrino();
        scontrino.setNomePianta(nomePianta);
        scontrino.setPrezzoUnitario(prezzoUnitario);
        scontrino.setTotale(totale);
        scontrino.setQuantitaRimasta(quantitaRimasta);
        scontrino.setQuantitaAcquistata(quantitaAcquistata);

        return scontrino;

    }
}
