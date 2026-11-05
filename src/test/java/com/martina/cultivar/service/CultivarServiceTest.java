package com.martina.cultivar.service;

import com.martina.cultivar.client.AgroLabClient;
import com.martina.cultivar.client.dto.AnalisiTerrenoResponse;
import com.martina.cultivar.dto.*;
import com.martina.cultivar.entity.Cultivar;
import com.martina.cultivar.entity.FabbisognoIdrico;
import com.martina.cultivar.entity.TipoTerreno;
import com.martina.cultivar.exception.*;
import com.martina.cultivar.mapper.CultivarMapper;
import com.martina.cultivar.repository.CultivarRepository;
import com.martina.cultivar.repository.TipoTerrenoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.martina.cultivar.entity.FabbisognoIdrico.BASSO;
import static com.martina.cultivar.entity.FabbisognoIdrico.MEDIO;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CultivarServiceTest {

    @Mock
    private CultivarRepository cultivarRepository;

    @Mock
    private TipoTerrenoRepository tipoTerrenoRepository;

    @Mock
    AgroLabClient agroLabClient;

    @InjectMocks
    private CultivarService cultivarService;

    @Spy
    private CultivarMapper cultivarMapper = Mappers.getMapper(CultivarMapper.class);

    @Test
    void dettaglioCultivar_quandoEsiste_restituisceIlDto() {
        // GIVEN: preparo i dati e dico al mock cosa restituire
        TipoTerreno terreno = new TipoTerreno();
        terreno.setNome("ARGILLOSO");

        Cultivar cultivar = new Cultivar();
        cultivar.setNome("Sangiovese");
        cultivar.setSpecie("Vite");
        cultivar.setPhMin(6.5);
        cultivar.setPhMax(8.0);
        cultivar.setFabbisognoIdrico(MEDIO);
        cultivar.setTipoTerreno(terreno);

        when(cultivarRepository.findById(1L)).thenReturn(Optional.of(cultivar));

        // WHEN: chiamo il metodo da testare
        CultivarDto risultato = cultivarService.dettaglioCultivar(1L);

        // THEN: controllo il risultato
        assertEquals("Sangiovese", risultato.getNome());
        assertEquals("Vite", risultato.getSpecie());
        assertEquals("ARGILLOSO", risultato.getTipoTerreno());
        verify(cultivarRepository).findById(1L);
    }

    @Test
    void dettaglioCultivar_quandoNonEsiste_lanciaEccezione() {
        // GIVEN: il repository non trova niente
        when(cultivarRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN: mi aspetto l'eccezione
        assertThrows(CultivarNotFoundException.class,
                () -> cultivarService.dettaglioCultivar(999L));
    }

    @Test
    void recuperaCultivarAdatteAlTerreno_terrenoEsistente_restituisceCultivarEAnalisi() {
        TipoTerreno terreno = new TipoTerreno();
        terreno.setNome("ARGILLOSO");

        when(tipoTerrenoRepository.findByNomeIgnoreCase("ARGILLOSO")).thenReturn(Optional.of(terreno));

        AnalisiTerrenoResponse analisiTerrenoResponse = new AnalisiTerrenoResponse();
        analisiTerrenoResponse.setTerreno("Argilloso");
        analisiTerrenoResponse.setPhMisurato(7.0);
        analisiTerrenoResponse.setUmidita(2.0);
        analisiTerrenoResponse.setDataAnalisi(LocalDate.of(2025, 12, 3));

        when(agroLabClient.recuperaAnalisi("ARGILLOSO")).thenReturn(analisiTerrenoResponse);

        Cultivar cultivar = new Cultivar();
        cultivar.setNome("Sangiovese");
        cultivar.setSpecie("Vite");
        cultivar.setPhMin(1.0);
        cultivar.setPhMax(14.0);
        cultivar.setFabbisognoIdrico(MEDIO);
        cultivar.setTipoTerreno(terreno);
        List<Cultivar> cultivarAdatte = new ArrayList<>();
        cultivarAdatte.add(cultivar);

        when(cultivarRepository.findAdatte("ARGILLOSO", 7.0)).thenReturn(cultivarAdatte);

        RecuperaCultivarPerTerrenoDto risultato = cultivarService.recuperaCultivarAdatteAlTerreno(terreno.getNome());

        assertEquals("ARGILLOSO", risultato.getNomeTerreno());
        assertEquals(7.0, risultato.getPhMisurato());
        assertEquals(LocalDate.of(2025, 12, 3), risultato.getDataAnalisi());
        assertEquals(1, risultato.getCultivarAdatte().size());
        assertEquals("Sangiovese", risultato.getCultivarAdatte().get(0).getNome());
    }

    @Test
    void recuperaCultivarAdatteAlTerreno_terrenoInesistente_lanciaEccezione(){

        String terrenoInesistente = "VULCANICO";

        when(tipoTerrenoRepository.findByNomeIgnoreCase(terrenoInesistente)).thenReturn(Optional.empty());

        assertThrows(TipoTerrenoNotFoundException.class,
                () -> cultivarService.recuperaCultivarAdatteAlTerreno(terrenoInesistente));

        verifyNoInteractions(agroLabClient);
    }

    @Test
    void recuperaLaTolleranzaAlPhDelleCultivar_specieInesistente_lanciaEccezione(){

        String specie = "BANANO";

        when(cultivarRepository.findBySpecieIgnoreCase(specie)).thenReturn(List.of());

        assertThrows(CultivarNotFoundException.class,
                () -> cultivarService.tolleranzaAlPhCultivar(specie));
    }

    @Test
    void profiloCultivarPerTerreno_terrenoInesistente_lanciaEccezione(){

        String terreno = "PIANTOSO";

        when(tipoTerrenoRepository.findByNomeIgnoreCase(terreno)).thenReturn(Optional.empty());

        assertThrows(TipoTerrenoNotFoundException.class,
                () -> cultivarService.profiloCultivarPerTerreno(terreno));

        verifyNoInteractions(cultivarRepository);
    }

    @Test
    void profiloCultivarPerTerreno_terrenoSenzaCultivar_lanciaEccezione() {
        // Prepara: TUTTO il copione, una battuta per ogni repository che il service chiama
        String terreno = "ARGILLOSO";

        TipoTerreno tipoTerreno = new TipoTerreno();
        tipoTerreno.setNome(terreno);

        when(tipoTerrenoRepository.findByNomeIgnoreCase(terreno)).thenReturn(Optional.of(tipoTerreno)); // il terreno c'è
        when(cultivarRepository.findByTipoTerrenoNomeIgnoreCase(terreno)).thenReturn(List.of());         // ma zero cultivar

        // Esegui + Verifica: UNA chiamata, UN'eccezione attesa
        assertThrows(CultivarNotFoundException.class,
                () -> cultivarService.profiloCultivarPerTerreno(terreno));
    }

    @Test
    void cultivarDiUnTerrenoInClassifica_quandoEsiste_restituisceClassificaOrdinata() {
        // PREPARA: il terreno
        String nomeTerreno = "ARGILLOSO";
        TipoTerreno tipoTerreno = new TipoTerreno();
        tipoTerreno.setNome(nomeTerreno);

        // PREPARA: 3 cultivar volutamente in DISORDINE (una riga ciascuna grazie al metodo di aiuto)
        List<Cultivar> cultivars = List.of(
                creaCultivar("Sangiovese", "Vite",  6.5, 8.0, tipoTerreno),   // ampiezza 1.5
                creaCultivar("Leccino",    "Olivo", 5.5, 8.0, tipoTerreno),   // ampiezza 2.5
                creaCultivar("Frantoio",   "Olivo", 6.0, 7.5, tipoTerreno));  // ampiezza 1.5

        // PREPARA: il copione, una battuta per ogni repository
        when(tipoTerrenoRepository.findByNomeIgnoreCase(nomeTerreno)).thenReturn(Optional.of(tipoTerreno)); // il terreno c'è
        when(cultivarRepository.findByTipoTerrenoNomeIgnoreCase(nomeTerreno)).thenReturn(cultivars);         // ecco le 3 cultivar

        // ESEGUI: chiamo il service vero
        ClassificaCultivarDto risultato = cultivarService.cultivarDiUnTerrenoInClassifica(nomeTerreno);

        // VERIFICA: i dati generali
        assertEquals("ARGILLOSO", risultato.getNomeTerreno());
        assertEquals(3L, risultato.getNumeroCultivar());

        // VERIFICA: l'ordine della classifica
        assertEquals("Leccino",    risultato.getClassifica().get(0).getNome());   // 1° → ampiezza più grande (2.5)
        assertEquals("Frantoio",   risultato.getClassifica().get(1).getNome());   // 2° → 1.5, vince lo spareggio (F prima di S)
        assertEquals("Sangiovese", risultato.getClassifica().get(2).getNome());   // 3° → 1.5, perde lo spareggio

        // VERIFICA: le specie, senza doppioni e in ordine alfabetico
        assertEquals(List.of("Olivo", "Vite"), risultato.getSpecie());
    }

    // METODO DI AIUTO: crea una cultivar con i soli campi che servono ai test
    private Cultivar creaCultivar(String nome, String specie, Double phMin, Double phMax, TipoTerreno terreno) {
        Cultivar cultivar = new Cultivar();
        cultivar.setNome(nome);
        cultivar.setSpecie(specie);
        cultivar.setPhMin(phMin);
        cultivar.setPhMax(phMax);
        cultivar.setTipoTerreno(terreno);
        return cultivar;
    }

    @Test
    void creaCultivar_creazioneRiuscita() {

        // ===== GIVEN: preparo i dati =====

        // 1. L'INPUT: quello che il client manda all'API (niente id: lo genera il database)
        CultivarCreaDto cultivarCreaDto = new CultivarCreaDto();
        cultivarCreaDto.setNome("Sangiovese");
        cultivarCreaDto.setSpecie("vite");
        cultivarCreaDto.setPhMin(6.5);
        cultivarCreaDto.setPhMax(8.0);
        cultivarCreaDto.setFabbisognoIdrico(FabbisognoIdrico.MEDIO);
        cultivarCreaDto.setNomeTerreno("ARGILLOSO");

        // 2. LE RISPOSTE DEI MOCK: cosa "risponde il database" quando il service lo interroga
        TipoTerreno terreno = new TipoTerreno();                                       // il terreno che il finto database "trova"
        terreno.setNome("ARGILLOSO");
        when(tipoTerrenoRepository.findByNomeIgnoreCase("ARGILLOSO"))
                .thenReturn(Optional.of(terreno));                                     // il terreno esiste

        when(cultivarRepository.existsByNomeIgnoreCase("Sangiovese"))
                .thenReturn(false);                                                    // nessuna cultivar con questo nome: si può creare

        Cultivar cultivarSalvata = new Cultivar();                                     // la cultivar come la restituirebbe il database dopo il save...
        cultivarSalvata.setId(1L);                                                     // ...CON l'id generato (il mock non ha la sequence, lo metto io)
        cultivarSalvata.setNome("Sangiovese");
        cultivarSalvata.setSpecie("Vite");
        cultivarSalvata.setPhMin(6.5);
        cultivarSalvata.setPhMax(8.0);
        cultivarSalvata.setFabbisognoIdrico(FabbisognoIdrico.MEDIO);
        cultivarSalvata.setTipoTerreno(terreno);                                       // l'OGGETTO terreno, non il suo nome
        when(cultivarRepository.save(any(Cultivar.class)))
                .thenReturn(cultivarSalvata);                                          // "quando il service salva qualunque cultivar, restituisci questa"

        // ===== WHEN: eseguo il MIO codice =====
        CultivarDettaglioDto risultato = cultivarService.creaCultivar(cultivarCreaDto);

        // ===== THEN: controllo il risultato del service =====
        assertEquals(1L, risultato.getId());                                           // l'id arriva dalla cultivar salvata
        assertEquals("Sangiovese", risultato.getNome());
        assertEquals("Vite", risultato.getSpecie());
        assertEquals("ARGILLOSO", risultato.getNomeTerreno());                         // il nome preso dall'oggetto terreno
        assertEquals("6.5 - 8.0", risultato.getIntervalloPh());                        // valore atteso scritto A MANO, non ricalcolato
        assertEquals("MEDIO", risultato.getFabbisognoIdrico());                        // nel DTO è una String, quindi tra virgolette
    }

    @Test
    void creaCultivar_terrenoInesistente() {
        // L'INPUT: basta il terreno, perché il service si ferma al primo controllo
        CultivarCreaDto cultivarCreaDto = new CultivarCreaDto();
        cultivarCreaDto.setNome("Sangiovese");
        cultivarCreaDto.setNomeTerreno("SASSOSO");                                     // un terreno che non esiste

        // UN SOLO MOCK: il database "non trova" il terreno
        when(tipoTerrenoRepository.findByNomeIgnoreCase("SASSOSO"))
                .thenReturn(Optional.empty());                                         // Optional vuoto = "non trovato"

        // ===== WHEN + THEN =====

        // eseguo il service e controllo che lanci l'eccezione giusta
        assertThrows(TipoTerrenoNotFoundException.class,
                () -> cultivarService.creaCultivar(cultivarCreaDto));                  // la lambda "contiene" la chiamata: assertThrows la esegue e cattura l'eccezione

        // controllo che NON sia stato salvato niente nel database
        verify(cultivarRepository, never()).save(any());
    }

    @Test
    void creaCultivar_nomeGiaPresente() {
        CultivarCreaDto cultivarCreaDto = new CultivarCreaDto();
        cultivarCreaDto.setNome("Sangiovese");
        cultivarCreaDto.setNomeTerreno("ARGILLOSO");

        TipoTerreno terreno = new TipoTerreno();
        terreno.setNome("ARGILLOSO");

        when(tipoTerrenoRepository.findByNomeIgnoreCase("ARGILLOSO"))
                .thenReturn(Optional.of(terreno));

        when(cultivarRepository.existsByNomeIgnoreCase("Sangiovese"))
                .thenReturn(true);

        assertThrows(NomeCultivarGiaPresente.class,
                () -> cultivarService.creaCultivar(cultivarCreaDto));
    }

    @Test
    void creaCultivar_phMinMaggioreDelMax() {
        CultivarCreaDto cultivarCreaDto = new CultivarCreaDto();
        cultivarCreaDto.setNome("Sangiovese");
        cultivarCreaDto.setNomeTerreno("ARGILLOSO");
        cultivarCreaDto.setPhMin(8.0);
        cultivarCreaDto.setPhMax(6.0);

        TipoTerreno terreno = new TipoTerreno();
        terreno.setNome("ARGILLOSO");

        when(tipoTerrenoRepository.findByNomeIgnoreCase("ARGILLOSO"))
                .thenReturn(Optional.of(terreno));

        when(cultivarRepository.existsByNomeIgnoreCase("Sangiovese"))
                .thenReturn(false);

        assertThrows(PhNonValidoException.class,
                () -> cultivarService.creaCultivar(cultivarCreaDto));

        verify(cultivarRepository, never()).save(any());
    }

    @Test
    void catalogo_richiestaRiuscita() {
        TipoTerreno terreno1 = new TipoTerreno();                                       // il terreno che il finto database "trova"
        terreno1.setNome("ARGILLOSO");

        Cultivar cultivar1 = new Cultivar();
        cultivar1.setId(1L);
        cultivar1.setNome("Sangiovese");
        cultivar1.setSpecie("Vite");
        cultivar1.setPhMin(6.0);
        cultivar1.setPhMax(8.0);
        cultivar1.setFabbisognoIdrico(FabbisognoIdrico.MEDIO);
        cultivar1.setTipoTerreno(terreno1);

        TipoTerreno terreno2 = new TipoTerreno();
        terreno2.setNome("SABBIOSO");

        Cultivar cultivar2 = new Cultivar();
        cultivar2.setId(2L);
        cultivar2.setNome("Carota Nantese");
        cultivar2.setSpecie("Carota");
        cultivar2.setPhMin(6.0);
        cultivar2.setPhMax(6.8);
        cultivar2.setFabbisognoIdrico(FabbisognoIdrico.MEDIO);
        cultivar2.setTipoTerreno(terreno2);

        Page pagina = new PageImpl<>(List.of(cultivar1, cultivar2), PageRequest.of(0, 2, Sort.by("nome")), 5);

        when(cultivarRepository.findAll(any(Pageable.class))).thenReturn(pagina);

        PaginaCultivarDto risultato = cultivarService.catalogo(null, 0, 2);          // ESEGUO: nessuna specie, pagina 0, da 2

        assertEquals(2, risultato.getConteuto().size());                            // 2 cultivar in questa pagina
        assertEquals(5, risultato.getTotaleElementi());                              // 5 in tutto (lo dice la Page finta)
        assertEquals(3, risultato.getTotalePagine());                                // 5 a gruppi di 2 = 3 pagine
        verify(cultivarRepository, never()).findBySpecieIgnoreCase(any(), any());   // senza specie NON deve usare la query per specie
    }

    @Test
    void catalogo_conSpecie() {

        // ===== GIVEN =====
        TipoTerreno terreno = new TipoTerreno();
        terreno.setNome("ARGILLOSO");

        Cultivar sangiovese = new Cultivar();                                          // una sola cultivar: la "pagina" delle viti
        sangiovese.setId(1L);
        sangiovese.setNome("Sangiovese");
        sangiovese.setSpecie("Vite");
        sangiovese.setPhMin(6.0);
        sangiovese.setPhMax(8.0);
        sangiovese.setFabbisognoIdrico(FabbisognoIdrico.MEDIO);
        sangiovese.setTipoTerreno(terreno);

        Page<Cultivar> pagina = new PageImpl<>(
                List.of(sangiovese), PageRequest.of(0, 10, Sort.by("nome")), 1);      // pagina 0 da 10, 1 elemento in tutto

        when(cultivarRepository.findBySpecieIgnoreCase(eq("Vite"), any(Pageable.class)))   // con any() serve eq() per la stringa
                .thenReturn(pagina);

        // ===== WHEN =====
        PaginaCultivarDto risultato = cultivarService.catalogo("Vite", 0, 10);

        // ===== THEN =====
        assertEquals(1, risultato.getConteuto().size());
        assertEquals("Sangiovese", risultato.getConteuto().get(0).getNome());        // il primo (e unico) elemento della pagina
        assertEquals(1, risultato.getTotaleElementi());
        verify(cultivarRepository, never()).findAll(any(Pageable.class));             // con la specie NON deve usare findAll
    }

    @Test
    void catalogo_specieVuota_usaFindAll() {

        // ===== GIVEN =====
        Page<Cultivar> paginaVuota = new PageImpl<>(
                List.of(), PageRequest.of(0, 10, Sort.by("nome")), 0);                // basta una pagina vuota: qui conta QUALE query viene usata

        when(cultivarRepository.findAll(any(Pageable.class))).thenReturn(paginaVuota);

        // ===== WHEN =====
        PaginaCultivarDto risultato = cultivarService.catalogo("   ", 0, 10);          // specie fatta solo di spazi

        // ===== THEN =====
        assertEquals(0, risultato.getConteuto().size());                             // collezione vuota: 200 con [], niente eccezione
        verify(cultivarRepository, never()).findBySpecieIgnoreCase(any(), any());     // "   " deve valere come "nessuna specie" (isBlank)
    }

    @Test
    void catalogo_paginaNegativa() {
        assertThrows(PaginaNonValidaException.class,
                () -> cultivarService.catalogo(null, -1, 10));                        // pagina -1: errore prima di toccare il database
        verifyNoInteractions(cultivarRepository);                                      // il repository non deve essere stato usato PER NIENTE
    }

    @Test
    void catalogo_dimensioneTroppoGrande() {
        assertThrows(PaginaNonValidaException.class,
                () -> cultivarService.catalogo(null, 0, 51));                         // 51 supera il limite di 50
        verifyNoInteractions(cultivarRepository);
    }
}