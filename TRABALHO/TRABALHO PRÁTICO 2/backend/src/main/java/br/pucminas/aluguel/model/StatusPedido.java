package br.pucminas.aluguel.model;

public enum StatusPedido {
    PENDENTE,
    APROVADO,
    REPROVADO,
    CANCELADO,
    CONTRATADO;

    public boolean podeSerModificado() {
        return this == PENDENTE;
    }

    public boolean podeSerCancelado() {
        return this == PENDENTE || this == APROVADO;
    }
}
