package com.martina.cultivar.exception;

public class NomeCultivarGiaPresente extends RuntimeException{
    public NomeCultivarGiaPresente(String message) {
        super(message);
    }
}