package br.pucminas.aluguel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record AutomovelRequest(
        @NotBlank(message = "informe a matrícula") String matricula,
        @NotNull(message = "informe o ano") @Min(1950) @Max(2100) Integer ano,
        @NotBlank(message = "informe a marca") String marca,
        @NotBlank(message = "informe o modelo") String modelo,
        @NotBlank(message = "informe a placa") String placa,
        @NotNull(message = "informe a diária") @Positive BigDecimal valorDiaria) {
}
