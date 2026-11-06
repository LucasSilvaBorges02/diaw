package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequest(
        @NotNull(message = "informe se o pedido foi aprovado") Boolean aprovado,
        @Size(max = 500) String parecer) {
}
