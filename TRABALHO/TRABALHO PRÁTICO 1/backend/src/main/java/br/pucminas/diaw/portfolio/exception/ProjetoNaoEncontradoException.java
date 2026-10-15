package br.pucminas.diaw.portfolio.exception;

public class ProjetoNaoEncontradoException extends RuntimeException {

    public ProjetoNaoEncontradoException(String id) {
        super("Projeto nao encontrado: " + id);
    }
}
