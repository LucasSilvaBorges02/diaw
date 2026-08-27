package com.example.API_Clima.exception;

public class CidadeNaoEncontradaException extends RuntimeException {

    public CidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}