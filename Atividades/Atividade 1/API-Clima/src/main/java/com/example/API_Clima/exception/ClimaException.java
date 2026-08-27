package com.example.API_Clima.exception;

public class ClimaException extends RuntimeException {

    public ClimaException(String mensagem) {
        super(mensagem);
    }

    public ClimaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}