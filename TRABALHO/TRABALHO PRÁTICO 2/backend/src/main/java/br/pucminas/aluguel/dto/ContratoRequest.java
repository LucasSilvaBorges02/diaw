package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.TipoProprietario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** bancoId preenchido = aluguel com contrato de crédito daquele banco. */
public record ContratoRequest(
        @NotNull(message = "informe em nome de quem o carro fica") TipoProprietario tipoProprietario,
        Long bancoId,
        @PositiveOrZero BigDecimal valorCredito) {
}
