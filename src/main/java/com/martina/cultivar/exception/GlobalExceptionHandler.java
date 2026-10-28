package com.martina.cultivar.exception;

import com.martina.cultivar.vivaio.exception.AcquistoNonValidoException;
import com.martina.cultivar.vivaio.exception.PiantaNotFoundException;
import com.martina.cultivar.vivaio.exception.ProdottoNotFoundException;
import com.martina.cultivar.vivaio.exception.ScortaInsufficienteException;
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

    @ExceptionHandler(PiantaNotFoundException.class)
    public ResponseEntity<String> gestisciPiantaNonTrovata(PiantaNotFoundException ex) {
        log.warn("404 - {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ProdottoNotFoundException.class)
    public ResponseEntity<String> gestisciProdottoNonTrovato(ProdottoNotFoundException ex) {
        log.warn("404 - {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }


    @ExceptionHandler(AcquistoNonValidoException.class)
    public ResponseEntity<String> gestisciAcquistoNonValido(AcquistoNonValidoException ex) {
        log.warn("400 - {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ScortaInsufficienteException.class)
    public ResponseEntity<String> gestisciScortaInsufficiente(ScortaInsufficienteException ex) {
        log.warn("409 - {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}