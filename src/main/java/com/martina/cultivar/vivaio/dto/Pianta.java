package com.martina.cultivar.vivaio.dto;


public record Pianta (Long id, String nome, String tipo, Double prezzo, int quantita) implements Prodotto{

}
