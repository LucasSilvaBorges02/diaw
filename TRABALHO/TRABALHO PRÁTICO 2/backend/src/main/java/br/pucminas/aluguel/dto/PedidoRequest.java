package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PedidoRequest(
        @NotNull(message = "escolha um automóvel") Long automovelId,
        @NotNull(message = "informe a data de retirada") LocalDate dataInicio,
        @NotNull(message = "informe a data de devolução") LocalDate dataFim) {
}
