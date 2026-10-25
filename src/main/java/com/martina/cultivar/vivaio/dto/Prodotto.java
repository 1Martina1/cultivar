package com.martina.cultivar.vivaio.dto;

public sealed interface Prodotto permits Pianta, Attrezzo, Seme {
    Double prezzo();
}
