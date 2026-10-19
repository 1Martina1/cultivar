package com.martina.cultivar.service;


import com.martina.cultivar.PhUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhUtilsTest {
    @Test
        // dice a JUnit: "questo metodo è un test, eseguilo"
    void nelRange_phDentro_restituisceTrue() {
        // Prepara
        double ph = 7.0;

        // Esegui
        boolean risultato = PhUtils.nelRange(ph, 6.0, 8.0);

        // Verifica
        assertTrue(risultato);   // se risultato è false → il test FALLISCE (rosso)
    }

    @Test
    void nelRange_phFuori_restituisceFalse() {
        // Prepara
        double ph = 4.0;

        // Esegui
        boolean risultato = PhUtils.nelRange(ph, 6.0, 8.0);

        // Verifica
        assertFalse(risultato);
    }
}