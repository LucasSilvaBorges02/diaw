package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Rendimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record RendimentoDto(
        @NotBlank(message = "informe a entidade empregadora") String entidadeEmpregadora,
        @NotNull(message = "informe o valor") @PositiveOrZero BigDecimal valor) {

    public static RendimentoDto de(Rendimento rendimento) {
        return new RendimentoDto(rendimento.getEntidadeEmpregadora(), rendimento.getValor());
    }

    public Rendimento paraEntidade() {
        return new Rendimento(entidadeEmpregadora.trim(), valor);
    }
}
