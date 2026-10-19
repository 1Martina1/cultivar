package com.martina.cultivar.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TipoTerrenoNotFoundException.class)
    public ResponseEntity<String> gestisciTerrenoNonTrovato(TipoTerrenoNotFoundException ex) {
        log.warn("404 - {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CultivarNotFoundException.class)
    public ResponseEntity<String> gestisciCultivarNonTrovata(CultivarNotFoundException ex) {
        log.warn("404 - {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());

    }

    @ExceptionHandler(PhNonValidoException.class)
    public ResponseEntity<String> gestisciPhNonValido(PhNonValidoException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}