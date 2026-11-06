package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Automovel;
import java.math.BigDecimal;

public record AutomovelResponse(Long id, String matricula, Integer ano, String marca, String modelo, String placa,
        BigDecimal valorDiaria) {

    public static AutomovelResponse de(Automovel automovel) {
        return new AutomovelResponse(automovel.getId(), automovel.getMatricula(), automovel.getAno(),
                automovel.getMarca(), automovel.getModelo(), automovel.getPlaca(), automovel.getValorDiaria());
    }
}
