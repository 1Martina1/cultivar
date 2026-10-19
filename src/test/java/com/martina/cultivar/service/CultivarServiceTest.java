package com.martina.cultivar.service;

import com.martina.cultivar.client.AgroLabClient;
import com.martina.cultivar.client.dto.AnalisiTerrenoResponse;
import com.martina.cultivar.dto.ClassificaCultivarDto;
import com.martina.cultivar.dto.CultivarDto;
import com.martina.cultivar.dto.RecuperaCultivarPerTerrenoDto;
import com.martina.cultivar.entity.Cultivar;
import com.martina.cultivar.entity.FabbisognoIdrico;
import com.martina.cultivar.entity.TipoTerreno;
import com.martina.cultivar.exception.CultivarNotFoundException;
import com.martina.cultivar.exception.TipoTerrenoNotFoundException;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.martina.cultivar.entity.FabbisognoIdrico.BASSO;
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
        cultivar.setFabbisognoIdrico(FabbisognoIdrico.MEDIO);
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
        cultivar.setFabbisognoIdrico(FabbisognoIdrico.MEDIO);
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
}